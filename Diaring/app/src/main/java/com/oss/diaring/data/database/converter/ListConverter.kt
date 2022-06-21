package com.oss.diaring.data.database.converter

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson

// List 형태의 Multi-Value Attribute를 Json 형태로 저장하는 Converter
class ListConverter {

    @TypeConverter
    fun listToJson(value: List<String>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) //= Gson().fromJson(value, Array<String>::class.java).toList()
            : List<String>? { // prevent runtime error when Diary.hashtag has null value
        if (Gson().fromJson(value, Array<String>::class.java) != null) {
            return Gson().fromJson(value, Array<String>::class.java).toList()
        } else {
            return listOf("")
        }
    }

}