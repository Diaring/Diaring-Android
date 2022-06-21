package com.oss.diaring.data.model

import com.oss.diaring.data.database.entity.DailyWeather

// 날짜, 날씨 값들을 사용하기 위한 모델 클래스
data class DayWithWeather(
    val day: Int,
    val dailyWeather: List<DailyWeather>,
) {
    val weatherState = dailyWeather.map {
        it.weatherState
    }
}
