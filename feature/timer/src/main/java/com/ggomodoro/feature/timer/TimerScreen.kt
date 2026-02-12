package com.ggomodoro.feature.timer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ggomodoro.feature.timer.service.TimerState
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * 타이머 화면의 루트 Composable입니다.
 * ViewModel에서 상태를 수집하고 하위 Composable에 전달합니다.
 *
 * @param viewModel 타이머 ViewModel
 */
@Composable
fun TimerRoute(
    viewModel: TimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    TimerScreen(
        state = uiState,
        onStartTimer = viewModel::startTimer,
        onStopTimer = viewModel::stopTimer
    )
}

/**
 * 타이머 화면의 메인 UI를 구성하는 Composable입니다.
 * 상태에 따라 [CircularTimerSelector] 또는 [CircularTimerProgress]를 표시합니다.
 *
 * @param state 현재 타이머 상태
 * @param onStartTimer 타이머 시작 콜백 (분)
 * @param onStopTimer 타이머 정지 콜백
 */
@Composable
fun TimerScreen(
    state: TimerState,
    onStartTimer: (Int) -> Unit,
    onStopTimer: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp), // 좌우 여백 16dp
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is TimerState.Idle -> CircularTimerSelector(onStartTimer = onStartTimer)
                is TimerState.Running -> {
                    KeepScreenOn()
                    CircularTimerProgress(
                        state = state,
                        onStopTimer = onStopTimer
                    )
                }
            }
        }
    }
}

/**
 * 타이머 시간을 설정하는 원형 선택기 UI입니다.
 * 드래그 제스처를 통해 시간을 설정합니다. 시작점은 12시 방향이어야 합니다.
 *
 * @param onStartTimer 설정 완료 시 호출되는 콜백
 */
@Composable
fun CircularTimerSelector(
    onStartTimer: (Int) -> Unit
) {
    var angle by remember { mutableFloatStateOf(0f) }
    var durationMinutes by remember { mutableStateOf(0) }
    var isDragging by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val indicatorColor = MaterialTheme.colorScheme.onSurfaceVariant

    var previousAngle by remember { mutableFloatStateOf(0f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Hint Area
        Box(
            modifier = Modifier.height(40.dp), // Reserved height
            contentAlignment = Alignment.BottomCenter
        ) {
            if (!isDragging) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Swipe Start!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Start Here",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            // Use the same radius logic as drawing
                            val strokeWidthVal = 40.dp.toPx()
                            val radius = (minOf(size.width, size.height) - strokeWidthVal) / 2f
                            
                            val startTarget = Offset(center.x, center.y - radius) 
                            
                            val distance = sqrt(
                                (offset.x - startTarget.x).pow(2) + (offset.y - startTarget.y).pow(2)
                            )
                            
                            // Hit test radius can be generous (e.g. 1.5x stroke width)
                            if (distance < strokeWidthVal * 1.5f) {
                                isDragging = true
                            }
                        },
                        onDragEnd = {
                            if (isDragging && durationMinutes > 0) {
                                onStartTimer(durationMinutes)
                            }
                            isDragging = false
                            angle = 0f
                            durationMinutes = 0
                        },
                        onDragCancel = {
                            isDragging = false
                            angle = 0f
                            durationMinutes = 0
                        }
                    ) { change, _ ->
                        if (isDragging) {
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val touchPoint = change.position
                            
                            val degrees = Math.toDegrees(
                                atan2(
                                    (touchPoint.y - center.y).toDouble(),
                                    (touchPoint.x - center.x).toDouble()
                                )
                            ).toFloat() + 90f
                            
                            
                            val normalizeDegrees = if (degrees < 0) degrees + 360f else degrees
                            
                            // Blocking Logic:
                            // If we jump from a small angle (near 0) to a large angle (near 360), it's a CCW wrap.
                            // We block this.
                            // Condition: previous was near start (0-90) AND current is near end (270-360).
                            if (previousAngle < 90f && normalizeDegrees > 270f) {
                                return@detectDragGestures
                            }
                            
                            previousAngle = normalizeDegrees
                            angle = normalizeDegrees
                            
                            val minutes = ((angle / 360f) * 60).toInt()
                            durationMinutes = maxOf(0, minutes)
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 40.dp.toPx() // Reverted to 40dp
                // Radius to fill box: (size - stroke) / 2
                val radius = (minOf(size.width, size.height) - strokeWidth) / 2f
                
                drawCircle(
                    color = trackColor.copy(alpha = 0.3f),
                    radius = radius,
                    style = Stroke(width = strokeWidth)
                )

                if (isDragging && angle > 0) {
                    drawArc(
                        color = primaryColor,
                        startAngle = -90f,
                        sweepAngle = angle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(radius * 2, radius * 2),
                        topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2)
                    )
                }
                
                if (!isDragging) {
                   val topCenter = Offset(size.width / 2, strokeWidth / 2) // Visual Top
                   // Actually, with new radius calc, the stroke center is at `radius` distance from center.
                   // Visual top edge is at `center.y - radius - stroke/2` = `center.y - (size-stroke)/2 - stroke/2` = `center.y - size/2 + stroke/2 - stroke/2` = `center.y - size/2`.
                   // If size = height, then `center.y - height/2` = 0.
                   // So visually it touches the top.
                   
                   drawCircle(
                       color = primaryColor,
                       radius = 10.dp.toPx(), // Indicator size
                       center = Offset(size.width / 2, (size.height - radius * 2) / 2) 
                   )
                }
            }
            
            Text(
                text = "${durationMinutes} min",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

/**
 * 진행 중인 타이머 상태를 표시하는 원형 프로그레스 UI입니다.
 * 60분 시계를 기준으로 남은 시간만큼의 원호를 표시합니다.
 *
 * @param state 실행 중인 타이머 상태 정보
 * @param onStopTimer 타이머 중지 콜백
 */
@Composable
fun CircularTimerProgress(
    state: TimerState.Running,
    onStopTimer: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    var showStopDialog by remember { mutableStateOf(false) }

    if (showStopDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showStopDialog = false },
            title = { Text(text = "타이머 정지") },
            text = { Text(text = "정말 그만하시겠습니까?") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showStopDialog = false
                        onStopTimer()
                    }
                ) { Text("예") }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showStopDialog = false }
                ) { Text("아니요") }
            }
        )
    }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 40.dp.toPx() // Reverted to 40dp
            val radius = (minOf(size.width, size.height) - strokeWidth) / 2f
            
            // Background Ring (Trace)
            drawCircle(
                color = trackColor.copy(alpha = 0.3f),
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // 진행률 표시 (남은 시간 / 60분)
            val maxSeconds = 60 * 60f
            val sweep = (state.remainingSeconds / maxSeconds) * 360f
            
            drawArc(
                color = primaryColor,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2)
            )
        }
        
        // Stop Button Centered (No Text)
        Button(onClick = { showStopDialog = true }) {
            Text("Stop")
        }
    }
}

@Composable
fun KeepScreenOn() {
    val context = androidx.compose.ui.platform.LocalContext.current
    androidx.compose.runtime.DisposableEffect(Unit) {
        val window = (context as? android.app.Activity)?.window
        window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
