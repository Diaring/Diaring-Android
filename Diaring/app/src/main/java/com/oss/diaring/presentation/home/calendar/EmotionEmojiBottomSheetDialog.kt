package com.oss.diaring.presentation.home.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oss.diaring.R
import com.oss.diaring.databinding.BottomSheetEmotionEmojiBinding

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