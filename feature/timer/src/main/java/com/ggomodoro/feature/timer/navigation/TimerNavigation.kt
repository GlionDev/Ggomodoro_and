package com.ggomodoro.feature.timer.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.ggomodoro.feature.timer.TimerRoute

const val TIMER_ROUTE = "timer_route"

fun NavController.navigateToTimer(navOptions: NavOptions? = null) {
    this.navigate(TIMER_ROUTE, navOptions)
}

fun NavGraphBuilder.timerScreen() {
    composable(route = TIMER_ROUTE) {
        TimerRoute()
    }
}
