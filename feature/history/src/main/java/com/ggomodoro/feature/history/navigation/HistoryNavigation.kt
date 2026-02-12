package com.ggomodoro.feature.history.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.ggomodoro.feature.history.HistoryRoute

const val HISTORY_ROUTE = "history_route"

fun NavController.navigateToHistory(navOptions: NavOptions? = null) {
    this.navigate(HISTORY_ROUTE, navOptions)
}

fun NavGraphBuilder.historyScreen() {
    composable(route = HISTORY_ROUTE) {
        HistoryRoute()
    }
}
