package com.oss.diaring.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
data class MonthlyCalendar(
    val year: Int,
    val month: Int,
    val selectedDay: Int
) {
    private val baseDate = LocalDate.of(year, month, 1)
    val startDate: LocalDate = baseDate.withDayOfMonth(1)
    val endDate: LocalDate = baseDate.withDayOfMonth(baseDate.lengthOfMonth())

    fun getMonthlyDateList(): List<Int> {
        val dateList = (startDate.dayOfMonth..endDate.dayOfMonth).toList()
        val prefixDateList = IntArray(startDate.dayOfWeek.value % 7).toList()

        return prefixDateList + dateList
    }
}

