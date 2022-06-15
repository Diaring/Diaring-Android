package com.oss.diaring.data.model

import com.oss.diaring.data.database.entity.DailyEmojis

data class DayWithEmojiAndSelect(
    val day: Int,
    val dailyEmoji: List<DailyEmojis>,
    val isSelected: Boolean
) {
    val emoji = dailyEmoji.map {
        it.emoji.name
    }
}
