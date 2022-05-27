package com.oss.diaring.di

import com.oss.diaring.data.database.DiaryDatabase
import com.oss.diaring.data.sharedpreference.SharedPrefManagerImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

//    single {
//        DiaryDatabase.getDatabaseInstance(androidApplication())
//    }
//
//    single {
//        get<DiaryDatabase>().diaryDao()
//    }

    single<SharedPrefManagerImpl> {
        SharedPrefManagerImpl(get())
    }

    // Repository
//    single<DiaryRepository> {
//        DiaryRepository(get())
//    }

    // ViewModel
//    viewModel {
//        CalendarViewModel(get())
//    }

    // DataSourceModule
//    single<DiaryDataSource> {
//        DiaryDataSource(get())
//    }
}