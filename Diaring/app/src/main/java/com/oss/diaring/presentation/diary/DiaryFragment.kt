package com.oss.diaring.presentation.diary

import android.R.attr.bitmap
import android.graphics.*
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import com.oss.diaring.R
import com.oss.diaring.databinding.FragmentDiaryBinding
import com.oss.diaring.presentation.base.BaseFragment


class DiaryFragment : BaseFragment<FragmentDiaryBinding>(R.layout.fragment_diary){

    ////////////////////////////////////////////////////////////////
    // Binding to get ids of objects without findViewById(R.id)
    ////////////////////////////////////////////////////////////////
    private var mBinding: FragmentDiaryBinding? = null
    private val diaryMBinding get() = mBinding!!

    private var weather : Int = 1
    private var emotion : Int = 0
    private var currentColor = 1 // for testing
    private var currentImage = 5 // for testing
    private var editting = 0 // for testing
    private var viewing = 0
    //private var emotionDrawbleTypedArray = resources.obtainTypedArray(R.array.emotionDrawableArray)

    ////////////////////////////////////////////////////////////////
    // Initialize DiaryFragment
    ////////////////////////////////////////////////////////////////
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentDiaryBinding.inflate(inflater, container, false)

        //activity?.window?.statusBarColor = ResourcesCompat.getColor(resources, R.color.gradient_start, null)

        setDiaryBackgroundColor(BitmapFactory.decodeResource(resources, R.drawable.img_cafe5))
        diaryMBinding.diaryContentText.clearFocus()

        ////////////////////////////////////////////////////////////////
        // Emotion SeekBar Progress TextView Dynamic Positioning
        ////////////////////////////////////////////////////////////////
        val layoutParams = ConstraintLayout.LayoutParams(
            ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
        //diaryMBinding.diaryEmotionSeekBarTextview.layoutParams = layoutParams

        ////////////////////////////////////////////////////////////////
        // Emotion SeekBar Listeners
        ////////////////////////////////////////////////////////////////
        diaryMBinding.diaryEmotionSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                diaryMBinding.diaryEmotionSeekBarTextview.text = "$p1"

                if (weather == 2 ) emotion = changeThumb(p1, emotion)
                //layoutParams.setMargins(changeDP(p1), changeDP(400), 0, 0)
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
            // Temp
            weather = 1
            diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_circle_18, null))
            diaryMBinding.diaryVeryBad.visibility = View.VISIBLE
            diaryMBinding.diaryVeryGood.visibility = View.VISIBLE
            //
        }
        diaryMBinding.diaryCloudyButton.setOnClickListener {
            changeWeather(2)
            // Temp
            weather = 2
            diaryMBinding.diaryVeryBad.visibility = View.INVISIBLE
            diaryMBinding.diaryVeryGood.visibility = View.INVISIBLE
            //
        }
        diaryMBinding.diarySunnyButton.setOnClickListener {
            changeWeather(3)
            val bitmap = diaryMBinding.diaryMainImage.getDrawable().toBitmap()
            setDiaryBackgroundColor(bitmap)
            diaryMBinding.diaryHashtagText.setText("image : $currentImage, color : $currentColor")
        }
        diaryMBinding.diaryRainyButton.setOnClickListener {
            changeWeather(4)
            //Toast.makeText(activity, "Test", Toast.LENGTH_LONG).show()
        }

        diaryMBinding.diaryLeftArrowButton.setOnClickListener {
            when (currentImage) { // for testing
                1 -> {
                }
                2 -> {
                    currentImage = 1
                    diaryMBinding.diaryMainImage.setImageResource(R.drawable.img_cafe1)
                }
                3 -> {
                    currentImage = 2
                    diaryMBinding.diaryMainImage.setImageResource(R.drawable.img_cafe2)
                }
                4 -> {
                    currentImage = 3
                    diaryMBinding.diaryMainImage.setImageResource(R.drawable.img_cafe3)
                }
                5 -> {
                    currentImage = 4
                    diaryMBinding.diaryMainImage.setImageResource(R.drawable.img_cafe4)
                }
            }
            val bitmap = diaryMBinding.diaryMainImage.getDrawable().toBitmap()
            if (currentColor > 1) currentColor -= 1
            setDiaryBackgroundColor(bitmap)
            diaryMBinding.diaryHashtagText.setText("image : $currentImage, color : $currentColor")
        }
        diaryMBinding.diaryRightArrowButton.setOnClickListener {
            when (currentImage) { // for testing
                1 -> {
                    currentImage = 2
                    diaryMBinding.diaryMainImage.setImageResource(R.drawable.img_cafe2)
                }
                2 -> {
                    currentImage = 3
                    diaryMBinding.diaryMainImage.setImageResource(R.drawable.img_cafe3)
                }
                3 -> {
                    currentImage = 4
                    diaryMBinding.diaryMainImage.setImageResource(R.drawable.img_cafe4)
                }
                4 -> {
                    currentImage = 5
                    diaryMBinding.diaryMainImage.setImageResource(R.drawable.img_cafe5)
                }
                5 -> {
                }
            }
            val bitmap = diaryMBinding.diaryMainImage.getDrawable().toBitmap()
            if (currentColor > 1) currentColor -= 1
            setDiaryBackgroundColor(bitmap)
            diaryMBinding.diaryHashtagText.setText("image : $currentImage, color : $currentColor")
        }

        diaryMBinding.diaryPlayButton.setOnClickListener {
            when (editting) { // for testing
                0 -> { // while editting
                    editting = 1
                    diaryMBinding.diaryPlayText.text = "SAVE"
                    diaryMBinding.diaryPlayButton.setImageResource(R.drawable.ic_play_24)
                    diaryMBinding.diaryMainImage.alpha = 0.2f
                    diaryMBinding.diaryContentText.translationZ = 1f
                    diaryMBinding.diaryContentText.isClickable = true
                    diaryMBinding.diaryContentText.isEnabled = true
                    diaryMBinding.diaryContentText.alpha = 1f
                    diaryMBinding.diarySubjectText.isClickable = true
                    diaryMBinding.diarySubjectText.isEnabled = true
                    diaryMBinding.diaryPlaceText.isClickable = true
                    diaryMBinding.diaryPlaceText.isEnabled = true
                    diaryMBinding.diaryHashtagText.isClickable = true
                    diaryMBinding.diaryHashtagText.isEnabled = true

                }
                1 -> { // while playing
                    editting = 0
                    diaryMBinding.diaryPlayText.text = "EDIT"
                    diaryMBinding.diaryPlayButton.setImageResource(R.drawable.ic_pause_24)
                    diaryMBinding.diaryMainImage.alpha = 1f
                    diaryMBinding.diaryContentText.translationZ = -1f
                    diaryMBinding.diaryContentText.isClickable = false
                    diaryMBinding.diaryContentText.isEnabled = false
                    diaryMBinding.diaryContentText.alpha = 0f
                    diaryMBinding.diarySubjectText.isClickable = false
                    diaryMBinding.diarySubjectText.isEnabled = false
                    diaryMBinding.diaryPlaceText.isClickable = false
                    diaryMBinding.diaryPlaceText.isEnabled = false
                    diaryMBinding.diaryHashtagText.isClickable = false
                    diaryMBinding.diaryHashtagText.isEnabled = false
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

        }

        return diaryMBinding.root
    }


    // Generate palette synchronously and return it

    fun createPaletteSync(bitmap: Bitmap): Palette = Palette.from(bitmap).generate()

    // Generate palette asynchronously and use it on a different
    // thread using onGenerated()
    fun createPaletteAsync(bitmap: Bitmap) {
        Palette.from(bitmap).generate { palette ->
            // Use generated instance
        }
    }

    // Set the background and text colors of a toolbar given a
    // bitmap image to match
    private fun setDiaryBackgroundColor(bitmap: Bitmap) {
        // Generate the palette and get the vibrant swatch
        //val vibrantSwatch = createPaletteSync(bitmap).vibrantSwatch

        // Set the toolbar background and text colors.
        // Fall back to default colors if the vibrant swatch is not available.

        //val bitmap = BitmapFactory.decodeResource(resources, diaryMBinding.diaryMainImage.id)
        val bitmap = diaryMBinding.diaryMainImage.getDrawable().toBitmap()
        Palette.from(bitmap).generate { palette ->
            val lightVibrantColor = palette!!.getLightVibrantColor(ResourcesCompat.getColor(resources, R.color.black, null))
            val vibrantColor = palette!!.getVibrantColor(ResourcesCompat.getColor(resources, R.color.black, null))
            val darkVibrantColor = palette!!.getDarkVibrantColor(ResourcesCompat.getColor(resources, R.color.black, null))
            val lightMutedColor = palette!!.getLightMutedColor(ResourcesCompat.getColor(resources, R.color.black, null))
            val mutedColor = palette!!.getMutedColor(ResourcesCompat.getColor(resources, R.color.black, null))
            val darkMutedColor = palette!!.getDarkMutedColor(ResourcesCompat.getColor(resources, R.color.black, null))

            //Log.d("myColor", Integer.toHexString(darkVibrantColor))
            //Log.d("myColor", Integer.toHexString(R.color.gradient_start))
            //Log.d("myColor", Integer.toHexString(ResourcesCompat.getColor(resources, R.color.gradient_center, null)))

            when (currentColor) { // for testing
                1 -> { // dv, v, lv
                    currentColor = 2
                    diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(darkVibrantColor, vibrantColor, lightVibrantColor))
                    activity?.window?.statusBarColor = darkVibrantColor
                }
                2 -> { // lv, v, dv
                    currentColor = 3
                    diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(lightVibrantColor, vibrantColor, darkVibrantColor))
                    activity?.window?.statusBarColor = lightVibrantColor
                }

                3 -> { // dv, v, v
                    currentColor = 4
                    diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(darkVibrantColor, vibrantColor, vibrantColor))
                    activity?.window?.statusBarColor = darkVibrantColor
                }
                4 -> { // v, v, dv
                    currentColor = 5
                    diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(vibrantColor, vibrantColor, darkVibrantColor))
                    activity?.window?.statusBarColor = vibrantColor
                }

                5 -> { // lv, v, v
                    currentColor = 6
                    diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(lightVibrantColor, vibrantColor, vibrantColor))
                    activity?.window?.statusBarColor = lightVibrantColor
                }
                6 -> { // v, v, lv
                    currentColor = 7
                    diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(vibrantColor, vibrantColor, lightVibrantColor))
                    activity?.window?.statusBarColor = vibrantColor
                }

                7 -> { // dm, m, lm
                    currentColor = 8
                    diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(darkMutedColor, mutedColor, lightMutedColor))
                    activity?.window?.statusBarColor = darkMutedColor
                }
                8 -> { // lm, m, dm
                    currentColor = 9
                    diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(lightMutedColor, mutedColor, darkMutedColor))
                    activity?.window?.statusBarColor = lightMutedColor
                }

                9 -> { // dm, dm, m
                    currentColor = 10
                    diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(darkMutedColor, darkMutedColor, mutedColor))
                    activity?.window?.statusBarColor = darkMutedColor
                }
                10 -> { // m, dm, dm
                    currentColor = 11
                    diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(mutedColor, darkMutedColor, darkMutedColor))
                    activity?.window?.statusBarColor = mutedColor
                }

                11 -> { // dm, v, lv
                    currentColor = 12
                    diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(darkMutedColor, vibrantColor, lightVibrantColor))
                    activity?.window?.statusBarColor = darkMutedColor
                }
                12 -> { // dv, m, dm
                    currentColor = 13
                    diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(darkVibrantColor, mutedColor, darkMutedColor))
                    activity?.window?.statusBarColor = darkVibrantColor
                }
                else -> {
                    currentColor = 1
                    diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(darkVibrantColor, vibrantColor, lightVibrantColor))
                    activity?.window?.statusBarColor = darkVibrantColor
                }
            }
            //activity?.window?.statusBarColor = darkVibrantColor        // @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
            //diaryMBinding.root.setBackgroundDrawable(linearGradientDrawable(darkVibrantColor, darkMutedColor, ResourcesCompat.getColor(resources, R.color.gradient_end,null)))


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
    private fun changeDP(value : Int) : Int{
        var displayMetrics = resources.displayMetrics
        var dp = Math.round(value * displayMetrics.density)
        return dp
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
                1 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_very_bad_24, null))
                2 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_bad_24, null))
                3 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_fine_24, null))
                4 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_good_24, null))
                5 -> diaryMBinding.diaryEmotionSeekBar.setThumb(ResourcesCompat.getDrawable(resources, R.drawable.ic_very_good_24, null))
            }
        }
        return new_emotion
    }

    ////////////////////////////////////////////////////////////////
    // Util Function : change weather image's background transparency
    ////////////////////////////////////////////////////////////////
    private fun changeWeather(currentWeather : Int) {
        // 추후 현재 일기의 weather 상태를 저장하는 변수 생성 시 간략화 가능하나 호출횟수 자체가 적어 overhead가 높지 않으므로 우선순위는 낮음
        diaryMBinding.diarySnowyButton.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparency_0, null))
        diaryMBinding.diaryCloudyButton.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparency_0, null))
        diaryMBinding.diarySunnyButton.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparency_0, null))
        diaryMBinding.diaryRainyButton.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparency_0, null))
        when (currentWeather) {
            // 추후 가독성을 위해 strings.xml에 정의된 TypedArray 형태로 변경할 예정
            1 -> diaryMBinding.diarySnowyButton.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparency_25, null))
            2 -> diaryMBinding.diaryCloudyButton.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparency_25, null))
            3 -> diaryMBinding.diarySunnyButton.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparency_25, null))
            4 -> diaryMBinding.diaryRainyButton.setBackgroundColor(ResourcesCompat.getColor(resources, R.color.transparency_25, null))
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}