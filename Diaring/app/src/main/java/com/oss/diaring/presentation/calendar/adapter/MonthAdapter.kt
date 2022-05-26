package com.oss.diaring.presentation.calendar.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oss.diaring.databinding.ItemCalendarMonthBinding
import java.time.LocalDate
import java.util.*

class MonthAdapter : RecyclerView.Adapter<MonthAdapter.MonthViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MonthViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCalendarMonthBinding.inflate(inflater, parent, false)
        return MonthViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MonthViewHolder, position: Int) {
        return holder.bindItems(position)
    }

    override fun getItemCount() = Int.MAX_VALUE

    class MonthViewHolder(
        private val binding: ItemCalendarMonthBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val calendar = Calendar.getInstance()
        private val centerPos = Int.MAX_VALUE / 2

        private lateinit var dateAdapter: DateAdapter

        private lateinit var calendarDayList: MutableList<Date>

        @SuppressLint("SetTextI18n")
        fun bindItems(position: Int) {

            initCalendar(position)
            setCalendarDayList()

            binding.tvCurrentDate.text =
                "${calendar.get(Calendar.YEAR)}년 ${calendar.get(Calendar.MONTH)}월"

        }

        private fun initCalendar(position: Int) {
            calendar.time = Date()  // calendar.time을 현재 시간으로 초기화
            calendar.set(Calendar.DAY_OF_MONTH, 1) // 현재 월의 1일로 이동
            calendar.add(Calendar.MONTH, position - centerPos) // 좌로 스크롤 : -1 , 우로 스크롤 : 1
        }


        private fun setCalendarDayList() {
            calendarDayList = MutableList(6 * 7) { Date() }
            val currentMonth = calendar.get(Calendar.MONTH) - 1

            for (i in 0..5) {
                for (j in 0..6) {
                    calendar.add(
                        Calendar.DAY_OF_MONTH,
                        (1 - calendar.get(Calendar.DAY_OF_WEEK)) + j
                    )

                    calendarDayList[i * 7 + j] = calendar.time
                }

                calendar.add(Calendar.WEEK_OF_MONTH, 1)

                val dateLayoutManager = GridLayoutManager(binding.root.context, 7)

                dateAdapter = DateAdapter(currentMonth, calendarDayList)

                binding.rvMonthlyDay.apply {
                    layoutManager = dateLayoutManager
                    adapter = dateAdapter
                }
            }
        }
    }
}