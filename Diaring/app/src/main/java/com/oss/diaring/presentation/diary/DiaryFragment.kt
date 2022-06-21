package com.oss.diaring.presentation.diary

import android.Manifest
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.palette.graphics.Palette
import androidx.room.Room
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.oss.diaring.R
import com.oss.diaring.data.database.DiaryDatabase
import com.oss.diaring.data.database.entity.Diary
import com.oss.diaring.databinding.FragmentDiaryBinding
import com.oss.diaring.presentation.base.BaseFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


class DiaryFragment : BaseFragment<FragmentDiaryBinding>(R.layout.fragment_diary){

    private var currentDiaryId: Int = localDateToIntId(LocalDate.now())
    private var currentDiaryLocalDate: LocalDate = LocalDate.now()
    private var currentDiary: Diary? = Diary(localDateToIntId(LocalDate.now()), "", LocalDate.now(), "", listOf(), "", 0, 0, null)
    private var diariesMap: MutableMap<LocalDate, Diary> = mutableMapOf<LocalDate, Diary>()
    private var diariesIndexes: MutableList<Int> = mutableListOf(0)

    // db variable to use Room, created with databaseBuilder
    ////////////////////////////////////////////////////////////////
    private lateinit var db: DiaryDatabase

    // Declare binding variables
    ////////////////////////////////////////////////////////////////
    private var mBinding: FragmentDiaryBinding? = null
    private val diaryMBinding get() = mBinding!!

    // Global variable for managing the diary view page
    ////////////////////////////////////////////////////////////////
    private var currentPageWeather : Int = 0
    private var currentPageEmotion : Int = 3
    private var isImageUploaded : Boolean = false
    private var viewMode = 0
    private var imageUri: Uri? = null
    private var imagePath: String? = null

    // step being created
    ////////////////////////////////////////////////////////////////
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // navArg() to get Arguments from another fragment
        val args: DiaryListFragmentArgs by navArgs()
        // If args are received normally
        if (args.diaryIndex != 0) {
            currentDiaryId = args.diaryIndex
            currentDiaryLocalDate = intIdToLocalDate(currentDiaryId)
        }
        // If args are not received
        if (args.diariesIndexes != null) {
            this.diariesIndexes = args.diariesIndexes!!.toMutableList()
            this.diariesIndexes.sort()
        }

        db = Room.databaseBuilder(requireContext(), DiaryDatabase::class.java, "diary_database.db").build()

        mBinding = FragmentDiaryBinding.inflate(inflater, container, false)

        // listener declaration
        bindViews()

        // Get the diary using DiaryDao's Query function. Input Int and return Diary
        loadDiaryFromDao(currentDiaryLocalDate)
        return diaryMBinding.root
    }

    // After View is created
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        diaryMBinding.diaryEmotionSeekBar.isEnabled = false
        // seekbar android:enabled isn't working on xml, so add this line programmatically
        diaryMBinding.diaryContentText.clearFocus()
    }

    override fun onResume() {
        super.onResume()

        // Transition effect to hide lag due to loading
        ObjectAnimator.ofFloat(diaryMBinding.root, View.ALPHA, -1f,1f).apply {
            duration = 800
            start()
        }

        // The process of loading information and updating the page
        GlobalScope.launch {
            activity?.window?.statusBarColor = resources.getColor(R.color.white)
            delay(300)
            activity?.runOnUiThread { setDiaryPage(currentDiary) }
        }
    }

    // Steps to register various listeners
    ////////////////////////////////////////////////////////
    private fun bindViews() {
        // Return to neutral state when you click on the root screen
        diaryMBinding.root.setOnClickListener {
            hideKeyboard()
            if (viewMode == 2) {
                changeEditingStatus(0)
            }
        }

        // Displays the length of text when editing content
        diaryMBinding.diaryContentText.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                diaryMBinding.diaryContentTextLength.text = getString(R.string.diary_textLength_assist, 0)
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val userInput = diaryMBinding.diaryContentText.text.toString()
                diaryMBinding.diaryContentTextLength.text = getString(R.string.diary_textLength_assist, userInput.length)
            }

            override fun afterTextChanged(s: Editable?) {
                val userInput = diaryMBinding.diaryContentText.text.toString()
                diaryMBinding.diaryContentTextLength.text = getString(R.string.diary_textLength_assist, userInput.length)
            }
        })

        // button to return to list
        diaryMBinding.diaryMinimizeButton.setOnClickListener {
            hideKeyboard()
            returnToDiaryList()
        }

        // Menu button to support change photo, delete photo, delete diary
        diaryMBinding.diaryMenuButton.setOnClickListener {
            val pop = PopupMenu(this.requireContext(), diaryMBinding.diaryMenuButton)//diaryMBinding.diaryHashtagText)

            requireActivity().menuInflater.inflate(R.menu.menu_diary_popup, pop.menu)

            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.changeImage -> {
                        if (viewMode != 1)
                            changeEditingStatus(1)
                        cameraCall()
                    }
                    R.id.deleteImage -> {
                        removeImage()
                        saveDiary()
                        isImageUploaded = false
                        if (viewMode == 1) {
                            diaryMBinding.diaryAddImage.alpha = 1f
                            diaryMBinding.diaryAddImage.isClickable = true
                        }
                    }
                    R.id.deletePage -> {
                        // Confirmation alert to prevent accidental deletion
                        val builder = AlertDialog.Builder(requireContext())
                        builder.setTitle("confirmation message")
                            .setMessage("Are you really deleting? Removal is irreversible")
                            .setPositiveButton("Delete",
                                DialogInterface.OnClickListener { dialog, id ->
//                                    when accept clicked
                                    GlobalScope.launch {
                                        try {
                                            activity?.runOnUiThread {
                                                if (diariesIndexes.contains(currentDiary!!.no)) {
                                                    diariesIndexes.remove(currentDiary!!.no)
                                                }
                                                removeImage()
                                                isImageUploaded = false
                                                currentDiary?.title = ""
                                                currentDiary?.location = ""
                                                currentDiary?.content = ""
                                                currentPageEmotion = 3
                                                currentPageWeather = 0
                                                diaryMBinding.diarySubjectText.setText(R.string.emptyText)
                                                diaryMBinding.diaryPlaceText.setText(R.string.emptyText)
                                                diaryMBinding.diaryContentText.setText(R.string.emptyText)
                                                diaryMBinding.diaryContentTextLength.setText(R.string.diary_textLength_zero)
                                                changeWeather(currentPageWeather)
                                                diaryMBinding.diaryEmotionSeekBar.progress =
                                                    currentPageEmotion
                                                if (viewMode == 1) {
                                                    diaryMBinding.diaryAddImage.alpha = 1f
                                                    diaryMBinding.diaryAddImage.isClickable = true
                                                }
                                            }
                                            db.diaryDao()
                                                .deleteDiary(currentDiary!!.no)
                                            currentDiaryId =
                                                diariesIndexes[diariesIndexes.lastIndex]
                                            delay(150)
                                            activity?.runOnUiThread {
                                                returnToDiaryList()
                                            }

                                        } catch (e: java.lang.Exception) {
                                            Timber.e(e)
                                        }
                                    }
                                })
                            .setNegativeButton("Cancel",
                                DialogInterface.OnClickListener { dialog, id ->
//                                   when cancel clicked
                                })
                            builder.show()
                    }
                }
                false
            }
            pop.show()
        }

        diaryMBinding.diaryAddImage.setOnClickListener {
            if (viewMode == 1) {
                cameraCall()
            }
        }

        // Even if you are not in edit state, press an empty title or place to enter edit state
        diaryMBinding.diarySubjectText.setOnClickListener {
            if (viewMode != 1) {
                if (diaryMBinding.diarySubjectText.text.isEmpty())  {
                    changeEditingStatus(1)
                }
            }
        }
        diaryMBinding.diaryPlaceText.setOnClickListener {
            if (viewMode != 1) {
                if (diaryMBinding.diaryPlaceText.text.isEmpty())  {
                    changeEditingStatus(1)
                }
            }
        }

        diaryMBinding.diaryMainImage.setOnClickListener { // On Progress
            if (viewMode == 0) {
                changeEditingStatus(2)
            }
        }

        // Emotion SeekBar Listeners
        diaryMBinding.diaryEmotionSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                when (p1) {
                    1 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_very_bad_32, null))
                    2 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_bad_32, null))
                    3 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_fine_32, null))
                    4 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_good_32, null))
                    5 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_very_good_32, null))
                }
                currentPageEmotion = p1
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        // Weather Buttons Listeners
        diaryMBinding.diarySnowyButton.setOnClickListener {
            changeWeather(1)
        }
        diaryMBinding.diaryCloudyButton.setOnClickListener {
            changeWeather(2)
        }
        diaryMBinding.diarySunnyButton.setOnClickListener {
            changeWeather(3)
        }
        diaryMBinding.diaryRainyButton.setOnClickListener {
            changeWeather(4)
        }

        // A listener that tracks the date of the diary recorded in a separate list
        // and moves to the next diary page position.
        diaryMBinding.diaryPrevArrow.setOnClickListener {
            var destinationIndex = 0
            var tempFlag = false
            if (diariesIndexes.contains(currentDiaryId)) {
                destinationIndex = diariesIndexes.indexOf(currentDiaryId)
            } else {
                diariesIndexes.add(currentDiaryId)
                diariesIndexes.sort()
                destinationIndex = diariesIndexes.indexOf(currentDiaryId)
                tempFlag = true
            }
            if (tempFlag && diariesIndexes.contains(currentDiaryId)) {
                diariesIndexes.remove(currentDiaryId)
            }
            if (destinationIndex>1) {
                currentDiaryLocalDate = intIdToLocalDate(diariesIndexes[destinationIndex-1])
                currentDiaryId = localDateToIntId(currentDiaryLocalDate)
            }

            loadDiaryFromDao(currentDiaryLocalDate)
            GlobalScope.launch {
                delay(100)
                activity?.runOnUiThread {
                    setDiaryPage(currentDiary)
                }
            }
        }

        diaryMBinding.diaryNextArrow.setOnClickListener {
            var destinationIndex = 0
            var tempFlag = false
            if (diariesIndexes.contains(currentDiaryId)) {
                destinationIndex = diariesIndexes.indexOf(currentDiaryId)
            } else {
                diariesIndexes.add(currentDiaryId)
                diariesIndexes.sort()
                destinationIndex = diariesIndexes.indexOf(currentDiaryId)
                tempFlag = true
            }
            if (tempFlag && diariesIndexes.contains(currentDiaryId)) {
                diariesIndexes.remove(currentDiaryId)
            }
            if (destinationIndex<diariesIndexes.size-1) {
                currentDiaryLocalDate = intIdToLocalDate(diariesIndexes[destinationIndex+1])
                currentDiaryId = localDateToIntId(currentDiaryLocalDate)
            }
            loadDiaryFromDao(currentDiaryLocalDate)
            GlobalScope.launch {
                delay(100)
                activity?.runOnUiThread {
                    setDiaryPage(currentDiary)
                }
            }
        }

        // A button that triggers a state change
        diaryMBinding.diaryPlayButton.setOnClickListener {
            when (viewMode) {
                0 -> { changeEditingStatus(1) }
                1 -> { changeEditingStatus(0) }
                2 -> { changeEditingStatus(1) }
            }
        }
    }

    // Start editing and related commands
    private fun startEdit() {
        diaryMBinding.diarySubjectText.alpha = 1f
        diaryMBinding.diarySubjectText.isFocusableInTouchMode = true
        diaryMBinding.diaryPlaceText.alpha = 1f
        diaryMBinding.diaryPlaceText.isFocusableInTouchMode = true
        diaryMBinding.diarySnowyButton.isClickable = true
        diaryMBinding.diaryCloudyButton.isClickable = true
        diaryMBinding.diarySunnyButton.isClickable = true
        diaryMBinding.diaryRainyButton.isClickable = true
        if (currentPageWeather == 0) {
            diaryMBinding.diarySnowyButton.alpha = 1f
            diaryMBinding.diaryCloudyButton.alpha = 1f
            diaryMBinding.diarySunnyButton.alpha = 1f
            diaryMBinding.diaryRainyButton.alpha = 1f
        }
        diaryMBinding.diaryEmotionSeekBar.isEnabled = true
        diaryMBinding.diaryEmotionSeekBar.alpha = 1.0f
        diaryMBinding.diaryPlayText.text = getString(R.string.diary_save)
        diaryMBinding.diaryPlayButton.setImageResource(R.drawable.ic_play_72)
    }

    // Quit editing and adjust the ui related to it
    private fun endEdit() {
        diaryMBinding.diarySubjectText.alpha = 0.85f
        diaryMBinding.diarySubjectText.isFocusableInTouchMode = false
        diaryMBinding.diaryPlaceText.alpha = 0.85f
        diaryMBinding.diaryPlaceText.isFocusableInTouchMode = false
        diaryMBinding.diarySnowyButton.isClickable = false
        diaryMBinding.diaryCloudyButton.isClickable = false
        diaryMBinding.diarySunnyButton.isClickable = false
        diaryMBinding.diaryRainyButton.isClickable = false
        if (currentPageWeather == 0) {
            diaryMBinding.diarySnowyButton.alpha = 0.75f
            diaryMBinding.diaryCloudyButton.alpha = 0.75f
            diaryMBinding.diarySunnyButton.alpha = 0.75f
            diaryMBinding.diaryRainyButton.alpha = 0.75f
            changeWeather(currentPageWeather)
        }
        diaryMBinding.diaryEmotionSeekBar.isEnabled = false
        diaryMBinding.diaryEmotionSeekBar.alpha = 0.75f
        diaryMBinding.diaryPlayText.text = getString(R.string.diary_edit)
        diaryMBinding.diaryPlayButton.setImageResource(R.drawable.ic_pause_72)
        diaryMBinding.diarySubjectText.clearFocus()
        diaryMBinding.diaryPlaceText.clearFocus()
    }

    // A function that changes the UI in relation to the weather change
    private fun changeWeather(currentWeather : Int) {
        diaryMBinding.diarySnowyButton.setImageResource(R.drawable.ic_snow_24)
        diaryMBinding.diaryCloudyButton.setImageResource(R.drawable.ic_cloud_24)
        diaryMBinding.diarySunnyButton.setImageResource(R.drawable.ic_sun_24)
        diaryMBinding.diaryRainyButton.setImageResource(R.drawable.ic_rain_24)
        diaryMBinding.diarySnowyButton.alpha = 0.5f
        diaryMBinding.diaryCloudyButton.alpha = 0.5f
        diaryMBinding.diarySunnyButton.alpha = 0.5f
        diaryMBinding.diaryRainyButton.alpha = 0.5f

        currentPageWeather = currentWeather
        when (currentWeather) {
            1 -> {
                diaryMBinding.diarySnowyButton.setImageResource(R.drawable.ic_snow_32)
                diaryMBinding.diarySnowyButton.alpha = 1f
            }
            2 -> {
                diaryMBinding.diaryCloudyButton.setImageResource(R.drawable.ic_cloud_32)
                diaryMBinding.diaryCloudyButton.alpha = 1f
            }
            3 -> {
                diaryMBinding.diarySunnyButton.setImageResource(R.drawable.ic_sun_32)
                diaryMBinding.diarySunnyButton.alpha = 1f
            }
            4 -> {
                diaryMBinding.diaryRainyButton.setImageResource(R.drawable.ic_rain_32)
                diaryMBinding.diaryRainyButton.alpha = 1f
            }
            else -> {

            }
        }
    }

    // Adjust the transparency and click relationship of the main image and main text string
    // in relation to the state change
    private fun mainImageAndContentTransparency(state: Int) {
        when (state) {
            0 -> { // normal mode
                diaryMBinding.diaryMainImage.alpha = 1f
                diaryMBinding.diaryMainImage.isClickable = true
                if (!isImageUploaded) {
                    diaryMBinding.diaryAddImage.alpha = 0f
                    diaryMBinding.diaryAddImage.isClickable = true
                    diaryMBinding.diaryAddImage.translationZ = -1f
                }
                diaryMBinding.diaryContentText.translationZ = -1f
                diaryMBinding.diaryContentText.isClickable = false
                diaryMBinding.diaryContentText.isEnabled = false
                diaryMBinding.diaryContentText.alpha = 0f
                diaryMBinding.diaryContentTextLength.alpha = 0f
            }
            1 -> { // edit mode
                diaryMBinding.diaryMainImage.alpha = 0.1f
                diaryMBinding.diaryMainImage.isClickable = true
                if (!isImageUploaded) {
                    diaryMBinding.diaryAddImage.alpha = 1f
                    diaryMBinding.diaryAddImage.isClickable = true
                    diaryMBinding.diaryAddImage.translationZ = 2f
                }
                diaryMBinding.diaryContentText.translationZ = 1f
                diaryMBinding.diaryContentText.isClickable = true
                diaryMBinding.diaryContentText.isEnabled = true
                diaryMBinding.diaryContentText.alpha = 1f
                diaryMBinding.diaryContentText.isFocusableInTouchMode = true
                diaryMBinding.diaryContentTextLength.alpha = 0.5f
            }
            2 -> { // view mode
                diaryMBinding.diaryMainImage.alpha = 0.5f
                diaryMBinding.diaryMainImage.isClickable = false
                diaryMBinding.diaryContentText.isEnabled = true
                diaryMBinding.diaryContentText.isClickable = true
                diaryMBinding.diaryContentText.alpha = 1f
                diaryMBinding.diaryContentText.translationZ = 1f
                diaryMBinding.diaryContentText.isFocusableInTouchMode = false
                diaryMBinding.diaryContentText.clearFocus()
            }
        }
    }

    // page setup of diary
    // Distinguish between diaries in DB and diaries not in DB
    private fun setDiaryPage(cd: Diary?) {
        if (cd != null) {
            var currentIndex = 0
            var tempFlag = false
            if (diariesIndexes.contains(cd.no)) {
                currentIndex = diariesIndexes.indexOf(cd.no)
            } else {
                diariesIndexes.add(cd.no)
                diariesIndexes.sort()
                currentIndex = diariesIndexes.indexOf(cd.no)
                tempFlag = true
            }
            diaryMBinding.diaryDateText.text = localDateToFormattedString(cd.date)
            diaryMBinding.diarySubjectText.setText(cd.title)
            diaryMBinding.diaryPlaceText.setText(cd.location)
            diaryMBinding.diaryContentText.setText(cd.content)
            // It keeps track of whether there is a diary connected to the left or right and the date of the diary.
            var leftDateText = ""
            var rightDateText = ""
            if (diariesIndexes.size==2) {
                if (currentIndex == 0) {
                    rightDateText = "${diariesIndexes[currentIndex + 1] % 10000 / 100}/${diariesIndexes[currentIndex + 1] % 100}"
                } else {
                    leftDateText = "${diariesIndexes[currentIndex - 1] % 10000 / 100}/${diariesIndexes[currentIndex - 1] % 100}"
                }
            } else if (diariesIndexes.size >3) {
                if (currentIndex == 0) {
                    rightDateText = "${diariesIndexes[currentIndex + 1] % 10000 / 100}/${diariesIndexes[currentIndex + 1] % 100}"
                } else if (currentIndex == diariesIndexes.size-1){
                    leftDateText = "${diariesIndexes[currentIndex - 1] % 10000 / 100}/${diariesIndexes[currentIndex - 1] % 100}"
                } else {
                    leftDateText = "${diariesIndexes[currentIndex - 1] % 10000 / 100}/${diariesIndexes[currentIndex - 1] % 100}"
                    rightDateText = "${diariesIndexes[currentIndex + 1] % 10000 / 100}/${diariesIndexes[currentIndex + 1] % 100}"
                }
            }

            diaryMBinding.diaryPrevPageText.text = leftDateText
            diaryMBinding.diaryNextPageText.text = rightDateText

            diaryMBinding.diarySnowyButton.isClickable = false
            diaryMBinding.diaryCloudyButton.isClickable = false
            diaryMBinding.diarySunnyButton.isClickable = false
            diaryMBinding.diaryRainyButton.isClickable = false
            currentPageWeather = cd.weather
            activity?.runOnUiThread { changeWeather(currentPageWeather) }
            currentPageEmotion = cd.emotion

            diaryMBinding.diaryEmotionSeekBar.progress = currentPageEmotion

            if (cd.image != null) {
                diaryMBinding.diaryMainImage.setImageBitmap(cd.image)
                isImageUploaded = true
            } else {
                diaryMBinding.diaryMainImage.setImageResource(R.drawable.ic_diary_default_image)
            }
            setDiaryBackgroundColor()
            if (tempFlag) {
                diariesIndexes.remove(cd.no)
            }
        }
    }

    // A function to save diary contents and INSERT into DB
    private fun saveDiary() {
        try {
            currentDiary?.no = currentDiaryId
            currentDiary?.title = diaryMBinding.diarySubjectText.text.toString()
            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
            currentDiary?.date = LocalDate.parse(diaryMBinding.diaryDateText.text, formatter)
            currentDiary?.location = diaryMBinding.diaryPlaceText.text.toString()
            currentDiary?.hashTagList = listOf("")
            currentDiary?.content = diaryMBinding.diaryContentText.text.toString()
            currentDiary?.weather = currentPageWeather
            currentDiary?.emotion = currentPageEmotion
            if (diaryMBinding.diaryMainImage.drawable != null) {
                currentDiary?.image =
                    diaryMBinding.diaryMainImage.drawable.toBitmap()
                        .copy(Bitmap.Config.RGBA_F16, true)
            }
            if (currentDiary != null) {
                diariesMap[currentDiary!!.date] = currentDiary!!
                GlobalScope.launch {
                    db.diaryDao().insertDiary(currentDiary!!)
                }
                if (!diariesIndexes.contains(currentDiaryId)) {
                    diariesIndexes.add(currentDiaryId)
                }
            }
        } catch (e : java.lang.Exception) {
            Timber.e(e.toString())
        }
    }

    // A function that calls the camera after checking the permission
    private fun cameraCall() {
        val cameraPermission = ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.CAMERA)
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            startCrop()
        } else {
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(android.Manifest.permission.CAMERA), 99)
        }
    }

    // When the requested permissions are returned
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            0 -> {
                if (grantResults.isNotEmpty()){
                    var isAllGranted = true
                    // 요청한 권한 허용/거부 상태 한번에 체크
                    for (grant in grantResults) {
                        if (grant != PackageManager.PERMISSION_GRANTED) {
                            isAllGranted = false
                            break;
                        }
                    }

                    // 요청한 권한을 모두 허용했음.
                    if (isAllGranted) {
                        runDelayed(300) {
                            cameraCall()
                        }
                    }
                    // 허용하지 않은 권한이 있음. 필수권한/선택권한 여부에 따라서 별도 처리를 해주어야 함.
                    else {
                        if(!ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                            || !ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),
                                Manifest.permission.CAMERA)){
                            // 다시 묻지 않기 체크하면서 권한 거부 되었음.
                        } else {
                            // 접근 권한 거부하였음.
                        }
                    }
                }
            }
        }
    }

    // When performing photo cropping
    private fun startCrop() {
        // start picker to get image for cropping and then use the image in cropping activity
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
                setOutputCompressFormat(Bitmap.CompressFormat.JPEG)
                setInitialCropWindowPaddingRatio(0f)
                setAspectRatio(1, 1)
                setFixAspectRatio(true)
                setMinCropWindowSize(40, 40)
            }
        )
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            var uriFilePath = result.getUriFilePath(requireContext()) // optional usage

            var imageFile : File? = createImageFile()

            try {
                val out = FileOutputStream(imageFile)
                result.bitmap?.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
                out.close()

                //갤러리 갱신
                requireContext().sendBroadcast(
                    Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.parse("file://" + Environment.getExternalStorageDirectory())
                    )
                )

            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                removeImage()
                GlobalScope.launch {
                    delay(500)
                    activity?.runOnUiThread {
                        diaryMBinding.diaryMainImage.setImageURI(imageUri)
                        setDiaryBackgroundColor()
                    }
                    saveDiary()
                }
                isImageUploaded = true
                diaryMBinding.diaryAddImage.alpha = 0f
                diaryMBinding.diaryAddImage.isClickable = false
            } catch (e : java.lang.Exception) {
                Timber.e(e.toString())
            }
        }
    }

    // Create image file
    private fun createImageFile(): File? { // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        var imageFile: File? = null
        val storageDir = File(
            Environment.getExternalStorageDirectory().toString() + "/Diaring",
            "Image"
        )
        if (!storageDir.exists()) {
            Timber.i(storageDir.toString())
            storageDir.mkdirs()
        }
        imageFile = File(storageDir, imageFileName)
        imagePath = imageFile.absolutePath
        return imageFile
    }

    private fun saveImageFile() {

    }

    // Set the background and text colors of a toolbar given a
    // bitmap image to match
    private fun setDiaryBackgroundColor() {
        val bitmap = diaryMBinding.diaryMainImage.drawable.toBitmap().copy(Bitmap.Config.RGBA_F16, true)

        if (bitmap != null) {
            Palette.from(bitmap).generate { palette ->
                val dominantColor = palette!!.getDominantColor(ResourcesCompat.getColor(resources, R.color.tone_down_secondary_purple, null))
                val mutedColor = palette!!.getMutedColor(ResourcesCompat.getColor(resources, R.color.tone_down_secondary_purple, null))
                val darkMutedColor = palette.getDarkMutedColor(ResourcesCompat.getColor(resources, R.color.tone_down_primary_blue, null))
//                val darkMutedColor = palette.getDarkVibrantColor(ResourcesCompat.getColor(resources, R.color.primary_blue, null))

                diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(darkMutedColor, darkMutedColor, dominantColor))
                activity?.window?.statusBarColor = darkMutedColor
            }
        }
        Timber.d("color %s", diaryMBinding.root.background)
    }

    // Create a linear gradient drawable object
    private fun linearGradientDrawable(start_color:Int, center_color:Int, end_color:Int):GradientDrawable{
        return GradientDrawable().apply {
            colors = intArrayOf(start_color, center_color, end_color)
            gradientType = GradientDrawable.LINEAR_GRADIENT
            shape = GradientDrawable.RECTANGLE
            orientation = GradientDrawable.Orientation.TOP_BOTTOM

        }
    }

    ////////////////////////////////////////////////////////////////

    private fun removeImage() {
        currentDiary?.image = null
        diaryMBinding.diaryMainImage.setImageResource(R.drawable.ic_diary_default_image)
        setDiaryBackgroundColor()
        diaryMBinding.root.setBackgroundResource(R.drawable.bg_diary_main_background)
        isImageUploaded = false
        if (viewMode == 1) {
            diaryMBinding.diaryAddImage.alpha = 0.8f
            diaryMBinding.diaryAddImage.isClickable = true
        }
    }

    private fun changeEditingStatus(toState : Int) {
        when (toState) {
            0 -> {
                if (viewMode == 1) {
                    if (diaryMBinding.diarySubjectText.text.isNotEmpty()) {
                        viewMode = 0
                        endEdit()
                        saveDiary()
                    }
                    else {
                        Toast.makeText(
                            this.requireContext(),
                            "Title not entered",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } else {
                    viewMode = 0
                }

            }
            1 -> {
                viewMode = 1
                startEdit()
            }
            2 -> {
                viewMode = 2
            }
        }
        mainImageAndContentTransparency(viewMode)
    }

    private lateinit var backPressedCallback : OnBackPressedCallback
    private var doubleBackToExit = false
    fun onBackPressed() {
        if (doubleBackToExit) {
            returnToDiaryList()
        } else {
            if (viewMode == 2) {
                mainImageAndContentTransparency(0)
            } else {
                Toast.makeText(
                    this.requireContext(),
                    "To exit, click Back again",
                    Toast.LENGTH_SHORT
                ).show()
                doubleBackToExit = true
                runDelayed(1500L) {
                    doubleBackToExit = false
                }
            }
        }
    }

    private fun runDelayed(millis: Long, function: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(function, millis)
    }

    private fun hideKeyboard() {
        if (activity != null && activity?.currentFocus != null) {
            val inputManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                requireActivity().currentFocus!!.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    // overloaded query function
    private fun loadDiaryFromDao(id: Int) {
        GlobalScope.launch {
            currentDiary = db.diaryDao().getDiaryById(id)
            if (currentDiary == null)
                currentDiary = Diary(localDateToIntId(currentDiaryLocalDate), "", currentDiaryLocalDate, "", listOf(), "", 0, 0, null)
//            Log.d("diary", currentDiary.toString())
        }
    }

    // It is a commonly used query function that returns Diary. If null is returned,
    // a new Diary is created.
    private fun loadDiaryFromDao(ld: LocalDate) {
        GlobalScope.launch {
            currentDiary = db.diaryDao().getDiaryById(localDateToIntId(ld))
            if (currentDiary == null) {
                currentDiary = Diary(
                    localDateToIntId(ld),
                    "",
                    ld,
                    "",
                    listOf(),
                    "",
                    0,
                    0,
                    null
                )
            }
            Timber.d(currentDiary.toString())
        }
    }

    // full query
    private fun loadAllDiariesFromDao() {
        GlobalScope.launch {
            val crv = db.diaryDao().getAllDiaries()
            diariesMap = crv.map{ it.date to it }.toMap().toMutableMap()
        }
    }

    // Replace LocalDate with Int Id like 20220621
    private fun localDateToIntId(ld: LocalDate): Int {
        return ld.year*10000 + ld.monthValue*100 + ld.dayOfMonth
    }

    private fun intIdToLocalDate(id: Int): LocalDate {
        if (id == 0) return LocalDate.now()
        else return LocalDate.of(id/10000, id%10000/100, id%100)
    }

    private fun localDateToFormattedString(ld: LocalDate): String {
        return "${String.format("%02d", ld.year)}${getString(R.string.plain_year)} ${String.format("%02d", ld.monthValue)}${getString(R.string.monday)} ${String.format("%02d", ld.dayOfMonth)}${getString(R.string.sunday)}"
    }

    private fun returnToDiaryList() {
        val navHostFragment =
            activity?.supportFragmentManager?.findFragmentById(R.id.fcv_main) as NavHostFragment
        val action = DiaryFragmentDirections.actionToDiaryList(diariesIndexes.toIntArray(), currentDiaryId)
        navHostFragment.navController.navigate(action)
        activity?.window?.statusBarColor = resources.getColor(R.color.primary_blue)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // sample_text.text = "occur back pressed event!!"
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onDetach() {
        super.onDetach()
        backPressedCallback.remove()
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}