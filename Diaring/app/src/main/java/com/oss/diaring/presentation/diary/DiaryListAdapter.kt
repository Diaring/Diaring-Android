package com.oss.diaring.presentation.diary

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oss.diaring.R

class DiaryListAdapter(
    private var context : Context,
    private var list: MutableList<DiaryListData>,
    private val onClickListener: (View, DiaryListData) -> Unit
    )
    : RecyclerView.Adapter<DiaryListAdapter.DiaryListViewHolder> (){

    inner class DiaryListViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {

        private var dateText: TextView = itemView!!.findViewById(R.id.dateText)
        private var subjectText: TextView = itemView!!.findViewById(R.id.subjectText)
        private var weatherImage: ImageView = itemView!!.findViewById(R.id.weatherImage)
        private var emotionImage: ImageView = itemView!!.findViewById(R.id.emotionImage)

        fun bindItems(data: DiaryListData, position: Int) {
            dateText.text = "${data.date.dayOfMonth}일"
            subjectText.text = data.subject
            when (data.weather) {
                0 -> weatherImage.setImageResource(R.drawable.ic_nothing_24)
                1 -> weatherImage.setImageResource(R.drawable.ic_snow_24)
                2 -> weatherImage.setImageResource(R.drawable.ic_cloud_24)
                3 -> weatherImage.setImageResource(R.drawable.ic_sun_24)
                4 -> weatherImage.setImageResource(R.drawable.ic_rain_24)
            }
            weatherImage.setColorFilter(context.getColor(R.color.primary_blue))
            when (data.emotion) {
                0 -> emotionImage.setImageResource(R.drawable.ic_nothing_24)
                1 -> emotionImage.setImageResource(R.drawable.ic_very_bad_24)
                2 -> emotionImage.setImageResource(R.drawable.ic_bad_24)
                3 -> emotionImage.setImageResource(R.drawable.ic_fine_24)
                4 -> emotionImage.setImageResource(R.drawable.ic_good_24)
                5 -> emotionImage.setImageResource(R.drawable.ic_very_good_24)
            }
            emotionImage.setColorFilter(context.getColor(R.color.primary_blue))

            if (data.subject.isEmpty()) {
                dateText.alpha=0.35f
                subjectText.text="미작성"
                subjectText.alpha=0.35f
            } else {
                dateText.alpha=1f
                subjectText.alpha=1f
            }

            itemView.setOnClickListener { view ->
                onClickListener.invoke(view, DiaryListData(data.date, data.subject, data.weather, data.emotion))
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryListViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diary_list, parent, false)
        return DiaryListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    override fun onBindViewHolder(holder: DiaryListAdapter.DiaryListViewHolder, position: Int) {
        return holder.bindItems(list[position], position)
    }
}