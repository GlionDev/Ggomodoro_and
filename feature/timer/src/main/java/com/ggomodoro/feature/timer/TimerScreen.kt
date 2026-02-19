package com.ggomodoro.feature.timer

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.WindowManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val context = LocalContext.current
    val vibrator = remember(context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    var angle by remember { mutableFloatStateOf(0f) }
    var durationMinutes by remember { mutableIntStateOf(0) }
    var isDragging by remember { mutableStateOf(false) }

    val cs = MaterialTheme.colorScheme

    val trackColor = cs.primary                  // Yellow
    val filledColor = cs.secondary               // Red
    val knobColor = cs.surface                   // ✅ was onSecondary
    val knobShadow = Color.Black.copy(alpha = 0.12f)
    val textColor = cs.onBackground

    val hintColor = cs.onSurfaceVariant.copy(alpha = 0.55f) // ✅ was primary.copy(alpha=0.5f)


    var previousAngle by remember { mutableFloatStateOf(0f) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Hint Area
        Box(
            modifier = Modifier.height(60.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (!isDragging) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "SWIPE TO SET",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = hintColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Start Here",
                        tint = hintColor
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val center = Offset(size.width / 2f, size.height / 2f)
                            val strokeWidthVal = 50.dp.toPx() // Thicker stroke
                            val radius = (minOf(size.width, size.height) - strokeWidthVal) / 2f

                            val startTarget = Offset(center.x, center.y - radius)

                            val distance = sqrt(
                                (offset.x - startTarget.x).pow(2) + (offset.y - startTarget.y).pow(2)
                            )

                            if (distance < strokeWidthVal * 2f) { // Generous hit area
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

                            if (previousAngle < 90f && normalizeDegrees > 270f) {
                                return@detectDragGestures
                            }

                            previousAngle = normalizeDegrees
                            angle = normalizeDegrees

                            val minutes = ((angle / 360f) * 60).toInt()
                            val newDuration = maxOf(0, minutes)

                            if (newDuration != durationMinutes) {
                                durationMinutes = newDuration
                                // Haptic feedback (Tick)
                                val vibrationEffect = VibrationEffect.createOneShot(
                                    30L,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                                )

                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    val attributes = VibrationAttributes.Builder()
                                        .setUsage(VibrationAttributes.USAGE_TOUCH)
                                        .build()
                                    vibrator.vibrate(vibrationEffect, attributes)
                                } else {
                                    @Suppress("DEPRECATION")
                                    vibrator.vibrate(vibrationEffect)
                                }
                            }
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 50.dp.toPx() // Toy-like thickness
                val radius = (minOf(size.width, size.height) - strokeWidth) / 2f
                val center = Offset(size.width / 2, size.height / 2)
                
                // Track (Pastel Yellow)
                drawCircle(
                    color = trackColor,
                    radius = radius,
                    style = Stroke(width = strokeWidth)
                )

                if (isDragging && angle > 0) {
                    drawArc(
                        color = filledColor,
                        startAngle = -90f,
                        sweepAngle = angle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        size = Size(radius * 2, radius * 2),
                        topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2)
                    )
                }
                
                // Knob / Indicator
                val knobAngleRadians = Math.toRadians((angle - 90).toDouble())
                val knobX = center.x + radius * cos(knobAngleRadians).toFloat()
                val knobY = center.y + radius * sin(knobAngleRadians).toFloat()
                
                // Shadow for Knob
                drawCircle(
                    color = knobShadow,
                    radius = 20.dp.toPx(),
                    center = Offset(knobX, knobY + 4.dp.toPx())
                )
                
                // Knob Body
                drawCircle(
                    color = knobColor,
                    radius = 18.dp.toPx(),
                    center = Offset(knobX, knobY)
                )

                drawCircle(
                    color = cs.outline.copy(alpha = 0.35f),
                    radius = 18.dp.toPx(),
                    center = Offset(knobX, knobY),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${durationMinutes}",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Black
                    ),
                    color = textColor
                )
                Text(
                    text = "min",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = textColor.copy(alpha = 0.6f)
                )
            }
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
    val trackColor = MaterialTheme.colorScheme.primary // ToyYellow (Pastel Yellow)
    val filledColor = MaterialTheme.colorScheme.secondary
    val textColor = MaterialTheme.colorScheme.onBackground
    var showStopDialog by remember { mutableStateOf(false) }

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            title = { Text(text = "타이머 정지", fontWeight = FontWeight.Bold) },
            text = { Text(text = "정말 그만하시겠습니까?", style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showStopDialog = false
                        onStopTimer()
                    }
                ) { Text("예", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.secondary) }
            },
            dismissButton = {
                TextButton(
                    onClick = { showStopDialog = false }
                ) { Text("아니요", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
    
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f)
    ) {
        // Remaining Time (Minutes:Seconds)
        val remainingDesc = "${state.remainingSeconds / 60}:${String.format("%02d", state.remainingSeconds % 60)}"
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 50.dp.toPx() // Toy thickness
            val radius = (minOf(size.width, size.height) - strokeWidth) / 2f
            
            // Background Ring (Trace in Pastel Yellow)
            drawCircle(
                color = trackColor,
                radius = radius,
                style = Stroke(width = strokeWidth)
            )

            // 진행률 표시 (남은 시간 / 60분)
            val maxSeconds = 60 * 60f
            val sweep = (state.remainingSeconds / maxSeconds) * 360f
            
            drawArc(
                color = filledColor,
                startAngle = -90f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                size = Size(radius * 2, radius * 2),
                topLeft = Offset((size.width - radius * 2) / 2, (size.height - radius * 2) / 2)
            )
        }
        
        // Stop Button (Chunky)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { showStopDialog = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background, // White button
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp),
                modifier = Modifier
                    .height(56.dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.secondary, // red border
                        shape = RoundedCornerShape(28.dp) // Match default dialog shape
                    ),
                // TODO: Add shadow if possible (requires custom modifier or libs)
            ) {
                Text("GIVE UP", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun KeepScreenOn() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}
