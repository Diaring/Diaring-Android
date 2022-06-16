package com.oss.diaring.presentation.home.calendar

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oss.diaring.R
import com.oss.diaring.databinding.FragmentCalendarBinding
import com.oss.diaring.presentation.base.BaseFragment
import com.oss.diaring.presentation.home.HomeFragment
import com.oss.diaring.presentation.home.calendar.adapter.CalendarDayAdapter
import com.oss.diaring.util.DayClickListener
import com.oss.diaring.util.EmojiSwipeListener
import com.oss.diaring.util.MonthSwipeListener
import com.oss.diaring.util.TodayClickListener
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class CalendarFragment : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar),
    TodayClickListener, EmojiSwipeListener {

    private lateinit var viewPool: RecyclerView.RecycledViewPool

    private val viewModel: CalendarViewModel by viewModels()

    private val calendarDayAdapter =  CalendarDayAdapter(::dayClickCallback)
    private val dayClickListener: DayClickListener?
        get() = parentFragment as? DayClickListener
    private val monthSwipeListener: MonthSwipeListener?
        get() = parentFragment as? MonthSwipeListener


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initObserver()
    }

    override fun onResume() {
        super.onResume()

        (parentFragment as HomeFragment).setCalendarListener(this)
        binding.pbLoading.isVisible = true  // View.VISIBLE

        setMonthlyEmojiData()
    }

    private fun initRecyclerView() {
        binding.rvCalendar.apply {
            adapter = calendarDayAdapter
            itemAnimator = null
            if (::viewPool.isInitialized) {
                setRecycledViewPool(viewPool)
                (layoutManager as GridLayoutManager).recycleChildrenOnDetach = true
            }
        }
    }

    private fun initObserver() {
        viewModel.monthlyEmoji.observe(viewLifecycleOwner) { itemList ->
            Log.e("CHECK", "$itemList")
            calendarDayAdapter.submitList(itemList)
            binding.pbLoading.isVisible = false
            monthSwipeListener?.onSwipe()
        }
        viewModel.monthlyCalendar.observe(viewLifecycleOwner) {
            dayClickListener?.onClick(LocalDate.of(it.year, it.month, it.selectedDay))
        }
    }

    private fun dayClickCallback(date: Int) {
        viewModel.updateSelectedDay(date)
    }

    override fun onClick() {
        setMonthlyEmojiData()
    }

    override fun onSwipe() {
        swipeUpdate()
    }

    private fun swipeUpdate() {
        viewModel.setMonthlyDailyEmojis()
    }

    private fun setMonthlyEmojiData() {
        viewModel.initData()
        viewModel.setMonthlyDailyEmojis()
    }

    companion object {
        const val ITEM_ID_ARGUMENT = "item id"
        fun newInstance(
            itemId: Long,
            calendarPool: RecyclerView.RecycledViewPool
        ): CalendarFragment {
            return CalendarFragment().apply {
                arguments = bundleOf(ITEM_ID_ARGUMENT to itemId)
                viewPool = calendarPool
            }

        }
    }



}