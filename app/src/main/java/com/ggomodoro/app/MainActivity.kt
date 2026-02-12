package com.ggomodoro.app

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
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

        val array = IntArray(5) { 1 }
        array.map { it ->

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
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (!isGranted) {
                showPermissionDialog = true
            }
        }
    )

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!hasPermission) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text(text = "권한 요청") },
            text = { Text(text = "남은시간 알림을 위해 \"알림 권한\" 허용이 필요합니다.\n확인을 누르시면 설정으로 이동합니다.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        ).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    }
                ) { Text("확인") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPermissionDialog = false }
                ) { Text("취소") }
            }
        )
    }
    
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
