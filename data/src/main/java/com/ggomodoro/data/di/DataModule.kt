package com.ggomodoro.data.di

import com.ggomodoro.data.repository.HistoryRepositoryImpl
import com.ggomodoro.data.repository.TimerRepositoryImpl
import com.ggomodoro.domain.repository.HistoryRepository
import com.ggomodoro.domain.repository.TimerRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {
    @Binds
    fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository

    @Binds
    fun bindTimerRepository(impl: TimerRepositoryImpl): TimerRepository
}
