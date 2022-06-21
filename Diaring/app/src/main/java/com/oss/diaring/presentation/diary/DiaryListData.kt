package com.oss.diaring.presentation.diary

import java.time.LocalDate

data class DiaryListData(
    var date : LocalDate,
    var subject : String,
    var weather : Int,
    var emotion : Int
)