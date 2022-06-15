package com.oss.diaring.data.model

import com.oss.diaring.data.database.entity.DailyWeather

data class DayWithWeather(
    val day: Int,
    val dailyWeather: List<DailyWeather>,
) {
    val weatherState = dailyWeather.map {
        it.weatherState
    }
}
