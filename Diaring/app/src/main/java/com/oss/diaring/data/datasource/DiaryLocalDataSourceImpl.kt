package com.oss.diaring.data.datasource

import com.oss.diaring.data.database.dao.DiaryDao
import com.oss.diaring.data.database.entity.DailyEmojis
import com.oss.diaring.data.model.DiaryWithDailyEmoji
import java.time.LocalDate
import javax.inject.Inject

class DiaryDataSourceImpl @Inject constructor(
    private val diaryDao: DiaryDao
) : DiaryDataSource {

    override suspend fun getAllEmojiList(): List<DailyEmojis> {
        return diaryDao.getAllDailyEmojis()
    }

    override suspend fun insertDailyEmojisWithDateRange(dateRange: List<LocalDate>) {

    }

    override suspend fun getDiaryWithDailyEmojis(selectedDate: LocalDate): List<DiaryWithDailyEmoji> {
        TODO("Not yet implemented")
    }
}