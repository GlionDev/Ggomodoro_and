package com.ggomodoro.core.notification.di

import com.ggomodoro.core.notification.NotificationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    // NotificationHelper is already @Singleton and @Inject, so Hilt knows how to provide it.
    // But explicit provision is fine too if we needed interface binding.
    // We can just rely on the class annotation since it has an @Inject constructor.
}
