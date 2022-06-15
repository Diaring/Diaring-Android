package com.oss.diaring.presentation.home.calendar

import android.util.Log
import androidx.lifecycle.*
import com.oss.diaring.data.database.entity.DailyEmojis
import com.oss.diaring.data.model.DayWithEmojiAndSelect
import com.oss.diaring.data.model.MonthlyCalendar
import com.oss.diaring.data.repository.DiaryRepository
import com.oss.diaring.presentation.home.calendar.CalendarFragment.Companion.ITEM_ID_ARGUMENT
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: DiaryRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _monthlyCalendar = MutableLiveData<MonthlyCalendar>()
    val monthlyCalendar: LiveData<MonthlyCalendar> = _monthlyCalendar

    private val _monthlyEmoji = MutableLiveData<List<DayWithEmojiAndSelect>>()
    val monthlyEmoji: LiveData<List<DayWithEmojiAndSelect>> = _monthlyEmoji

    fun initData() {
        savedStateHandle.get<Long>(ITEM_ID_ARGUMENT)?.let {
            val year = it.div(100).toInt()
            val month = it.rem(100).toInt()
            val day = if (LocalDate.now().year == year && LocalDate.now().monthValue == month) {
                LocalDate.now().dayOfMonth
            } else {
                1
            }

            _monthlyCalendar.value = MonthlyCalendar(year, month, day)
        }
    }

    fun updateSelectedDay(date: Int) {
        _monthlyCalendar.value = _monthlyCalendar.value?.copy(selectedDay = date)

        _monthlyEmoji.value = _monthlyEmoji.value?.map {
            if (it.day == date) {
                it.copy(isSelected = true)
            } else {
                it.copy(isSelected = false)
            }
        }
    }

    fun setMonthlyDailyEmojis() {
        val calendar = _monthlyCalendar.value ?: return

        viewModelScope.launch {
            val dateList = (calendar.startDate.dayOfMonth .. calendar.endDate.dayOfMonth).map {
                LocalDate.of(calendar.year, calendar.month, it)
            }
//            Log.e("AA", "$dateList")

            val monthlyDailyEmojis = repository.getAllDailyEmojis().filter {
                it.isActive && it.date in calendar.startDate..calendar.endDate
            }

            setMonthlyEmojis(monthlyDailyEmojis)
        }
    }

    private fun setMonthlyEmojis(monthlyDailyEmojis: List<DailyEmojis>) {
        val calendar = _monthlyCalendar.value ?: return

        _monthlyEmoji.value = calendar.getMonthlyDateList().map { date ->
            val todayDailyEmojis = monthlyDailyEmojis.filter {
                it.date.dayOfMonth == date
            }

            DayWithEmojiAndSelect(date, todayDailyEmojis, isSelected(date))
        }
    }

    private fun isSelected(date: Int): Boolean {
        return date == _monthlyCalendar.value?.selectedDay ?: false
    }


}