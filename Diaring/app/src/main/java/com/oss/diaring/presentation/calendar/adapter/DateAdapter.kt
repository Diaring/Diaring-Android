package com.oss.diaring.presentation.calendar.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.oss.diaring.databinding.ItemCalendarDateBinding
import java.util.*

class DateAdapter(
    private val currentMonth: Int,
    private val calendarDayList: MutableList<Date>
) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCalendarDateBinding.inflate(inflater, parent, false)
        return DateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        holder.bindItems(currentMonth, calendarDayList[position])
    }

    override fun getItemCount() = ROW * 7

    class DateViewHolder(private val binding: ItemCalendarDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItems(currentMonth: Int, date: Date) {
            binding.tvDate.text = date.date.toString()

            binding.tvDate.setTextColor(
                when (adapterPosition % 7) {
                    0, 6 -> Color.GRAY
                    else -> Color.BLACK
                }
            )

            binding.clDateContainer.setOnClickListener {
                Toast.makeText(binding.root.context, "$adapterPosition", Toast.LENGTH_SHORT).show()
            }

            if (currentMonth != date.month - 1) {
                binding.tvDate.alpha = 0.2f
            }
        }
    }

    companion object {
        const val ROW = 6
    }
}