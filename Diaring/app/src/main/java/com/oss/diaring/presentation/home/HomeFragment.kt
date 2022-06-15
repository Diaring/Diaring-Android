package com.oss.diaring.presentation.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.SimpleItemAnimator
import com.oss.diaring.R
import com.oss.diaring.databinding.FragmentHomeBinding
import com.oss.diaring.presentation.base.BaseFragment
import com.oss.diaring.presentation.home.calendar.EmotionEmojiBottomSheetDialog
import com.oss.diaring.presentation.home.calendar.adapter.CalendarAdapter
import com.oss.diaring.util.DayClickListener
import com.oss.diaring.util.EmojiSwipeListener
import com.oss.diaring.util.MonthSwipeListener
import com.oss.diaring.util.TodayClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(
    R.layout.fragment_home
), DayClickListener, MonthSwipeListener {

    private lateinit var calendarAdapter: CalendarAdapter

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var todayClickListener: TodayClickListener
    private lateinit var emojiSwipeListener: EmojiSwipeListener

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

        bindViews()
        initRecyclerView()
        initObserver()
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
            Toast.makeText(requireActivity(), "T", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onClick(selectedDate: LocalDate) {
        val year = selectedDate.year
        val month = selectedDate.monthValue
        val date = selectedDate.dayOfMonth

        // ViewModel 업데이트
        homeViewModel.updateDate(year, month, date)
    }

    override fun onSwipe() {
        updateViewPager()
    }

    private fun initRecyclerView() {
        calendarAdapter = CalendarAdapter(
            ::todayClickCallback,
            childFragmentManager,
            viewLifecycleOwner.lifecycle
        )

        binding.rvHome.apply {
            adapter = calendarAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    private fun initObserver() {
        homeViewModel.date.observe(viewLifecycleOwner) {
            calendarAdapter.setDate(it)
            updateViewPager()
        }
    }

    private fun todayClickCallback() {
        todayClickListener.onClick()
    }

    fun setCalendarListener(calendarFragment: Fragment) {
        todayClickListener = calendarFragment as TodayClickListener
        emojiSwipeListener = calendarFragment as EmojiSwipeListener
    }

    private fun updateViewPager() {
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            calendarAdapter.notifyItemChanged(0)
        }
        handler.post(runnable)
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