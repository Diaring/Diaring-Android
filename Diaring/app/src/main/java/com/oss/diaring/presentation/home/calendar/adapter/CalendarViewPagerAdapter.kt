package com.oss.diaring.presentation.home.calendar.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.oss.diaring.presentation.home.calendar.CalendarFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class CalendarViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    private val calendarPool = RecyclerView.RecycledViewPool()
    val firstFragmentPosition = Int.MAX_VALUE / 2
    override fun getItemCount() = Int.MAX_VALUE

    override fun createFragment(position: Int): Fragment {
        val itemId = getItemId(position)

        return CalendarFragment.newInstance(itemId, calendarPool)
    }

    override fun getItemId(position: Int): Long {
        val moveMonth: Long = (position - firstFragmentPosition).toLong()

        val today = if (moveMonth > 0) {
            LocalDate.now().plusMonths(moveMonth)
        } else {
            LocalDate.now().minusMonths(abs(moveMonth))
        }

        return today.format(DateTimeFormatter.ofPattern("yyyyMM")).toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return itemId in MIN_VALUE..MAX_VALUE
    }

    companion object {
        const val MIN_VALUE = 20000102L
        const val MAX_VALUE = 20991230L
    }
}