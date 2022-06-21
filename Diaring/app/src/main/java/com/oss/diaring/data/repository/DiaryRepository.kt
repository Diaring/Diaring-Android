package com.oss.diaring.data.repository

import com.oss.diaring.data.database.entity.DailyEmojis
import com.oss.diaring.data.database.entity.DailyWeather
import com.oss.diaring.data.database.entity.Diary
import com.oss.diaring.data.model.DiaryWithDailyEmoji
import java.time.LocalDate

// Data 사용을 위한 Repository 인터페이스
interface DiaryRepository {
    suspend fun getAllDailyEmojis(): List<DailyEmojis>

    suspend fun insertDailyEmojisWithDateRange(dateRange: List<LocalDate>)

    suspend fun getDiaryWithDailyEmojis(selectedDate: LocalDate): List<DiaryWithDailyEmoji>

    suspend fun insertDiary(diary: Diary): Long

    suspend fun insertDiary(diary: Diary, selectedDate: LocalDate): Long

    suspend fun insertDailyEmojis(dailyDiary: DailyEmojis)

    suspend fun insertDailyWeather(dailyWeather: DailyWeather)

    suspend fun updateDiary(diary: Diary, selectedDate: LocalDate)

    suspend fun getAllDailyWeather(): List<DailyWeather>

    suspend fun getAllDiaries(): List<Diary>//MutableMap<LocalDate, Diary>

    suspend fun deleteDiary(id: Int)
}