package com.oss.diaring.data.database.dao

import androidx.room.*
import com.oss.diaring.data.database.entity.DailyEmojis
import com.oss.diaring.data.database.entity.DailyWeather
import com.oss.diaring.data.database.entity.Diary
import java.time.LocalDate

// Database Query를 위한 Interface
@Dao
interface DiaryDao {
    @Query("SELECT * FROM DailyEmojis")
    suspend fun getAllDailyEmojis(): List<DailyEmojis>

    @Query("SELECT * FROM DailyWeather")
    suspend fun getAllDailyWeather(): List<DailyWeather>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDiary(diary: Diary): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyEmojis(dailyEmojis: DailyEmojis)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyWeather(dailyWeather: DailyWeather)

    @Update
    suspend fun updateDiary(diary: Diary)

    @Query("SELECT * FROM `diary_database.db` WHERE `no`=:id")
    suspend fun getDiaryById(id: Int): Diary?

    @Query("SELECT * FROM `diary_database.db`")
    suspend fun getAllDiaries(): List<Diary>//MutableMap<LocalDate, Diary>

    @Query("DELETE FROM `diary_database.db` WHERE `no`=:id")
    suspend fun deleteDiary(id: Int)
}