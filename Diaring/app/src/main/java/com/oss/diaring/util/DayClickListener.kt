package com.oss.diaring.util

import java.time.LocalDate

fun interface DayClickListener {
    fun onClick(selectedDate: LocalDate)
}