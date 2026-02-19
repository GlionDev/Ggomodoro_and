package com.ggomodoro.feature.timer.service

import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.ggomodoro.core.notification.NotificationHelper
import com.ggomodoro.domain.model.SessionStatus
import com.ggomodoro.domain.model.TimerSession
import com.ggomodoro.domain.repository.TimerRepository
import com.ggomodoro.domain.usecase.SaveSessionUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

/**
 * 타이머 로직을 백그라운드에서 실행하고 알림을 관리하는 서비스입니다.
 * 화면이 꺼지거나 앱이 백그라운드로 전환되어도 타이머가 계속 동작하도록 보장합니다.
 */
@AndroidEntryPoint
class TimerService : Service() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var timerRepository: TimerRepository

    @Inject
    lateinit var saveSessionUseCase: SaveSessionUseCase

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var timerJob: Job? = null

    private val _timerState = MutableStateFlow<TimerState>(TimerState.Idle)
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val binder = LocalBinder()

    inner class LocalBinder : Binder() {
        fun getService(): TimerService = this@TimerService
    }

    override fun onBind(intent: Intent?): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_TIMER -> stopTimer(canceled = true)
        }
        return START_NOT_STICKY
    }

    /**
     * 타이머를 시작합니다.
     *
     * @param durationMinutes 설정된 타이머 시간 (분 단위)
     */
    fun startTimer(durationMinutes: Int) {
        if (_timerState.value is TimerState.Running) return
        
        val startTimeMillis = System.currentTimeMillis()
        val durationMillis = durationMinutes * 60 * 1000L
        val endTimeMillis = startTimeMillis + durationMillis

        startForegroundService()

        timerJob = serviceScope.launch {
            // Save state for process death resilience
            timerRepository.saveTimerState(startTimeMillis, durationMinutes)

            _timerState.value = TimerState.Running(
                remainingSeconds = durationMinutes * 60,
                totalSeconds = durationMinutes * 60,
                progress = 0f
            )

            var remainingMillis = durationMillis
            while (remainingMillis > 0) {
                // Update state
                val remainingSeconds = (remainingMillis / 1000).toInt()
                val totalSeconds = durationMinutes * 60
                val progress = 1f - (remainingMillis.toFloat() / durationMillis.toFloat())

                _timerState.value = TimerState.Running(
                    remainingSeconds = remainingSeconds,
                    totalSeconds = totalSeconds,
                    progress = progress
                )

                updateNotification(remainingSeconds)

                delay(1000) // 1 second tick
                remainingMillis = max(0, endTimeMillis - System.currentTimeMillis())
            }

            // Timer Completed
            completeTimer(startTimeMillis, endTimeMillis, durationMinutes)
        }
    }

    /**
     * 포그라운드 서비스를 시작하고 지속적인 알림을 표시합니다.
     */
    private fun startForegroundService() {
        val notification = notificationHelper.getOngoingTimerNotificationBuilder()
            .setContentTitle("꼬모도로 타이머 동작중")
            .setContentText("Timer starting...")
            .build()
        
        // Use a generic foreground service type or special use
        // Since we are targeting SDK 36, we might need to be specific in manifest, 
        // but for startForeground here, we can pass type if needed.
        // For now, let's try without explicit type in code (Manifest handles it often).
        startForeground(NOTIFICATION_ID, notification)
    }

    /**
     * 진행 중인 타이머 알림을 갱신합니다.
     *
     * @param remainingSeconds 남은 시간 (초)
     */
    private fun updateNotification(remainingSeconds: Int) {
        val currentState = _timerState.value
        val totalSeconds = if (currentState is TimerState.Running) currentState.totalSeconds else remainingSeconds
        val progress = totalSeconds - remainingSeconds

        val notification = notificationHelper.getOngoingTimerNotificationBuilder()
            .setContentTitle("꼬모도로 타이머 동작중")
            .setProgress(totalSeconds, progress, false)
            .build()

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    /**
     * 타이머를 중지합니다.
     *
     * @param canceled 사용자에 의해 취소되었는지 여부 (true면 중도 포기)
     */
    fun stopTimer(canceled: Boolean) {
        timerJob?.cancel()
        timerJob = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        
        serviceScope.launch {
            val currentState = _timerState.value
            if (currentState is TimerState.Running && canceled) {
                 // Save as FAILED history if needed, for now just clear local state
                 // Requirement: "Save a DB record for both completion (SUCCESS) and user stop (FAILED)"
                 // We need start time and duration for that.
                 val state = timerRepository.getTimerState()
                 if (state != null) {
                     val session = TimerSession(
                         startTimeEpochMillis = state.startTimeMillis,
                         endTimeEpochMillis = System.currentTimeMillis(),
                         plannedDurationMinutes = state.durationMinutes,
                         status = SessionStatus.FAILED
                     )
                     saveSessionUseCase(session)
                 }
                 timerRepository.clearTimerState()
            }
        }
        _timerState.value = TimerState.Idle
    }

    /**
     * 타이머 완료 처리를 수행합니다. 완료 알림을 표시하고 성공 기록을 저장합니다.
     *
     * @param startTime 시작 시간 (Epoch Millis)
     * @param endTime 종료 시간 (Epoch Millis)
     * @param durationMinutes 설정된 시간 (분)
     */
    private fun completeTimer(startTime: Long, endTime: Long, durationMinutes: Int) {
        timerJob?.cancel()
        timerJob = null
        stopForeground(STOP_FOREGROUND_REMOVE)

        // Show completion notification
        val notification = notificationHelper.getCompletionNotificationBuilder()
            .setContentTitle("꼬모도로 타이머 종료")
            .setContentText("$durationMinutes 분 꼬모도로 타이머가 종료되었어요.")
            .build()
        
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(COMPLETION_NOTIFICATION_ID, notification)

        // Save History
        serviceScope.launch {
            val session = TimerSession(
                startTimeEpochMillis = startTime,
                endTimeEpochMillis = endTime,
                plannedDurationMinutes = durationMinutes,
                status = SessionStatus.SUCCESS
            )
            saveSessionUseCase(session)
            timerRepository.clearTimerState()
        }

        _timerState.value = TimerState.Idle
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val NOTIFICATION_ID = 1001
        const val COMPLETION_NOTIFICATION_ID = 1002
        const val ACTION_STOP_TIMER = "com.ggomodoro.STOP_TIMER"
    }
}

sealed class TimerState {
    object Idle : TimerState()
    data class Running(
        val remainingSeconds: Int,
        val totalSeconds: Int,
        val progress: Float // 0f to 1f
    ) : TimerState()
}
