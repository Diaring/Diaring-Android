package com.oss.diaring.data.database.entity

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.oss.diaring.util.Constants.DIARY_DB_NAME
import java.io.Serializable
import java.time.LocalDate

@Entity(tableName = DIARY_DB_NAME)
data class Diary(
    @PrimaryKey(autoGenerate = true) val no: Int = 0,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "date") val date: LocalDate,
    @ColumnInfo(name = "location") val location: String?,
    @ColumnInfo(name = "hash_tag_list") val hashTagList: List<String>?,
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "weather") val weather: Int,
    @ColumnInfo(name = "emotion") val emotion: Int,
    @ColumnInfo(name = "image") val image: Bitmap?
) : Serializable
