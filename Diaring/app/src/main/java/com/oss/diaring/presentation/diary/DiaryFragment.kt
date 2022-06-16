package com.oss.diaring.presentation.diary

import android.Manifest
import android.content.Context
import android.content.Intent
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
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.palette.graphics.Palette
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.oss.diaring.R
import com.oss.diaring.databinding.FragmentDiaryBinding
import com.oss.diaring.presentation.base.BaseFragment
import com.oss.diaring.presentation.calendar.CalendarFragment
import com.oss.diaring.presentation.main.MainActivity
import com.oss.diaring.util.navigateWithData

class DiaryFragment : BaseFragment<FragmentDiaryBinding>(R.layout.fragment_diary){

    ////////////////////////////////////////////////////////////////
    // Binding to get ids of objects without findViewById(R.id)
    ////////////////////////////////////////////////////////////////
    private var mBinding: FragmentDiaryBinding? = null
    private val diaryMBinding get() = mBinding!!

    private var currentPageWeather : Int = 0
    private var currentPageEmotion : Int = 0
    private var currentPageImageUri : Uri? = null
    private var isImageUploaded : Boolean = false
    private var isEditing : Boolean = false
    private var viewing = 0
    //private var emotionDrawableTypedArray = resources.obtainTypedArray(R.array.emotionDrawableArray)
    private var imageUri: Uri? = null
    private var imagePath: String? = null

    private lateinit var callback: OnBackPressedCallback
    private var doubleBackToExit = false

    fun onBackPressed() {
        if (doubleBackToExit) {
            val navHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.fcv_main) as NavHostFragment
            navHostFragment.navController.navigate(R.id.navigation_diary_list)

        } else {
            Toast.makeText(this.requireContext(), "To exit, click Back again", Toast.LENGTH_SHORT).show()
            doubleBackToExit = true
            runDelayed(1500L) {
                doubleBackToExit = false
            }
        }
    }

    private fun runDelayed(millis: Long, function: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(function, millis)
    }

    ////////////////////////////////////////////////////////////////
    // Initialize DiaryFragment
    ////////////////////////////////////////////////////////////////
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDiaryBinding.inflate(inflater, container, false)

        setDiaryBackgroundColor()//BitmapFactory.decodeResource(resources, R.drawable.img_cafe5))
        diaryMBinding.diaryContentText.clearFocus()

        // seekbar android:enabled isn't working on xml, so add this line programmatically
        diaryMBinding.diaryEmotionSeekBar.isEnabled = false
        diaryMBinding.diaryAddImage.isClickable = false

        bindViews()

        return diaryMBinding.root
    }

    private fun bindViews() {
        diaryMBinding.root.setOnClickListener {
            hideKeyboard()
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
            val navHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.fcv_main) as NavHostFragment
            navHostFragment.navController.navigate(R.id.navigation_diary_list)

            var view1 = view?.parent as ViewGroup
//            var view2 = R.id.bnv_main
            Log.d("id test", view1.id.toString() + " " + R.id.fcv_main)
        }

        diaryMBinding.diaryMenuButton.setOnClickListener {
            val pop = PopupMenu(this.requireContext(), diaryMBinding.diaryMenuButton)//diaryMBinding.diaryHashtagText)

            requireActivity().menuInflater.inflate(R.menu.menu_diary_popup, pop.menu)

            pop.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.changeImage -> {
                        //startCrop()
                        cameraCall()
                    }
                    R.id.deleteImage -> {
                        removeImage()
                    }
                    R.id.deletePage -> {
                        removeImage()
                        diaryMBinding.diarySubjectText.setText(R.string.emptyText)
                        diaryMBinding.diaryPlaceText.setText(R.string.emptyText)
                        diaryMBinding.diaryContentText.setText(R.string.emptyText)
                        diaryMBinding.diaryContentTextLength.setText(R.string.diary_textLength_zero)
                        changeWeather(0)
                        diaryMBinding.diaryEmotionSeekBar.progress = 0
                    }
                }
                false
            }
            pop.show()
        }

        diaryMBinding.diaryAddImage.setOnClickListener {
//            selectCameraOrGallery()
            if (isEditing) {
//                startCrop()
                cameraCall()
            }
        }

        diaryMBinding.diarySubjectText.setOnClickListener {
            if (!isEditing) {
                if (diaryMBinding.diarySubjectText.hint == "제목을 입력하세요")  {
                    changeEditingStatus()
                }
            }

        }

        diaryMBinding.diaryMainImage.setOnClickListener {
            when (viewing) {
                0 -> { // no
                    viewing = 1
                    diaryMBinding.diaryContentText.isClickable = true
                    diaryMBinding.diaryContentText.alpha = 1f
                    diaryMBinding.diaryMainImage.alpha = 0.5f
                    diaryMBinding.diaryContentText.isFocusableInTouchMode = true
                    diaryMBinding.diaryContentText.clearFocus()
                }
                1 -> { // yes
                    viewing = 0
                    diaryMBinding.diaryContentText.isClickable = false
                    diaryMBinding.diaryContentText.alpha = 0f
                    diaryMBinding.diaryMainImage.alpha = 1f
                }
            }
        }

        ////////////////////////////////////////////////////////////////
        // Emotion SeekBar Listeners
        ////////////////////////////////////////////////////////////////
        diaryMBinding.diaryEmotionSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                currentPageEmotion = changeThumb(p1, currentPageEmotion)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {
            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }
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

//        diaryMBinding.diaryPrevArrow.setOnClickListener {

//            val bitmap = diaryMBinding.diaryMainImage.drawable.toBitmap()
//            setDiaryBackgroundColor()//bitmap)
//        }
//        diaryMBinding.diaryNextArrow.setOnClickListener {

//            val bitmap = diaryMBinding.diaryMainImage.drawable.toBitmap()
//            setDiaryBackgroundColor()//bitmap)
//        }

        diaryMBinding.diaryPlayButton.setOnClickListener {
            changeEditingStatus()
        }
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            // use the returned uri
            imageUri = result.uriContent
            var uriFilePath = result.getUriFilePath(requireContext()) // optional usage

            removeImage()
            diaryMBinding.diaryMainImage.setImageURI(imageUri)
            setDiaryBackgroundColor()
            isImageUploaded = true
            diaryMBinding.diaryAddImage.alpha = 0f
            diaryMBinding.diaryAddImage.isClickable = false
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val bitmap =
                diaryMBinding.diaryMainImage.drawable.toBitmap().copy(Bitmap.Config.RGBA_F16, true)

            if (bitmap != null) {
                Palette.from(bitmap).generate { palette ->
                    val mutedColor = palette!!.getMutedColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.black,
                            null
                        )
                    )
                    val darkMutedColor = palette.getDarkMutedColor(
                        ResourcesCompat.getColor(
                            resources,
                            R.color.black,
                            null
                        )
                    )

                    diaryMBinding.root.setBackgroundDrawable(
                        linearGradientDrawable(
                            darkMutedColor,
                            darkMutedColor,
                            mutedColor
                        )
                    )
                    activity?.window?.statusBarColor = darkMutedColor
                }
            }
        }
    }

    private fun linearGradientDrawable(start_color:Int, center_color:Int, end_color:Int):GradientDrawable{
        return GradientDrawable().apply {
            colors = intArrayOf(
                start_color,
                center_color,
                end_color
            )
            gradientType = GradientDrawable.LINEAR_GRADIENT
            shape = GradientDrawable.RECTANGLE
            orientation = GradientDrawable.Orientation.TOP_BOTTOM

        }
    }

    ////////////////////////////////////////////////////////////////
    // Util Function : change int value to dp value
    ////////////////////////////////////////////////////////////////
    private fun removeImage() {
        diaryMBinding.diaryMainImage.setImageResource(R.drawable.ic_diary_default_image)
        setDiaryBackgroundColor()
        diaryMBinding.root.setBackgroundResource(R.drawable.bg_diary_main_background)
        isImageUploaded = false
        if (isEditing) {
            diaryMBinding.diaryAddImage.alpha = 1f
            diaryMBinding.diaryAddImage.isClickable = true
        }
    }

    private fun changeEditingStatus() {
        when (isEditing) { // for testing
            false -> { // while editing
                isEditing = true
                diaryMBinding.diaryPlayText.text = getString(R.string.diary_save)
                diaryMBinding.diaryPlayButton.setImageResource(R.drawable.ic_play_72)
                diaryMBinding.diarySubjectText.alpha = 1f
                diaryMBinding.diaryPlaceText.alpha = 1f
//                diaryMBinding.diaryHashtagText.alpha = 1f
                diaryMBinding.diaryMainImage.alpha = 0.1f
                if (!isImageUploaded) {
                    diaryMBinding.diaryAddImage.alpha = 1f
                    diaryMBinding.diaryAddImage.isClickable = true
                    diaryMBinding.diaryAddImage.translationZ = 2f
                }
                diaryMBinding.diaryContentText.translationZ = 1f
                diaryMBinding.diaryContentText.isClickable = true
                diaryMBinding.diaryContentText.isEnabled = true
                diaryMBinding.diaryContentText.alpha = 1f
                diaryMBinding.diaryContentTextLength.alpha = 0.75f
                diaryMBinding.diarySubjectText.isClickable = true
                diaryMBinding.diarySubjectText.isEnabled = true
                diaryMBinding.diaryPlaceText.isClickable = true
                diaryMBinding.diaryPlaceText.isEnabled = true
//                diaryMBinding.diaryHashtagText.isClickable = true
//                diaryMBinding.diaryHashtagText.isEnabled = true
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
                if (currentPageWeather == 4) {
                    diaryMBinding.diaryPlayButton.setImageResource(R.drawable.ic_save_72)
                }
            }
            true -> { // while playing
                isEditing = false
                diaryMBinding.diaryPlayText.text = getString(R.string.diary_edit)
                diaryMBinding.diaryPlayButton.setImageResource(R.drawable.ic_pause_72)
                diaryMBinding.diarySubjectText.alpha = 0.85f
                diaryMBinding.diaryPlaceText.alpha = 0.85f
//                diaryMBinding.diaryHashtagText.alpha = 0.85f
                diaryMBinding.diaryMainImage.alpha = 1f
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
//                if (diaryMBinding.diarySubjectText.text.toString() != "") {
                diaryMBinding.diarySubjectText.isClickable = false
                diaryMBinding.diarySubjectText.isEnabled = false
//                }
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
                if (currentPageWeather == 4) {
                    diaryMBinding.diaryPlayButton.setImageResource(R.drawable.ic_edit_72)
                    isImageUploaded = true
                    diaryMBinding.diaryAddImage.alpha = 0f
                    diaryMBinding.diaryAddImage.isClickable = false
                }
            }
        }
    }

    ////////////////////////////////////////////////////////////////
    // Util Function : change seekbar thumb
    ////////////////////////////////////////////////////////////////
    private fun changeThumb(progress : Int, prev_emotion : Int) : Int{
        var new_emotion = 0
        when (progress) {
            in -100..-61 -> new_emotion = 1
            in -60..-21 -> new_emotion = 2
            in -20..19 -> new_emotion = 3
            in 20..59 -> new_emotion = 4
            in 60..100 -> new_emotion = 5
        }
        if (new_emotion == prev_emotion) {
            return prev_emotion
        } else {
            // 배열 형태로 바꾸고 싶으나 Drawable 자료형의 배열을 setThumb 함수의 인자로 넣을 경우 오작동하는 경우 발생함
            when (new_emotion) {
                1 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_very_bad_32, null))
                2 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_bad_32, null))
                3 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_fine_32, null))
                4 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_good_32, null))
                5 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_very_good_32, null))
            }
        }
        return new_emotion
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
                diaryMBinding.diarySnowyButton.alpha = 0.75f
            }
            2 -> {
                diaryMBinding.diaryCloudyButton.setImageResource(R.drawable.ic_cloud_32)
                diaryMBinding.diaryCloudyButton.alpha = 0.75f
            }
            3 -> {
                diaryMBinding.diarySunnyButton.setImageResource(R.drawable.ic_sun_32)
                diaryMBinding.diarySunnyButton.alpha = 0.75f
            }
            4 -> {
                diaryMBinding.diaryRainyButton.setImageResource(R.drawable.ic_rain_32)
                diaryMBinding.diaryRainyButton.alpha = 0.75f
            }
            else -> {

            }
        }
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // sample_text.text = "occur back pressed event!!"
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    //    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (resultCode == RESULT_OK && requestCode == 100) {
//            imageUri = data?.data
////            diaryMBinding.diaryMainImage.setImageURI(imageUri)
//
//            data?.data?.let { uri ->
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                    try {
//                        uri?.let {
//                            val decode = ImageDecoder.createSource(
//                                this.requireActivity().contentResolver,
//                                uri
//                            )
//                            val bitmap = ImageDecoder.decodeBitmap(decode)
//                            diaryMBinding.cropImageView.setImageUriAsync(uri)
//                            //diaryMBinding.diaryMainImage.setImageURI(uri)
//                            val cropped: Bitmap = diaryMBinding.cropImageView.getCroppedImage(100,100)!!
//                            diaryMBinding.diaryMainImage.setImageBitmap(bitmap)
//                            setDiaryBackgroundColor()//bitmap)
//                        }
//                    } catch (e: java.lang.Exception) {
//                        e.printStackTrace()
//                    }
//                }
//            }
//
//            // 카메라로 찍은 사진 띄워주기
//            val file = File(imagePath)
//            val selectedUri = Uri.fromFile(file)
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
//                try {
//                    selectedUri?.let {
//                        val decode = ImageDecoder.createSource(
//                            this.requireActivity().contentResolver,
//                            selectedUri
//                        )
//                        val bitmap = ImageDecoder.decodeBitmap(decode)
//                        diaryMBinding.cropImageView.setImageBitmap(bitmap)
//                        val cropped: Bitmap = diaryMBinding.cropImageView.getCroppedImage(100,100)!!
//                        diaryMBinding.diaryMainImage.setImageBitmap(bitmap)
//                        setDiaryBackgroundColor()//bitmap)
//                    }
//                } catch (e: java.lang.Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }

//    private fun selectCameraOrGallery() {
//        val galleryIntent = Intent(Intent.ACTION_PICK)
//        galleryIntent.type = "image/*"
//
//        val chooserIntent = Intent.createChooser(galleryIntent,"")
//        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { captureIntent ->
//            if (captureIntent.resolveActivity(this.requireActivity().packageManager) != null) {
//
//                val photoFile: File? =
//                    try {
//                        createImageFile()  // 파일 생성
//                    } catch (ex: IOException) {
//                        null
//                    }
//
//                photoFile?.also { file ->
//                    val photoURI: Uri = FileProvider.getUriForFile(
//                        this.requireContext(), "com.oss.diaring", file
//                    )
//                    captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
//                    imageUri = photoURI
//                }
//                // 다중 Intent 선택 창에 카메라로 사진찍기 추가
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(captureIntent))
//            }
//        }
//        startActivityForResult(chooserIntent, 100)  // 다중 Intent 선택 창 실행
//
//    }

//    private fun createImageFile(): File {
//        // 사진이 저장될 폴더 있는지 체크
//        val timestamp : String  = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date()) // 이미지 파일 이름
//        val storageDir : File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)  // 스트리지 디렉토리 경로
//
//        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
//            .apply { imagePath = absolutePath}
//    }
}