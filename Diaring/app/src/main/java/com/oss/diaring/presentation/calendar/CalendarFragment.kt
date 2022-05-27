package com.oss.diaring.presentation.calendar

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.oss.diaring.DiaringApplication
import com.oss.diaring.R
import com.oss.diaring.databinding.FragmentCalendarBinding
import com.oss.diaring.presentation.base.BaseFragment
import com.oss.diaring.presentation.calendar.adapter.MonthAdapter

class CalendarFragment : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    private val monthAdapter: MonthAdapter by lazy { MonthAdapter() }
    private val pagerSnapHelper: PagerSnapHelper by lazy { PagerSnapHelper() }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireActivity(),
            R.anim.fab_from_bottom
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireActivity(),
            R.anim.fab_to_bottom
        )
    }
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireActivity(),
            R.anim.fab_rotate_open
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireActivity(),
            R.anim.fab_rotate_close
        )
    }

    private var isFabClicked: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCalendarRecyclerView()
        bindViews()
    }

    private fun initCalendarRecyclerView() {
        val monthLayoutManager =
            LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

        binding.rvCalendar.apply {
            layoutManager = monthLayoutManager
            adapter = monthAdapter
            scrollToPosition(Int.MAX_VALUE / 2)
        }

        pagerSnapHelper.attachToRecyclerView(binding.rvCalendar)
    }

    private fun bindViews() {
        binding.fabExpand.setOnClickListener {
            setFabVisibility(isFabClicked)
            setFabAnimation(isFabClicked)

            isFabClicked = !isFabClicked
        }

        binding.fabInfo.setOnClickListener {
            // Bottom Sheet
            val emotionEmojiBottomSheetDialog = EmotionEmojiBottomSheetDialog()
            emotionEmojiBottomSheetDialog.show(
                childFragmentManager,
                emotionEmojiBottomSheetDialog.tag
            )
        }

        binding.fabCreate.setOnClickListener {
            // 일기 생성 Fragment 이동
            Toast.makeText(DiaringApplication.appContext, "T", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setFabAnimation(isFabClicked: Boolean) {
        if (!isFabClicked) {
            binding.fabInfo.startAnimation(fromBottom)
            binding.fabCreate.startAnimation(fromBottom)
            binding.fabExpand.startAnimation(rotateOpen)
        } else {
            binding.fabInfo.startAnimation(toBottom)
            binding.fabCreate.startAnimation(toBottom)
            binding.fabExpand.startAnimation(rotateClose)

            toBottom.fillAfter = false  // View.GONE 상태에서 FAB 클릭 가능한 이슈 해결
            rotateClose.fillAfter = false

        }
    }

    private fun setFabVisibility(isFabClicked: Boolean) {
        if (!isFabClicked) {
            binding.fabCreate.visibility = View.VISIBLE
            binding.fabInfo.visibility = View.VISIBLE
        } else {
            binding.fabCreate.visibility = View.GONE
            binding.fabInfo.visibility = View.GONE
        }
    }
}