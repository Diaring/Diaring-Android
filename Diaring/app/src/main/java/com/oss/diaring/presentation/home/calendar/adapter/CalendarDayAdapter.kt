package com.oss.diaring.presentation.home.calendar.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.oss.diaring.R
import com.oss.diaring.data.model.DayWithEmojiAndSelect
import com.oss.diaring.databinding.ItemCalendarDayBinding
import com.oss.diaring.util.Constants.EMPTY_DATE

class CalendarDayAdapter(private val dayClickCallback: (Int) -> (Unit)) :
    ListAdapter<DayWithEmojiAndSelect, CalendarDayAdapter.CalendarDayViewHolder>(CalendarDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCalendarDayBinding.inflate(inflater, parent, false)
        return CalendarDayViewHolder(binding, dayClickCallback)
    }

    override fun onBindViewHolder(holder: CalendarDayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CalendarDayViewHolder(
        private val binding: ItemCalendarDayBinding,
        private val dayClickCallback : (Int) -> (Unit)
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DayWithEmojiAndSelect) {

            val date = item.day

            if (date != EMPTY_DATE) {
                setDayText(date, item.isSelected)

                binding.ivEmoji.visibility = View.VISIBLE

                when (item.emoji.firstOrNull()) {
                    "VERY_BAD" -> binding.ivEmoji.setImageResource(R.drawable.ic_very_bad_24)  // very bad
                    "BAD" -> binding.ivEmoji.setImageResource(R.drawable.ic_bad_24)  // bad
                    "FINE" -> binding.ivEmoji.setImageResource(R.drawable.ic_fine_24)  // fine
                    "GOOD" -> binding.ivEmoji.setImageResource(R.drawable.ic_good_24)  // good
                    "VERY_GOOD" -> binding.ivEmoji.setImageResource(R.drawable.ic_very_good_24)  // very_good
                    else -> {}
                }

                binding.root.setOnClickListener {
                    dayClickCallback(binding.tvCalendarDay.text.toString().toInt())
                }
            } else {
                binding.ivEmoji.visibility = View.INVISIBLE
                binding.tvCalendarDay.visibility = View.INVISIBLE
            }

            binding.executePendingBindings()
        }

        private fun setDayText(date: Int, isSelected: Boolean) {
            val color: Int
            val style: Typeface

            if (isSelected) {
                color = R.color.black
                style = Typeface.DEFAULT_BOLD
            } else {
                color = R.color.dark_grey
                style = Typeface.DEFAULT
            }

            binding.tvCalendarDay.apply {
                text = date.toString()
                typeface = style
                setTextColor(ContextCompat.getColor(context, color))
            }
        }

    }

    class CalendarDiffUtil() : DiffUtil.ItemCallback<DayWithEmojiAndSelect>() {
        override fun areItemsTheSame(
            oldItem: DayWithEmojiAndSelect,
            newItem: DayWithEmojiAndSelect
        ): Boolean {
            return oldItem.day == newItem.day
        }

        override fun areContentsTheSame(
            oldItem: DayWithEmojiAndSelect,
            newItem: DayWithEmojiAndSelect
        ): Boolean {
            return oldItem == newItem
        }
    }
}