package com.oss.diaring.data.repository

import com.oss.diaring.data.database.entity.DailyEmojis
import com.oss.diaring.data.model.DiaryWithDailyEmoji
import java.time.LocalDate

interface DiaryRepository {
    suspend fun getAllDailyEmojis(): List<DailyEmojis>

    suspend fun insertDailyEmojisWithDateRange(dateRange: List<LocalDate>)

    suspend fun getDiaryWithDailyEmojis(selectedDate: LocalDate): List<DiaryWithDailyEmoji>
}