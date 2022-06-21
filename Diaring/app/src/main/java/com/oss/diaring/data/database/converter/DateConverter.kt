package com.oss.diaring.data.database.converter

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalDate

// LocalDate 형태의 날짜 값들을 String으로 저장하기 위한 Converter
class DateConverter {
    @RequiresApi(Build.VERSION_CODES.O)  // 이게 맞는지 확인해봐야함
    @TypeConverter
    fun stringToDate(date: String?): LocalDate? {
        return date?.let { LocalDate.parse(date) }
    }

    @TypeConverter
    fun dateToString(date: LocalDate?): String? {
        return date?.toString()
    }
}