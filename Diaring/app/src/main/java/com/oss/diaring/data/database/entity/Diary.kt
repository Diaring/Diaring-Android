package com.oss.diaring.data.database.entity

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oss.diaring.util.Constants.DIARY_DB_NAME
import java.io.Serializable
import java.time.LocalDate

// Diary Database Entity
@Entity(tableName = DIARY_DB_NAME)
data class Diary(
    @PrimaryKey(autoGenerate = true) var no: Int,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "date") var date: LocalDate,
    @ColumnInfo(name = "location") var location: String?,
    @ColumnInfo(name = "hash_tag_list") var hashTagList: List<String>?,
    @ColumnInfo(name = "content") var content: String,
    @ColumnInfo(name = "weather") var weather: Int,
    @ColumnInfo(name = "emotion") var emotion: Int,
    @ColumnInfo(name = "image") var image: Bitmap?
) : Serializable
