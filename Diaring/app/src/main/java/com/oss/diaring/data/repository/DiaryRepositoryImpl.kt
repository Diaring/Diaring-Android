package com.oss.diaring.data.repository

import com.oss.diaring.data.database.entity.DailyEmojis
import com.oss.diaring.data.database.entity.DailyWeather
import com.oss.diaring.data.database.entity.Diary
import com.oss.diaring.data.datasource.DiaryDataSource
import com.oss.diaring.data.model.DiaryWithDailyEmoji
import java.time.LocalDate
import javax.inject.Inject

// Data를 사용하기 위한 Repository 구현체
class DiaryRepositoryImpl @Inject constructor(
    private val dataSource: DiaryDataSource
) : DiaryRepository {

    override suspend fun getAllDailyEmojis(): List<DailyEmojis> {
        return dataSource.getAllEmojiList()
    }

    override suspend fun insertDailyEmojisWithDateRange(dateRange: List<LocalDate>) {
        dataSource.insertDailyEmojisWithDateRange(dateRange)
    }

    override suspend fun getDiaryWithDailyEmojis(selectedDate: LocalDate): List<DiaryWithDailyEmoji> {
        return dataSource.getDiaryWithDailyEmojis(selectedDate)
    }

    override suspend fun insertDiary(diary: Diary): Long {
        return dataSource.insertDiary(diary)
    }

    override suspend fun insertDiary(diary: Diary, selectedDate: LocalDate): Long {
        return dataSource.insertDiary(diary, selectedDate)
    }

    override suspend fun insertDailyEmojis(dailyDiary: DailyEmojis) {
        return dataSource.insertDailyEmojis(dailyDiary)
    }

    override suspend fun insertDailyWeather(dailyWeather: DailyWeather) {
        return dataSource.insertDailyWeather(dailyWeather)
    }

    override suspend fun updateDiary(diary: Diary, selectedDate: LocalDate) {
        dataSource.updateDiary(diary, selectedDate)
    }

    override suspend fun getAllDailyWeather(): List<DailyWeather> {
        return dataSource.getAllWeatherList()
    }

    override suspend fun getAllDiaries(): List<Diary> {//MutableMap<LocalDate, Diary> {
        return dataSource.getAllDiaries()
    }

    override suspend fun deleteDiary(id: Int) {
        return dataSource.deleteDiary(id)
    }
}