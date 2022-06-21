package com.oss.diaring.presentation.diary

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.getColor
import androidx.fragment.app.Fragment
import androidx.databinding.adapters.TextViewBindingAdapter.setText
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.oss.diaring.R
import com.oss.diaring.databinding.FragmentDiaryListBinding
import com.oss.diaring.presentation.diary.DiaryListFragment
import java.util.*

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
//        private var transparencyBlock : ImageView = itemView!!.findViewById(R.id.transparency_block)
//        private var viewAll: Boolean = itemView!!.findViewById(R.id.data3Text)

        // onBindViewHolder의 역할을 대신한다.
        fun bindItems(data: DiaryListData, position: Int) {
            //Log.d("ListAdapter", "===== ===== ===== ===== bind ===== ===== ===== =====")
            //Log.d("ListAdapter", data.getData1()+" "+data.getData2()+" "+data.getData3())
            dateText.text = "${data.date.dayOfMonth}일"
            //dateText.setText(Resources.getSystem().getString(R.string.day, data.date))
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
                onClickListener?.invoke(view, DiaryListData(data.date, data.subject, data.weather, data.emotion))
            }
            //data3Text.text = data.getData3()
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryListViewHolder {
        val inflater = LayoutInflater.from(parent.context)
//        val binding = FragmentDiaryListBinding.inflate(inflater, parent, false)

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diary_list, parent, false)
        return DiaryListViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.count()
    }

    // ViewHolder의 bind 메소드를 호출한다.
    override fun onBindViewHolder(holder: DiaryListAdapter.DiaryListViewHolder, position: Int) {
//            Log.d("ListAdapter", "===== ===== ===== ===== onBindViewHolder ===== ===== ===== =====")
        return holder.bindItems(list[position], position)
    }
}