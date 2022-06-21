package com.oss.diaring.data.database.dao

import androidx.room.*
import com.oss.diaring.data.database.entity.DailyEmojis
import com.oss.diaring.data.database.entity.DailyWeather
import com.oss.diaring.data.database.entity.Diary
import java.time.LocalDate

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

    @Query("SELECT * FROM DailyWeather")
    suspend fun getAllDailyWeather(): List<DailyWeather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(diary: Diary): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertDailyDiary(dailyEmojis: DailyEmojis)

    @Update
    suspend fun updateDiary(diary: Diary)

    @Query("SELECT * FROM `diary_database.db` WHERE `no`=:id")
    suspend fun getDiaryById(id: Int): Diary?

    @Query("SELECT * FROM `diary_database.db`")
    suspend fun getAllDiaries(): List<Diary>//MutableMap<LocalDate, Diary>

    @Query("DELETE FROM `diary_database.db` WHERE `no`=:id")
    suspend fun deleteDiary(id: Int)
}