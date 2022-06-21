package com.oss.diaring.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import com.oss.diaring.util.EmojiStates
import java.time.LocalDate

// Diary Entity에서 날짜와 감정 값들을 따로 관리하는 Entity
@Entity(primaryKeys = ["date", "parent_id"])
data class DailyEmojis(
    @ColumnInfo(name = "emoji_state") val emojiState: EmojiStates,
    @ColumnInfo(name = "is_active") val isActive: Boolean,
    @ColumnInfo(name = "parent_id") val parentId: Int,
    @ColumnInfo(name = "date") val date: LocalDate
)
