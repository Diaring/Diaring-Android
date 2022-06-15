package com.oss.diaring.data.model

import com.oss.diaring.data.database.entity.DailyEmojis
import com.oss.diaring.data.database.entity.Diary

data class DiaryWithDailyEmoji(
    val diary: Diary,
    val dailyEmojis: DailyEmojis
)
