package com.oss.diaring.data.database.dao

import androidx.room.*
import com.oss.diaring.data.database.entity.DailyEmojis

@Dao
interface DiaryDao {
//    @Query("SELECT * FROM `diary_database.db` WHERE date =:date")
//    suspend fun getAllByDate(date: LocalDate): List<Diary>
//
//    @Query("SELECT * From `diary_database.db` WHERE date =:date")
//    suspend fun getEmojisByDate(date: LocalDate): List<EmotionEmoji>
//
//    @Query("SELECT * FROM `diary_database.db` WHERE date =:date")
//    suspend fun getWeatherByDate(date: LocalDate): List<Weather>
//
//    @Insert(onConflict = OnConflictStrategy.IGNORE)
//    suspend fun insertDiary(diary: Diary)
//
//    @Delete
//    suspend fun deleteDiary(diary: Diary)
//
//    @Update
//    suspend fun updateDiary(diary: Diary)

    @Query("SELECT * FROM DailyEmojis")
    suspend fun getAllDailyEmojis(): List<DailyEmojis>
}