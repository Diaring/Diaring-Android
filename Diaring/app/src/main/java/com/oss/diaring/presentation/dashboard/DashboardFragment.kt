package com.oss.diaring.presentation.dashboard

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
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
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import androidx.fragment.app.viewModels

//Navigation Bar에서 두번째 탭에 해당하는 Dashboard Fragment
@AndroidEntryPoint
class DashboardFragment : BaseFragment<FragmentDashboardBinding>(R.layout.fragment_dashboard) {

    private lateinit var barChart: BarChart
    private lateinit var pieChart: PieChart
    private lateinit var btnLeft: ImageView
    private lateinit var tvMonth: TextView
    private lateinit var btnRight: ImageView

    private val viewModel: DashboardViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI(view)
    }

    @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    // 초기 UI를 구성하는 함수
    private fun initUI(view : View){
        btnLeft = view.findViewById(R.id.iv_previous_month)
        tvMonth = view.findViewById(R.id.tv_month)
        btnRight = view.findViewById(R.id.iv_next_month)
        barChart = view.findViewById(R.id.barChart)
        pieChart = view.findViewById(R.id.pieChart)

        viewModel.initData()
        tvMonth.text = (viewModel.curYear.toString() + "년 " + viewModel.curMonth + "월")
        setBarData(barChart)
        setPieData(pieChart)

        btnLeft.setOnTouchListener{ _: View, event: MotionEvent ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.previousMonth()
                }
                MotionEvent.ACTION_UP -> {
                    tvMonth.text = (viewModel.curYear.toString() + "년 " + viewModel.curMonth + "월")
                    setBarData(barChart)
                    setPieData(pieChart)
                }
            }
            true
        }

        btnRight.setOnTouchListener{ _: View, event: MotionEvent ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    viewModel.nextMonth()
                }
                MotionEvent.ACTION_UP -> {
                    tvMonth.text = (viewModel.curYear.toString() + "년 " + viewModel.curMonth + "월")
                    setBarData(barChart)
                    setPieData(pieChart)
                }
            }
            true
        }
    }


    @SuppressLint("UseCompatLoadingForDrawables")
    // Bar Chart의 Data를 set하는 함수
    private fun setBarData(barChart: BarChart) {
        initBarChart(barChart)

        barChart.setScaleEnabled(false) //Zoom In/Out

        val entries: ArrayList<BarEntry> = ArrayList()
        val title = "월별 기분"
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
                1.0f, viewModel.eCount1,
                resources.getDrawable(R.drawable.ic_very_bad_24, null)
            )
        )
        entries.add(
            BarEntry(
                2.0f, viewModel.eCount2,
                resources.getDrawable(R.drawable.ic_bad_24, null)
            )
        )
        entries.add(
            BarEntry(
                3.0f, viewModel.eCount3,
                resources.getDrawable(R.drawable.ic_fine_24, null)
            )
        )
        entries.add(
            BarEntry(
                4.0f, viewModel.eCount4,
                resources.getDrawable(R.drawable.ic_good_24, null)
            )
        )
        entries.add(
            BarEntry(
                5.0f, viewModel.eCount5,
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

    // Bar Chart의 초기환경을 설정하는 함수
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
        leftAxis.axisMaximum = 31f
        leftAxis.axisMinimum = 0f

        //우측 값 hiding the right y-axis line, default true if not set
        val rightAxis: YAxis = barChart.axisRight
        rightAxis.setDrawLabels(false)
        rightAxis.setDrawAxisLine(false)
        rightAxis.setDrawGridLines(false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    // Pie Chart의 Data를 set하는 함수
    private fun setPieData(pieChart: PieChart) {
        initPieChart(pieChart)
        val wTotal = viewModel.wCount1 + viewModel.wCount2 + viewModel.wCount3 + viewModel.wCount4
        val entries = ArrayList<PieEntry>()
        entries.add(
            PieEntry(
                viewModel.wCount1/wTotal*100f, "눈",
                resources.getDrawable(R.drawable.ic_snow_blue_24, null)
            )
        )
        entries.add(
            PieEntry(
                viewModel.wCount2/wTotal*100f, "구름많음",
                resources.getDrawable(R.drawable.ic_cloud_blue_24, null)
            )
        )
        entries.add(
            PieEntry(
                viewModel.wCount3/wTotal*100f, "맑음",
                resources.getDrawable(R.drawable.ic_sun_blue_24, null)
            )
        )
        entries.add(
            PieEntry(
                viewModel.wCount4/wTotal*100f, "비",
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
        colors.add(Color.parseColor("#41617f")) // 비
        dataSet.colors = colors
        val data = PieData(dataSet)
        data.setValueTextSize(22.0f)
        data.setValueTextColor(Color.WHITE)
        pieChart.data = data
        pieChart.invalidate()
    }

    // Pie Chart의 초기환경을 설정하는 함수
    private fun initPieChart(pieChart: PieChart) {
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
    }
}