package com.ggomodoro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.ggomodoro.core.designsystem.theme.GgomodoroTheme
import com.ggomodoro.feature.history.navigation.HISTORY_ROUTE
import com.ggomodoro.feature.history.navigation.historyScreen
import com.ggomodoro.feature.history.navigation.navigateToHistory
import com.ggomodoro.feature.timer.navigation.TIMER_ROUTE
import com.ggomodoro.feature.timer.navigation.navigateToTimer
import com.ggomodoro.feature.timer.navigation.timerScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Ggomodoro 앱의 메인 진입점 Activity입니다.
 * 전체적인 네비게이션 구조를 설정합니다.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GgomodoroTheme {
                GgomodoroAppContent()
            }
        }
    }
}

/**
 * 앱의 주요 UI 콘텐츠를 구성하는 Composable 함수입니다.
 * Bottom Navigation과 NavHost를 포함합니다.
 */
@Composable
fun GgomodoroAppContent() {
    val navController = rememberNavController()
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == TIMER_ROUTE } == true,
                    onClick = {
                        navController.navigateToTimer(navOptions {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        })
                    },
                    icon = { Icon(painter = painterResource(android.R.drawable.ic_menu_recent_history), contentDescription = "Timer") }, // Todo: Use proper icons
                    label = { Text("Timer") }
                )
                
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == HISTORY_ROUTE } == true,
                    onClick = {
                        navController.navigateToHistory(navOptions {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        })
                    },
                    icon = { Icon(painter = painterResource(android.R.drawable.ic_menu_save), contentDescription = "History") }, // Todo: Use proper icons
                    label = { Text("History") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = TIMER_ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {
            timerScreen()
            historyScreen()
        }
    }
}
