package com.oss.diaring.presentation.home.calendar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
import timber.log.Timber
import java.time.LocalDate

@AndroidEntryPoint
class CalendarFragment : Fragment(), TodayClickListener, EmojiSwipeListener {

    private lateinit var viewPool: RecyclerView.RecycledViewPool
    private lateinit var binding: FragmentCalendarBinding

    private val viewModel: CalendarViewModel by viewModels()

    private val calendarDayAdapter = CalendarDayAdapter(::dayClickCallback)
    private val dayClickListener: DayClickListener?
        get() = parentFragment as? DayClickListener
    private val monthSwipeListener: MonthSwipeListener?
        get() = parentFragment as? MonthSwipeListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.e("VIEWCREATED")

        initRecyclerView()
        initObserver()
    }

    override fun onResume() {
        super.onResume()

        Timber.e("RESUME")

        (parentFragment as? HomeFragment)?.setCalendarListener(this)
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
            Log.e("CHECK", "a")
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