package com.ggomodoro.core.database.di

import android.content.Context
import androidx.room.Room
import com.ggomodoro.core.database.GgomodoroDatabase
import com.ggomodoro.core.database.dao.TimerSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideGgomodoroDatabase(@ApplicationContext context: Context): GgomodoroDatabase {
        return Room.databaseBuilder(
            context,
            GgomodoroDatabase::class.java,
            "ggomodoro.db"
        ).build()
    }

    @Provides
    fun provideTimerSessionDao(database: GgomodoroDatabase): TimerSessionDao {
        return database.timerSessionDao()
    }
}
