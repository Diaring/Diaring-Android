package com.oss.diaring.presentation.home.calendar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.oss.diaring.databinding.ItemCalendarBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// Home Fragment에서 RecyclerView의 상태를 관리하는 Adapter
class CalendarAdapter(
    private val todayClickCallback: () -> Unit,
    private val fragmentManager: FragmentManager,
    private val lifecycle: Lifecycle
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {
    private var date = LocalDate.now()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return CalendarViewHolder(
            ItemCalendarBinding.inflate(inflater, parent, false),
            todayClickCallback,
            fragmentManager,
            lifecycle
        )
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(date)
    }

    override fun getItemCount() = 1

    fun setDate(newDate: LocalDate) {
        date = newDate
    }

    class CalendarViewHolder(
        private val binding: ItemCalendarBinding,
        private val todayClickCallback: () -> Unit,
        private val fragmentManager: FragmentManager,
        private val lifecycle: Lifecycle
    ) : RecyclerView.ViewHolder(binding.root) {

        private val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월")
        private val calendarViewPagerAdapter = CalendarViewPagerAdapter(fragmentManager, lifecycle)
        private var calendarPosition = calendarViewPagerAdapter.firstFragmentPosition

        init {
            binding.today = LocalDate.now().dayOfMonth.toString()
            binding.vpCalendar.adapter = calendarViewPagerAdapter
            setViewPagerPosition()
            setViewPagerPage(false)
        }

        fun bind(date: LocalDate) {
            binding.date = date.format(formatter)

            setViewPagerHeightWithContent(calendarPosition)

            binding.btnToday.setOnClickListener {
                if (date.monthValue == LocalDate.now().monthValue) {
                    todayClickCallback()
                } else {
                    setViewPagerPage(true)
                }
            }
            binding.executePendingBindings()
        }

        private fun setViewPagerPage(smoothScroll: Boolean) {
            binding.vpCalendar.setCurrentItem(
                calendarViewPagerAdapter.firstFragmentPosition,
                smoothScroll
            )
        }

        private fun setViewPagerPosition() {
            binding.vpCalendar.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    calendarPosition = position
                }
            })
        }

        private fun setViewPagerHeightWithContent(position: Int) {
            val viewPager = binding.vpCalendar

            val view = (viewPager[0] as RecyclerView).layoutManager?.findViewByPosition(position)
            view?.post {
                val widthMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
                val heightMeasureSpec =
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

                view.measure(widthMeasureSpec, heightMeasureSpec)

                if (viewPager.layoutParams.height != view.measuredHeight) {
                    viewPager.layoutParams = (viewPager.layoutParams).also {
                        it.height = viewPager.measuredHeight
                    }
                }
            }
        }
    }
}