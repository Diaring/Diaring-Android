package com.oss.diaring.data.repository

import com.oss.diaring.data.database.entity.DailyEmojis
import com.oss.diaring.data.datasource.DiaryDataSource
import com.oss.diaring.data.model.DiaryWithDailyEmoji
import java.time.LocalDate
import javax.inject.Inject

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
}