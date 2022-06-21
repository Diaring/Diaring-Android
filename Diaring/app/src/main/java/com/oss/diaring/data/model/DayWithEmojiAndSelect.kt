package com.oss.diaring.data.model

import com.oss.diaring.data.database.entity.DailyEmojis

// 날짜, 감정 값, 클릭 유무를 확인하기 위한 모델 클래스
data class DayWithEmojiAndSelect(
    val day: Int,
    val dailyEmoji: List<DailyEmojis>,
    val isSelected: Boolean
) {
    val emojiState = dailyEmoji.map {
        it.emojiState.name
    }
}
