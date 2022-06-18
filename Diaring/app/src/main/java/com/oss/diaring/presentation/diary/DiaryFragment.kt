package com.oss.diaring.presentation.diary

import android.animation.ObjectAnimator
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class DiaryFragment : BaseFragment<FragmentDiaryBinding>(R.layout.fragment_diary){

    private var currentDiaryId: Int = 0
    private var currentDiaryLocalDate: LocalDate = LocalDate.now()
    private var currentDiary: Diary? = Diary(localDateToIntId(LocalDate.now()), "", LocalDate.now(), "", listOf(), "", 0, 0, null)
    private lateinit var diariesMap: MutableMap<LocalDate, Diary>
    private lateinit var diariesIndex: List<Int>

    private lateinit var db: DiaryDatabase
    ////////////////////////////////////////////////////////////////
    // Binding to get ids of objects without findViewById(R.id)
    ////////////////////////////////////////////////////////////////
    private var mBinding: FragmentDiaryBinding? = null
    private val diaryMBinding get() = mBinding!!

    private var currentPageWeather : Int = 0
    private var currentPageEmotion : Int = 3
    private var isImageUploaded : Boolean = false
    private var viewMode = 0
    private var imageUri: Uri? = null

    ////////////////////////////////////////////////////////////////
    // Initialize DiaryFragment
    ////////////////////////////////////////////////////////////////
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val args: DiaryListFragmentArgs by navArgs()
        if (args.diaryLocalData == 0) {
            currentDiaryId = localDateToIntId(java.time.LocalDate.now())
        } else {
            currentDiaryId = localDateToIntId(currentDiaryLocalDate)
        }

//        currentDiary = Diary(currentDiaryId, "", currentDiaryLocalDate, "", listOf(), "", 0, 0, null)

        mBinding = FragmentDiaryBinding.inflate(inflater, container, false)

        bindViews()


        diariesMap = mutableMapOf()
        diariesIndex = listOf()

        db = Room.databaseBuilder(
            requireContext(),
            DiaryDatabase::class.java, "diary_database.db"
        ).build()

        loadDiaryFromDao(currentDiaryLocalDate)
//        loadAllDiariesFromDao()
        return diaryMBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        diaryMBinding.diaryEmotionSeekBar.isEnabled = false
        // seekbar android:enabled isn't working on xml, so add this line programmatically
        diaryMBinding.diaryAddImage.isClickable = false
        diaryMBinding.diaryContentText.clearFocus()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()

        ObjectAnimator.ofFloat(diaryMBinding.root, View.ALPHA, -1f,1f).apply {
            duration = 800
            start()
        }
//        ObjectAnimator.ofFloat(diaryMBinding.diaryFadeInObject, View.ALPHA, 3f,0f).apply {
//            duration = 800
//            start()
//        }

        GlobalScope.launch {
            activity?.window?.statusBarColor = resources.getColor(R.color.white)
            delay(300)
            activity?.runOnUiThread { setDiaryPage(currentDiary) }
        }
    }


    private fun bindViews() {
        diaryMBinding.root.setOnClickListener {
            hideKeyboard()
            if (viewMode == 2) {
                mainImageAndContentTransparency(0)
            }
        }

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

        diaryMBinding.diaryMinimizeButton.setOnClickListener {
            hideKeyboard()

            val navHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.fcv_main) as NavHostFragment
            val action = DiaryFragmentDirections.actionToDiaryList(localDateToIntId(currentDiaryLocalDate))
            navHostFragment.navController.navigate(action)

            activity?.window?.statusBarColor = resources.getColor(R.color.primary_blue)
        }

        diaryMBinding.diaryMenuButton.setOnClickListener {
            val pop = PopupMenu(this.requireContext(), diaryMBinding.diaryMenuButton)//diaryMBinding.diaryHashtagText)

            requireActivity().menuInflater.inflate(R.menu.menu_diary_popup, pop.menu)

            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.changeImage -> {
                        cameraCall()
                    }
                    R.id.deleteImage -> {
                        removeImage()
                        isImageUploaded = false
                        if (viewMode == 1) {
                            diaryMBinding.diaryAddImage.alpha = 1f
                            diaryMBinding.diaryAddImage.isClickable = true
                        }
                    }
                    R.id.deletePage -> {
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
                        diaryMBinding.diaryEmotionSeekBar.progress = currentPageEmotion
                        if (viewMode == 1) {
                            diaryMBinding.diaryAddImage.alpha = 1f
                            diaryMBinding.diaryAddImage.isClickable = true
                        }
                        GlobalScope.launch {
                            db.diaryDao().deleteDiary(0)//localDateToIntId(currentDiaryLocalDate))
                        }
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

        diaryMBinding.diarySubjectText.setOnClickListener { // On Progress
            if (viewMode == 1) {
                if (diaryMBinding.diarySubjectText.hint == "제목을 입력하세요")  {
                    changeEditingStatus()
                }
            }

        }

        diaryMBinding.diaryMainImage.setOnClickListener { // On Progress
            if (viewMode == 0) {
                viewMode = 2
                mainImageAndContentTransparency(viewMode)
            }
        }

        ////////////////////////////////////////////////////////////////
        // Emotion SeekBar Listeners
        ////////////////////////////////////////////////////////////////
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

        ////////////////////////////////////////////////////////////////
        // Weather Buttons Listeners
        ////////////////////////////////////////////////////////////////
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

        diaryMBinding.diaryPrevArrow.setOnClickListener {
            currentDiaryLocalDate = LocalDate.ofYearDay(currentDiaryLocalDate.year, currentDiaryLocalDate.dayOfYear - 1)
            currentDiaryId = localDateToIntId(currentDiaryLocalDate)
            loadDiaryFromDao(currentDiaryLocalDate)
            GlobalScope.launch {
                delay(300)
                activity?.runOnUiThread {
                    setDiaryPage(currentDiary)
                }
            }

        }

        diaryMBinding.diaryNextArrow.setOnClickListener {
            currentDiaryLocalDate = LocalDate.ofYearDay(currentDiaryLocalDate.year,currentDiaryLocalDate.dayOfYear + 1)
            currentDiaryId = localDateToIntId(currentDiaryLocalDate)
            loadDiaryFromDao(currentDiaryLocalDate)
            GlobalScope.launch {
                delay(300)
                activity?.runOnUiThread {
                    setDiaryPage(currentDiary)
                }
            }
        }

        diaryMBinding.diaryPlayButton.setOnClickListener {
            changeEditingStatus()
        }
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            imageUri = result.uriContent
            var uriFilePath = result.getUriFilePath(requireContext()) // optional usage

            removeImage()
            GlobalScope.launch {
                delay(500)
                activity?.runOnUiThread {
                    diaryMBinding.diaryMainImage.setImageURI(imageUri)
                    setDiaryBackgroundColor()
                }
            }
            isImageUploaded = true
            diaryMBinding.diaryAddImage.alpha = 0f
            diaryMBinding.diaryAddImage.isClickable = false
        }
    }

    private fun startEdit() {
        diaryMBinding.diarySubjectText.alpha = 1f
        diaryMBinding.diarySubjectText.isClickable = true
        diaryMBinding.diarySubjectText.isEnabled = true
        diaryMBinding.diaryPlaceText.alpha = 1f
        diaryMBinding.diaryPlaceText.isClickable = true
        diaryMBinding.diaryPlaceText.isEnabled = true
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

    private fun endEdit() {
        diaryMBinding.diarySubjectText.alpha = 0.85f
        diaryMBinding.diarySubjectText.isClickable = false
        diaryMBinding.diarySubjectText.isEnabled = false
        diaryMBinding.diaryPlaceText.alpha = 0.85f
        diaryMBinding.diaryPlaceText.isClickable = false
        diaryMBinding.diaryPlaceText.isEnabled = false
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
    }

    ////////////////////////////////////////////////////////////////
    // Util Function : change weather image's background transparency
    ///////////////////////////////////////////////////////////////
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

    private fun mainImageAndContentTransparency(state: Int) {
        when (state) {
            0 -> { // hide mode
                diaryMBinding.diaryMainImage.alpha = 1f
                diaryMBinding.diaryMainImage.isClickable = true
                if (!isImageUploaded) {
                    diaryMBinding.diaryAddImage.alpha = 0f
                    diaryMBinding.diaryAddImage.isClickable = false
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

    private fun setDiaryPage(cd: Diary?) {
        Log.d("testcode", cd.toString())
        if (cd != null) {
            Log.d("testcode", cd.toString())
            diaryMBinding.diaryDateText.text = localDateToFormattedString(cd.date)
            diaryMBinding.diarySubjectText.setText(cd.title)
            diaryMBinding.diaryPlaceText.setText(cd.location)
            diaryMBinding.diaryContentText.setText(cd.content)
            diaryMBinding.diaryPrevPageText.text = "${currentDiaryLocalDate.monthValue}/${currentDiaryLocalDate.dayOfMonth-1}"
            diaryMBinding.diaryNextPageText.text = "${currentDiaryLocalDate.monthValue}/${currentDiaryLocalDate.dayOfMonth+1}"
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
        }
    }

    private fun saveDiary() {
        currentDiary?.no = currentDiaryId
        currentDiary?.title = diaryMBinding.diarySubjectText.text.toString()
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
        currentDiary?.date = LocalDate.parse(diaryMBinding.diaryDateText.text, formatter)
        currentDiary?.location = diaryMBinding.diaryPlaceText.text.toString()
        currentDiary?.hashTagList = listOf("")
        currentDiary?.content = diaryMBinding.diaryContentText.text.toString()
        currentDiary?.weather = currentPageWeather
        currentDiary?.emotion = currentPageEmotion
        currentDiary?.image = diaryMBinding.diaryMainImage.drawable.toBitmap().copy(Bitmap.Config.RGBA_F16, true)

        if (currentDiary != null) {
            diariesMap.put(currentDiary!!.date!!, currentDiary!!)
            GlobalScope.launch {
                db.diaryDao().insertDiary(currentDiary!!)
            }
        }
    }

    private fun cameraCall() {
        // 1. 위험권한(Camera) 권한 승인상태 가져오기
        val cameraPermission = ContextCompat.checkSelfPermission(this.requireContext(), android.Manifest.permission.CAMERA)
        if (cameraPermission == PackageManager.PERMISSION_GRANTED) {
            // 카메라 권한이 승인된 상태일 경우
            startCrop()
        } else {
            // 카메라 권한이 승인되지 않았을 경우
            //requestPermission()
            ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(android.Manifest.permission.CAMERA), 99)
            startCrop()
        }
    }

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

    // Set the background and text colors of a toolbar given a
    // bitmap image to match
    private fun setDiaryBackgroundColor() {
            val bitmap = diaryMBinding.diaryMainImage.drawable.toBitmap().copy(Bitmap.Config.RGBA_F16, true)

            if (bitmap != null) {
                Palette.from(bitmap).generate { palette ->
                    val mutedColor = palette!!.getMutedColor(ResourcesCompat.getColor(resources, R.color.black, null))
                    val darkMutedColor = palette.getDarkMutedColor(ResourcesCompat.getColor(resources, R.color.black, null))

                    diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(darkMutedColor, darkMutedColor, mutedColor))
                    activity?.window?.statusBarColor = darkMutedColor
                }
            }
    }

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
            diaryMBinding.diaryAddImage.alpha = 1f
            diaryMBinding.diaryAddImage.isClickable = true
        }
    }

    private fun changeEditingStatus() {
        when (viewMode) {
            0 -> { // while editing
                viewMode = 1
                startEdit()
            }
            1 -> { // while playing
                viewMode = 0
                endEdit()
                saveDiary()
            }
            2 -> {
                viewMode = 1
                startEdit()
            }
        }
        mainImageAndContentTransparency(viewMode)
    }

    private lateinit var backPressedcallback : OnBackPressedCallback
    private var doubleBackToExit = false
    fun onBackPressed() {
        if (doubleBackToExit) {
            val navHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.fcv_main) as NavHostFragment
            navHostFragment.navController.navigate(R.id.navigation_diary_list)
            activity?.window?.statusBarColor = resources.getColor(R.color.primary_blue)
        } else {
            if (viewMode == 2) {
                mainImageAndContentTransparency(0)
            }
            Toast.makeText(this.requireContext(), "To exit, click Back again", Toast.LENGTH_SHORT).show()
            Log.d("test", currentDiary.toString())

            setDiaryPage(currentDiary)
            doubleBackToExit = true
            runDelayed(1500L) {
                doubleBackToExit = false
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

    private fun loadDiaryFromDao(id: Int) {
        GlobalScope.launch {
            currentDiary = db.diaryDao().getDiaryById(id)
            if (currentDiary == null)
                currentDiary = Diary(localDateToIntId(currentDiaryLocalDate), "", currentDiaryLocalDate, "", listOf(), "", 0, 0, null)
//            Log.d("diary", currentDiary.toString())
        }
    }

    private fun loadDiaryFromDao(ld: LocalDate) {
        GlobalScope.launch {
            currentDiary = db.diaryDao().getDiaryById(localDateToIntId(ld))
            Log.d("cD", currentDiary.toString())
            if (currentDiary == null) {
                currentDiary = Diary(localDateToIntId(currentDiaryLocalDate), "", currentDiaryLocalDate, "", listOf(), "", 0, 0, null)
            }
//            Log.d("diary", currentDiary.toString())
        }
    }

    private fun loadAllDiariesFromDao() {
        GlobalScope.launch {
            val crv = db.diaryDao().getAllDiaries()
            diariesMap = crv.map{ it.date to it }.toMap().toMutableMap()
        }
    }

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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backPressedcallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // sample_text.text = "occur back pressed event!!"
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedcallback)
    }

    override fun onDetach() {
        super.onDetach()
        backPressedcallback.remove()
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}