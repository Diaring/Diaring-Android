package com.oss.diaring.presentation.home.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oss.diaring.R
import com.oss.diaring.databinding.BottomSheetEmotionEmojiBinding

// Home Fragment에서 감정 이모지 정보와 일기 작성을 다이얼로그 형태로 보이기 위한 Fragment 클래스
class EmotionEmojiBottomSheetDialog : BottomSheetDialogFragment() {

    private var _binding: BottomSheetEmotionEmojiBinding? = null
    val binding get() = requireNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_emotion_emoji, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnClose.setOnClickListener {
            dialog?.dismiss()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}