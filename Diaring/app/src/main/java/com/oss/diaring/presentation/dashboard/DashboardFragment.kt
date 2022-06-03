package com.oss.diaring.presentation.dashboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.oss.diaring.R
import com.oss.diaring.databinding.FragmentDashboardBinding
import com.oss.diaring.presentation.base.BaseFragment
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DashboardFragment : BaseFragment<FragmentDashboardBinding>(R.layout.fragment_dashboard) {

    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart
    private lateinit var btn_left: ImageView
    private lateinit var tv_month: TextView
    private lateinit var btn_right: ImageView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun initUI(view : View){
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월")
        val formatted = current.format(formatter)

        btn_left = view.findViewById(R.id.iv_previous_month)
        tv_month = view.findViewById(R.id.tv_month)
        btn_right = view.findViewById(R.id.iv_next_month)

        val token = formatted.chunked(2)
        var cur_year = (token[0] + token[1]).toInt()
        var cur_month = token[3].toInt()

        tv_month.text = (cur_year.toString() + "년 " + cur_month + "월")

        btn_left.setOnClickListener{
            if(cur_month==1){
                cur_year -= 1
                cur_month = 13
            }
            cur_month -= 1
            tv_month.text = (cur_year.toString() + "년 " + cur_month + "월")
        }

        btn_right.setOnClickListener{
            if(cur_month==12){
                cur_year += 1
                cur_month = 0
            }
            cur_month += 1
            tv_month.text = (cur_year.toString() + "년 " + cur_month + "월")
        }

        barChart = view.findViewById(R.id.barChart)
        setBarData(barChart)

        pieChart = view.findViewById(R.id.pieChart)

        pieChart.setUsePercentValues(true)
        pieChart.description.isEnabled = false

        pieChart.centerText = ""

        pieChart.setTransparentCircleColor(Color.WHITE)
        pieChart.setTransparentCircleAlpha(110)

        pieChart.holeRadius = 58f
        pieChart.transparentCircleRadius = 61f

        pieChart.setDrawCenterText(true)

        pieChart.isHighlightPerTapEnabled = true

        val pieLegend = pieChart.legend
        pieLegend.isEnabled = false

        pieChart.setEntryLabelColor(Color.WHITE)
        pieChart.setEntryLabelTextSize(12f)

        setPieData()
    }



    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setBarData(barChart: BarChart) {
        initBarChart(barChart)

        barChart.setScaleEnabled(false) //Zoom In/Out

        val entries: ArrayList<BarEntry> = ArrayList()
        val title = "이번 달 기분"
        val xAxisValues: List<String> = ArrayList(
            listOf(
                "",
                "Very Bad",
                "Bad",
                "Fine",
                "Good",
                "Very Good"
            )
        )
        entries.add(
            BarEntry(
                1.0f, 20.0f,
                resources.getDrawable(R.drawable.ic_very_bad_24, null)
            )
        )
        entries.add(
            BarEntry(
                2.0f, 40.0f,
                resources.getDrawable(R.drawable.ic_bad_24, null)
            )
        )
        entries.add(
            BarEntry(
                3.0f, 60.0f,
                resources.getDrawable(R.drawable.ic_fine_24, null)
            )
        )
        entries.add(
            BarEntry(
                4.0f, 30.0f,
                resources.getDrawable(R.drawable.ic_good_24, null)
            )
        )
        entries.add(
            BarEntry(
                5.0f, 90.0f,
                resources.getDrawable(R.drawable.ic_very_good_24, null)
            )
        )

        val barDataSet = BarDataSet(entries, title)
        val data = BarData(barDataSet)
        //Changing the color of the bar
        barDataSet.color = Color.parseColor("#66A4FF")
        //Setting the size of the form in the legend
        barDataSet.formSize = 15f
        //showing the value of the bar, default true if not set
        barDataSet.setDrawValues(false)
        barDataSet.iconsOffset = MPPointF(0F, (-10).toFloat())
        //setting the text size of the value of the bar
        barDataSet.valueTextSize = 12f
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisValues)
        barChart.legend.isEnabled = false
        barChart.data = data
        barChart.invalidate()
    }

    private fun initBarChart(barChart: BarChart) {
        //hiding the grey background of the chart, default false if not set
        barChart.setDrawGridBackground(false)
        //remove the bar shadow, default false if not set
        barChart.setDrawBarShadow(false)
        //remove border of the chart, default false if not set
        barChart.setDrawBorders(false)

        //remove the description label text located at the lower right corner
        val description = Description()
        description.isEnabled = false
        barChart.description = description

        //X, Y 바의 애니메이션 효과
        barChart.animateY(1000)
        barChart.animateX(1000)

        //바텀 좌표 값
        val xAxis: XAxis = barChart.xAxis
        xAxis.isEnabled = true
        //change the position of x-axis to the bottom
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        //set the horizontal distance of the grid line
        xAxis.granularity = 1f
        xAxis.textColor = Color.parseColor("#66A4FF")
        xAxis.textSize = 13f
        //hiding the x-axis line, default true if not set
        xAxis.setDrawAxisLine(false)
        //hiding the vertical grid lines, default true if not set
        xAxis.setDrawGridLines(false)

        //좌측 값 hiding the left y-axis line, default true if not set
        val leftAxis: YAxis = barChart.axisLeft
        leftAxis.textSize = 13f
        leftAxis.setDrawLabels(true)
        leftAxis.setDrawAxisLine(false)
        leftAxis.setDrawGridLines(false)

        //우측 값 hiding the right y-axis line, default true if not set
        val rightAxis: YAxis = barChart.axisRight
        rightAxis.setDrawLabels(false)
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawGridLines(false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setPieData() {
        val entries = ArrayList<PieEntry>()
        entries.add(
            PieEntry(
                8.0f, "눈",
                resources.getDrawable(R.drawable.ic_snow_blue_24, null)
            )
        )
        entries.add(
            PieEntry(
                37.0f, "구름많음",
                resources.getDrawable(R.drawable.ic_cloud_blue_24, null)
            )
        )
        entries.add(
            PieEntry(
                32.0f, "맑음",
                resources.getDrawable(R.drawable.ic_sun_blue_24, null)
            )
        )
        entries.add(
            PieEntry(
                23.0f, "비",
                resources.getDrawable(R.drawable.ic_rain_blue_24, null)
            )
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.setDrawIcons(true)
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0F, (-60).toFloat())
        dataSet.selectionShift = 5f
        val colors = ArrayList<Int>()

        colors.add(Color.parseColor("#808080")) // 눈
        colors.add(Color.parseColor("#d3d1d6")) // 구름
        colors.add(Color.parseColor("#66A4FF")) // 맑음
        colors.add(Color.parseColor("#41617f")) //비
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setValueTextSize(22.0f)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data
        pieChart.invalidate()
    }

}

