package com.oss.diaring.presentation.dashboard

import androidx.lifecycle.*
import com.oss.diaring.data.database.entity.DailyEmojis
import com.oss.diaring.data.database.entity.DailyWeather
import com.oss.diaring.data.repository.DiaryRepository
import com.oss.diaring.util.EmojiStates
import com.oss.diaring.util.WeatherStates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

// Dashboard Fragment에서 사용되는 ViewModel
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: DiaryRepository
) : ViewModel() {
    private var _eCount1 = 0.0f
    private var _eCount2 = 0.0f
    private var _eCount3 = 0.0f
    private var _eCount4 = 0.0f
    private var _eCount5 = 0.0f

    val eCount1: Float
        get() = _eCount1
    val eCount2: Float
        get() = _eCount2
    val eCount3: Float
        get() = _eCount3
    val eCount4: Float
        get() = _eCount4
    val eCount5: Float
        get() = _eCount5

    private var _wCount1 = 0.1f
    private var _wCount2 = 0.1f
    private var _wCount3 = 0.1f
    private var _wCount4 = 0.1f

    val wCount1: Float
        get() = _wCount1
    val wCount2: Float
        get() = _wCount2
    val wCount3: Float
        get() = _wCount3
    val wCount4: Float
        get() = _wCount4

    val current = LocalDateTime.now()!!
    private val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월")
    val formatted = current.format(formatter)!!
    private val token = formatted.chunked(2)
    private var _curYear = (token[0] + token[1]).toInt()
    private var _curMonth = token[3].toInt()
    val curYear: Int
        get() = _curYear
    val curMonth: Int
        get() = _curMonth

    // viewModel에서 이모지, 날씨 데이터를 초기화하는 함수
    fun initData() {
        viewModelScope.launch {
            val getAllDailyEmojis = repository.getAllDailyEmojis().filter {
                it.date.year == _curYear && it.date.monthValue == _curMonth
            }
            _eCount1 = 0f
            _eCount2 = 0f
            _eCount3 = 0f
            _eCount4 = 0f
            _eCount5 = 0f
            getMonthDailyEmojis(getAllDailyEmojis) // 초기화 후 월별 이모지 데이터 GET

            val getAllDailyWeather = repository.getAllDailyWeather().filter {
                it.date.year == _curYear && it.date.monthValue == _curMonth
            }
            _wCount1 = 0.1f
            _wCount2 = 0.1f
            _wCount3 = 0.1f
            _wCount4 = 0.1f
            getMonthDailyWeather(getAllDailyWeather) // 초기화 후 월별 날씨 데이터 GET
        }
    }

    // 월별 각 이모지의 데이터(y값) 계산 함수
    private fun getMonthDailyEmojis(getAllDailyEmojis: List<DailyEmojis>){
        getAllDailyEmojis.filter {
            when (it.emojiState) {
                EmojiStates.VERY_BAD -> _eCount1 += 1.0f
                EmojiStates.BAD -> _eCount2 += 1.0f
                EmojiStates.FINE -> _eCount3 += 1.0f
                EmojiStates.GOOD -> _eCount4 += 1.0f
                EmojiStates.VERY_GOOD -> _eCount5 += 1.0f
                else -> print("default")
            }
            true
        }
    }

    // 월별 각 날씨의 데이터(y값) 계산 함수
    private fun getMonthDailyWeather(getAllDailyWeather: List<DailyWeather>){
        getAllDailyWeather.filter{
            when(it.weatherState){
                WeatherStates.SNOWY -> _wCount1 += 1.0f
                WeatherStates.CLOUDY -> _wCount2 += 1.0f
                WeatherStates.SUNNY -> _wCount3 += 1.0f
                WeatherStates.RAINY -> _wCount4 += 1.0f
                else -> print("default")
            }
            true
        }
    }

    // 이전 달 상태 변경 함수
    fun previousMonth() {
        viewModelScope.launch {
            if (_curMonth == 1) {
                _curYear -= 1
                _curMonth = 13
            }
            _curMonth -= 1
            initData()
        }
    }

    // 다음 달 상태 변경 함수
    fun nextMonth() {
        viewModelScope.launch {
            if (_curMonth == 12) {
                _curYear += 1
                _curMonth = 0
            }
            _curMonth += 1
            initData()
        }
    }
}