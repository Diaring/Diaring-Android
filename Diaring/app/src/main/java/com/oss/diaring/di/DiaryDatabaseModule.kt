package com.oss.diaring.di

import android.content.Context
import androidx.room.Room
import com.oss.diaring.data.database.DiaryDatabase
import com.oss.diaring.data.database.dao.DiaryDao
import com.oss.diaring.data.datasource.DiaryDataSource
import com.oss.diaring.data.datasource.DiaryDataSourceImpl
import com.oss.diaring.data.repository.DiaryRepository
import com.oss.diaring.data.repository.DiaryRepositoryImpl
import com.oss.diaring.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DiaryDatabaseModule {

    @Singleton
    @Provides
    fun provideDiaryDatabase(@ApplicationContext context: Context): DiaryDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            DiaryDatabase::class.java,
            Constants.DIARY_DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDiaryDao(diaringDatabase: DiaryDatabase): DiaryDao {
        return diaringDatabase.diaryDao()
    }

    @Provides
    fun provideDiaryDataSource(diaryDao: DiaryDao): DiaryDataSource {
        return DiaryDataSourceImpl(diaryDao)
    }

    @Singleton
    @Provides
    fun provideDiaryRepository(diaryDataSource: DiaryDataSource): DiaryRepository {
        return DiaryRepositoryImpl(diaryDataSource)
    }

}