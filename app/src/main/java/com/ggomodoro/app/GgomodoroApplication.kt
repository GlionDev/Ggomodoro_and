package com.ggomodoro.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Ggomodoro 애플리케이션의 엔트리 포인트입니다.
 * Hilt DI를 초기화합니다.
 */
@HiltAndroidApp
class GgomodoroApplication : Application()
