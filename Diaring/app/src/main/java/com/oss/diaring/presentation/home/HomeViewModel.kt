package com.oss.diaring.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oss.diaring.data.model.DiaryWithDailyEmoji
import com.oss.diaring.data.repository.DiaryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.LocalDate
import javax.inject.Inject

// Home Fragment에서 사용되는 ViewModel
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: DiaryRepository
) : ViewModel(){

    private val _diaryList = MutableLiveData<List<DiaryWithDailyEmoji>>()
    val diaryList: LiveData<List<DiaryWithDailyEmoji>> = _diaryList

    private val _date = MutableLiveData(LocalDate.now())
    val date: LiveData<LocalDate> = _date


    fun updateDate(year: Int, month: Int, day: Int) {
        _date.value = LocalDate.of(year, month, day)
    }


}