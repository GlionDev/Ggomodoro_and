package com.ggomodoro.core.common.dispatcher.di

import com.ggomodoro.core.common.dispatcher.DefaultDispatcherProvider
import com.ggomodoro.core.common.dispatcher.DispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DispatchersModule {
    @Binds
    fun bindDispatcherProvider(impl: DefaultDispatcherProvider): DispatcherProvider
}
