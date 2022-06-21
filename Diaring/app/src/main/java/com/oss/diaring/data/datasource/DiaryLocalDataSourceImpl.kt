package com.oss.diaring.data.datasource

import com.oss.diaring.data.database.dao.DiaryDao
import com.oss.diaring.data.database.entity.DailyEmojis
import com.oss.diaring.data.database.entity.DailyWeather
import com.oss.diaring.data.database.entity.Diary
import com.oss.diaring.data.model.DiaryWithDailyEmoji
import com.oss.diaring.util.EmojiStates
import java.time.LocalDate
import javax.inject.Inject

class DiaryDataSourceImpl @Inject constructor(
    private val diaryDao: DiaryDao
) : DiaryDataSource {

    override suspend fun getAllEmojiList(): List<DailyEmojis> {
        return diaryDao.getAllDailyEmojis()
    }

    override suspend fun insertDailyEmojisWithDateRange(dateRange: List<LocalDate>) {

    }

    override suspend fun getDiaryWithDailyEmojis(selectedDate: LocalDate): List<DiaryWithDailyEmoji> {
        TODO("Not yet implemented")
    }

    override suspend fun insertDiary(diary: Diary): Long {
        return diaryDao.insertDiary(diary)
    }

    override suspend fun insertDiary(diary: Diary, selectedDate: LocalDate): Long {
        val diaryId = diaryDao.insertDiary(diary)

        diaryDao.insertDailyEmojis(
            DailyEmojis(
                EmojiStates.DEFAULT,
                true,
                diaryId.toInt(),
                selectedDate
            )
        )

        return diaryId
    }

    override suspend fun insertDailyEmojis(dailyDiary: DailyEmojis) {
        diaryDao.insertDailyEmojis(dailyDiary)
    }

    override suspend fun insertDailyWeather(dailyWeather: DailyWeather) {
        diaryDao.insertDailyWeather(dailyWeather)
    }

    override suspend fun updateDiary(diary: Diary, selectedDate: LocalDate) {
//        val originDiary = diaryDao.getDiaryById(diary.no)

        diaryDao.updateDiary(diary)
    }

    override suspend fun getAllWeatherList(): List<DailyWeather> {
        return diaryDao.getAllDailyWeather()
    }

    override suspend fun getAllDiaries(): List<Diary> {//MutableMap<LocalDate, Diary> {
        return diaryDao.getAllDiaries()
    }

    override suspend fun deleteDiary(id: Int) {
        return diaryDao.deleteDiary(id)
    }
}