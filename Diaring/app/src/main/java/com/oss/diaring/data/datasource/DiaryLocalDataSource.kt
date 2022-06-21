package com.oss.diaring.data.datasource

import com.oss.diaring.data.database.entity.DailyEmojis
import com.oss.diaring.data.database.entity.DailyWeather
import com.oss.diaring.data.database.entity.Diary
import com.oss.diaring.data.model.DiaryWithDailyEmoji
import java.time.LocalDate

// Data에 접근하기 위한 비즈니스 로직 인터페이스
interface DiaryDataSource {

    suspend fun getAllEmojiList(): List<DailyEmojis>

    suspend fun insertDailyEmojisWithDateRange(dateRange: List<LocalDate>)

    suspend fun getDiaryWithDailyEmojis(selectedDate: LocalDate): List<DiaryWithDailyEmoji>

    suspend fun insertDiary(diary: Diary, selectedDate: LocalDate) : Long

    suspend fun insertDiary(diary: Diary) : Long

    suspend fun insertDailyEmojis(dailyDiary: DailyEmojis)

    suspend fun insertDailyWeather(dailyWeather: DailyWeather)

    suspend fun updateDiary(diary: Diary, selectedDate: LocalDate)

    suspend fun getAllWeatherList(): List<DailyWeather>

    suspend fun getAllDiaries(): List<Diary>//MutableMap<LocalDate, Diary>

    suspend fun deleteDiary(id: Int)
}