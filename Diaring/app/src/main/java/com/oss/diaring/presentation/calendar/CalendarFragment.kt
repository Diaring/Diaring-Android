package com.oss.diaring.presentation.calendar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.SimpleItemAnimator
import com.oss.diaring.R
import com.oss.diaring.databinding.FragmentCalendarBinding
import com.oss.diaring.presentation.base.BaseFragment
import com.oss.diaring.presentation.calendar.adapter.MonthAdapter

class CalendarFragment : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    private val monthAdapter: MonthAdapter by lazy { MonthAdapter() }
    private val pagerSnapHelper: PagerSnapHelper by lazy { PagerSnapHelper() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCalendarRecyclerView()
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

}