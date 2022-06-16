package com.oss.diaring.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.oss.diaring.util.WeatherStates
import java.time.LocalDate

@Entity(primaryKeys = ["date", "parent_id"])
data class DailyWeather(
    @ColumnInfo(name = "weather_state") val weatherState: WeatherStates,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "parent_id") val parentId: Int,
    @ColumnInfo(name = "date") val date: LocalDate
)
