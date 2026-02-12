package com.ggomodoro.feature.timer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ggomodoro.feature.timer.service.TimerState
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

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
                .weight(0.6f)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            when (state) {
                is TimerState.Idle -> CircularTimerSelector(onStartTimer = onStartTimer)
                is TimerState.Running -> CircularTimerProgress(
                    state = state,
                    onStopTimer = onStopTimer
                )
            }
        }
        
        // Additional UI below if needed (e.g. text instructions)
    }
}

/**
 * 타이머 시간을 설정하는 원형 선택기 UI입니다.
 * 드래그 제스처를 통해 시간을 설정합니다.
 *
 * @param onStartTimer 설정 완료 시 호출되는 콜백
 */
@Composable
fun CircularTimerSelector(
    onStartTimer: (Int) -> Unit
) {
    // ... implementation ...
    var angle by remember { mutableFloatStateOf(0f) }
    var durationMinutes by remember { mutableStateOf(0) }
    var isDragging by remember { mutableStateOf(false) }

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = {
                        isDragging = false
                        onStartTimer(durationMinutes)
                    },
                    onDragCancel = { isDragging = false }
                ) { change, _ ->
                    val center = Offset(size.width / 2f, size.height / 2f)
                    val touchPoint = change.position
                    
                    // Simple logic: if touch is roughly within ring, update angle
                    // For robustness, calculate angle from center
                    val degrees = Math.toDegrees(
                        atan2(
                            (run { touchPoint.y - center.y }).toDouble(),
                            (run { touchPoint.x - center.x }).toDouble()
                        )
                    ).toFloat() + 90f
                    
                    val normalizeDegrees = if (degrees < 0) degrees + 360f else degrees
                    angle = normalizeDegrees
                    
                    // Map 0-360 to 0-60 minutes
                    val minutes = ((angle / 360f) * 60).toInt()
                    durationMinutes = maxOf(1, minutes)
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 40.dp.toPx()
            val radius = size.minDimension / 2 - strokeWidth
            
            // Background Ring
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.3f),
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // Selected Arc
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
        
        Text(
            text = "$durationMinutes min",
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

/**
 * 진행 중인 타이머 상태를 표시하는 원형 프로그레스 UI입니다.
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
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
            .pointerInput(Unit) {
                // Long press to stop
                detectDragGestures(
                    onDragStart = { /* No op */ },
                    onDragEnd = { /* No op */ }
                ) { _, _ -> /* No drag */ }
            }
            .pointerInput(Unit) {
               // detectTapGestures(onLongPress = { onStopTimer() }) 
               // Combined with dialog, but for now simple long press action
            }
    ) {
        // Just using a button for now to stop as long press on canvas is tricky to discover without hints
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 40.dp.toPx()
            val radius = size.minDimension / 2 - strokeWidth
            
            drawCircle(
                color = Color.LightGray.copy(alpha = 0.3f),
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            val sweep = 360f * state.progress
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
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val minutes = state.remainingSeconds / 60
            val seconds = state.remainingSeconds % 60
            Text(
                text = String.format("%02d:%02d", minutes, seconds),
                style = MaterialTheme.typography.displayLarge
            )
            androidx.compose.material3.Button(onClick = onStopTimer) {
                Text("Stop")
            }
        }
    }
}
