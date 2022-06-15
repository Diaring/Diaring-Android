package com.oss.diaring.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.oss.diaring.data.database.converter.BitmapConverter
import com.oss.diaring.data.database.converter.DateConverter
import com.oss.diaring.data.database.converter.ListConverter
import com.oss.diaring.data.database.dao.DiaryDao
import com.oss.diaring.data.database.entity.DailyEmojis
import com.oss.diaring.data.database.entity.Diary

@Database(version = 1, entities = [Diary::class, DailyEmojis::class], exportSchema = false)
@TypeConverters(
    value = [
        ListConverter::class,
        BitmapConverter::class,
        DateConverter::class
    ]
)
abstract class DiaryDatabase : RoomDatabase() {

    abstract fun diaryDao(): DiaryDao
}