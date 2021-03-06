package com.oss.diaring.presentation.diary

import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.oss.diaring.R
import com.oss.diaring.databinding.FragmentDiaryListBinding
import com.oss.diaring.presentation.base.BaseFragment
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import com.oss.diaring.data.database.DiaryDatabase
import com.oss.diaring.data.database.entity.DailyEmojis
import com.oss.diaring.data.database.entity.DailyWeather
import com.oss.diaring.data.database.entity.Diary
import kotlinx.coroutines.delay
import timber.log.Timber
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import com.oss.diaring.util.EmojiStates
import com.oss.diaring.util.WeatherStates

class DiaryListFragment : BaseFragment<FragmentDiaryListBinding>(R.layout.fragment_diary_list) {

    private var currentPageLocalDate: LocalDate = LocalDate.now()
    private var recentlyVisitedDiaryDate: Int = localDateToIntId(LocalDate.now())

    private var diariesMap: MutableMap<LocalDate, Diary> = mutableMapOf()
    private var diariesIndexes: MutableList<Int> = mutableListOf(0)
    private lateinit var db: DiaryDatabase

    private lateinit var defaultBitmapImage : Bitmap
    private lateinit var diaryListAdapter: DiaryListAdapter // by lazy { DiaryListAdapter() }

    private var mBinding: FragmentDiaryListBinding? = null
    private val diaryListMBinding get() = mBinding!!

    private val mHandler: Handler = Handler(Looper.getMainLooper())

    private var viewAll: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        db = Room.databaseBuilder(requireContext(), DiaryDatabase::class.java, "diary_database.db").build()
        // Inflate the layout for this fragment
        mBinding = FragmentDiaryListBinding.inflate(inflater, container, false)

        val args: DiaryListFragmentArgs by navArgs()

        if (args.diaryIndex != 0) {
            recentlyVisitedDiaryDate = args.diaryIndex
            currentPageLocalDate = intIdToLocalDate(recentlyVisitedDiaryDate)
        }
        if (args.diariesIndexes != null) {
            this.diariesIndexes = args.diariesIndexes!!.toMutableList()
            this.diariesIndexes.sort()
        }

        bindViews()

        // Insert sample database
        GlobalScope.launch {
            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_diary_default_image, null)
            defaultBitmapImage = drawable!!.toBitmap()
            if (db.diaryDao().getDiaryById(20220302)==null) {
                db.diaryDao().insertDiary( Diary(20220302, "??? ??????", LocalDate.of(2022,3,2), "?????? ???", listOf(), "??? ?????? ??????", 2, 5, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220315, "????????? ??????", LocalDate.of(2022,3,15), "?????? ???", listOf(), "??? ?????? ??????", 2, 5, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220321, "?????? ?????????", LocalDate.of(2022,3,21), "?????? ???", listOf(), "??? ?????? ??????", 4, 4, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220403, "????????????", LocalDate.of(2022,4,3), "?????? ???", listOf(), "??? ?????? ??????", 2, 3, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220405, "?????????", LocalDate.of(2022,4,5), "?????? ???", listOf(), "??? ?????? ?????????", 3, 5, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220410, "?????? ??????", LocalDate.of(2022,4,10), "?????? ???", listOf(), "??? ?????? ?????????", 3, 5, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220412, "??????", LocalDate.of(2022,4,12), "?????? ???", listOf(), "??? ?????? ?????????", 3, 2, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220422, "????????? ???", LocalDate.of(2022,4,22), "?????? ???", listOf(), "????????? ?????????", 4, 3, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220426, "?????? ??????", LocalDate.of(2022,4,26), "?????? ???", listOf(), "????????? ??? ???????????????", 3, 1, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220430, "???????????????", LocalDate.of(2022,4,30), "?????? ???", listOf(), "?????? ????????? ?????????", 2, 1, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220511, "?????????", LocalDate.of(2022,5,11), "?????? ???", listOf(), "???????????????", 1, 4, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220515, "????????? ??????", LocalDate.of(2022,5,15), "?????? ???", listOf(), "?????? ????????? ????????????. ????????? ????????? ?????????\n\n\n\n??????????????????\n\n\n\n\n\n??????????????????\n\n\n\n\n\n??????????????????\n\n", 3, 1, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220516, "????????? ?????????", LocalDate.of(2022,5,16), "????????? ??????", listOf(), "????????????\n \n", 4, 2, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220517, "?????? ??? ??????", LocalDate.of(2022,5,17), "?????? ?????? ???", listOf(), "??? ?????? ?????? ????????????", 3, 3, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220518, "??? ?????????", LocalDate.of(2022,5,18), "??????... ????", listOf(), "\"????????????\"", 2, 5, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220610, "????????????", LocalDate.of(2022,6,10), "?????? ???", listOf(), "??? ?????? ??????", 3, 4, defaultBitmapImage) )
                db.diaryDao().insertDiary( Diary(20220612, "?????? ?????????", LocalDate.of(2022,6,12), "?????? ???", listOf(), "?????????!", 1, 5, defaultBitmapImage) )
                delay(100)
            }

            loadAllDiaryFromDao()
        }

        ObjectAnimator.ofFloat(diaryListMBinding.root, View.ALPHA, -0f, 1f).apply {
            duration = 700
            start()
//            }
        }

        runDelayed(300) {
            initDiaryListRecyclerView(currentPageLocalDate)
        }

        if (diariesMap.isEmpty()) {
            runDelayed(1500) {
                try {
                    initDiaryListRecyclerView(currentPageLocalDate)
                } catch (e: Exception) {
                    Timber.e(e.toString())
                }
            }
        }

        return diaryListMBinding.root
    }

    private fun bindViews() {
        diaryListMBinding.diaryListBackArrow.setOnClickListener {
            currentPageLocalDate =
                if (currentPageLocalDate.monthValue == 1) {
                    LocalDate.of(currentPageLocalDate.year-1, 12, currentPageLocalDate.dayOfMonth)
                } else {
                    LocalDate.of(currentPageLocalDate.year, currentPageLocalDate.monthValue-1, currentPageLocalDate.dayOfMonth)
                }
            loadDateText()
            initDiaryListRecyclerView(currentPageLocalDate)
        }

        diaryListMBinding.diaryListForwardArrow.setOnClickListener {
            currentPageLocalDate =
                if (currentPageLocalDate.monthValue == 12) {
                    LocalDate.of(currentPageLocalDate.year+1, 1, currentPageLocalDate.dayOfMonth)
                } else {
                    LocalDate.of(currentPageLocalDate.year, currentPageLocalDate.monthValue+1, currentPageLocalDate.dayOfMonth)
                }
            loadDateText()
            initDiaryListRecyclerView(currentPageLocalDate)
        }

        diaryListMBinding.diaryButton.setOnClickListener {
            val cal = Calendar.getInstance()    //???????????? ?????????
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                currentPageLocalDate = LocalDate.of(year, month+1, dayOfMonth)
                recentlyVisitedDiaryDate = localDateToIntId(currentPageLocalDate)

                val navHostFragment =
                    activity?.supportFragmentManager?.findFragmentById(R.id.fcv_main) as NavHostFragment
                val action = DiaryListFragmentDirections.actionToDiary(diariesIndexes.toIntArray(), recentlyVisitedDiaryDate)
                navHostFragment.navController.navigate(action)
            }
            DatePickerDialog(requireContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
//            DatePickerDialog(requireContext(), R.style.DiaryListCalendarView, dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        diaryListMBinding.mainTitle.setOnClickListener {
            val cal = Calendar.getInstance()    //???????????? ?????????
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                currentPageLocalDate = LocalDate.of(year, month+1, dayOfMonth)
                recentlyVisitedDiaryDate = localDateToIntId(currentPageLocalDate)

                initDiaryListRecyclerView(currentPageLocalDate)
            }
            DatePickerDialog(requireContext(), AlertDialog.THEME_HOLO_DARK, dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        diaryListMBinding.diaryListCheckButton.setOnClickListener {
            viewAll = !viewAll
            if (!viewAll) { diaryListMBinding.diaryListCheckButton.alpha = 1f }
            else { diaryListMBinding.diaryListCheckButton.alpha = 0.3f }
            initDiaryListRecyclerView(currentPageLocalDate)
        }
    }

    private fun loadDiaryListData(ld:LocalDate, viewAllDate:Boolean): ArrayList<DiaryListData> {
        var dataList: ArrayList<DiaryListData> = arrayListOf()
//        for (_dayOfYear in 1..LocalDate.of(ld.year, 12, 31).dayOfYear) {
        for (_dayOfMonth in 1..ld.lengthOfMonth()) {
            val data: Diary? =
                diariesMap[LocalDate.of(ld.year, ld.monthValue, _dayOfMonth)]// ?: continue
            if (data != null) {
                if (currentPageLocalDate.monthValue == data.date.monthValue) {
                    dataList.add(
                        DiaryListData(data.date, data.title, data.weather, data.emotion)
                    )
                }
            } else if (viewAllDate) {
                if (currentPageLocalDate.monthValue == ld.monthValue) {
                    dataList.add(
                        DiaryListData(LocalDate.of(ld.year, ld.monthValue, _dayOfMonth), "", 0, 0)
                    )
                }
            }
        }
        return dataList
    }

    private fun initDiaryListRecyclerView(ld:LocalDate) {

        diaryListMBinding.mainTitle.text = localDateToFormattedString(ld)

        val dataList = loadDiaryListData(ld, viewAll)

        if (dataList.isNotEmpty()) {
            mHandler.removeCallbacksAndMessages(null)
        }
        diaryListAdapter = DiaryListAdapter(
            this.requireContext(),
            dataList,
            onClickListener = this::openDiary
        )
        diaryListMBinding.diaryListView.adapter = diaryListAdapter

        if (ld.year == LocalDate.now().year) {
            if (ld.monthValue == LocalDate.now().monthValue) {
                diaryListMBinding.diaryListForwardArrow.alpha = 0f
            } else {
                diaryListMBinding.diaryListForwardArrow.alpha = 1f
            }
        }
        ObjectAnimator.ofFloat(diaryListMBinding.mainTitle, View.ALPHA, 0f, 1f).apply {
            duration = 400
            start()
        }
        ObjectAnimator.ofFloat(diaryListMBinding.diaryListView, View.ALPHA, -1f, 1f).apply {
            duration = 800
            start()
        }

        val lm = LinearLayoutManager(this.requireContext())
        diaryListMBinding.diaryListView.layoutManager = lm
        diaryListMBinding.diaryListView.setHasFixedSize(true)
    }

    private fun loadDateText() {
        ObjectAnimator.ofFloat(diaryListMBinding.mainTitle, View.ALPHA, -1f, 1f).apply {
            duration = 500
            start()
        }
        if (currentPageLocalDate.monthValue == LocalDate.now().monthValue) {
            diaryListMBinding.diaryListForwardArrow.isClickable = false
            diaryListMBinding.diaryListForwardArrow.alpha = 0f
        } else {
            diaryListMBinding.diaryListForwardArrow.isClickable = true
            diaryListMBinding.diaryListForwardArrow.alpha = 1f
        }
    }

    private fun openDiary(view: View, diaryListData: DiaryListData) {
        val navHostFragment =
            activity?.supportFragmentManager?.findFragmentById(R.id.fcv_main) as NavHostFragment
        recentlyVisitedDiaryDate = localDateToIntId(diaryListData.date)
//        Timber.d(recentlyVisitedDiaryDate.toString())
//        Timber.d("onopen %s", diariesMap.get(currentPageLocalDate)?.image.toString())
        val action = DiaryListFragmentDirections.actionToDiary(diariesIndexes.toIntArray(), recentlyVisitedDiaryDate)
        navHostFragment.navController.navigate(action)
    }

    private fun loadAllDiaryFromDao() {
        GlobalScope.launch {
            val crv = db.diaryDao().getAllDiaries()
            diariesMap = crv.associateBy { it.date }.toMutableMap().toSortedMap()
//            diariesIndexes.addAll
            diariesIndexes.clear()
            for (element in diariesMap) {
                diariesIndexes.add(element.value.no)
            }
        }
    }

    private fun intIdToLocalDate(id: Int): LocalDate {
        if (id == 0) return LocalDate.now()
        else return LocalDate.of(id/10000, id%10000/100, id%100)
    }

    private fun localDateToIntId(ld: LocalDate): Int {
        return ld.year*10000 + ld.monthValue*100 + ld.dayOfMonth
    }

    private fun localDateToFormattedString(ld: LocalDate): String {
        return "${String.format("%02d", ld.year)}${getString(R.string.plain_year)} ${String.format("%02d", ld.monthValue)}${getString(R.string.monday)}"
    }

    private lateinit var backPressedCallback : OnBackPressedCallback
    private var doubleBackToExit = false
    fun onBackPressed() {
        if (doubleBackToExit) {
            val navHostFragment =
                activity?.supportFragmentManager?.findFragmentById(R.id.fcv_main) as NavHostFragment
            navHostFragment.navController.navigate(R.id.navigation_diary_list)
            activity?.window?.statusBarColor = resources.getColor(R.color.primary_blue)
        } else {
            Toast.makeText(this.requireContext(), "To exit, click Back again", Toast.LENGTH_SHORT)
                .show()
            doubleBackToExit = true
            runDelayed(1500L) {
                doubleBackToExit = false
            }
        }
    }

    private fun runDelayed(millis: Long, function: () -> Unit) {
        mHandler.postDelayed(function, millis)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // sample_text.text = "occur back pressed event!!"
                onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
    }

    override fun onDetach() {
        super.onDetach()
        backPressedCallback.remove()
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }
}

