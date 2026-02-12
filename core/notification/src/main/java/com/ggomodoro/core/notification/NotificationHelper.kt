package com.ggomodoro.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * 알림 채널 생성 및 알림 빌더 제공을 담당하는 헬퍼 클래스입니다.
 * 타이머 진행 중 알림과 완료 알림을 관리합니다.
 *
 * @property context 애플리케이션 컨텍스트
 */
@Singleton
class NotificationHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        const val TIMER_CHANNEL_ID = "timer_ongoing_channel"
        const val COMPLETION_CHANNEL_ID = "timer_completion_channel"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timerChannel = NotificationChannel(
                TIMER_CHANNEL_ID,
                "Ongoing Timer",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows the ongoing timer progress"
                setShowBadge(false)
            }

            val completionChannel = NotificationChannel(
                COMPLETION_CHANNEL_ID,
                "Timer Completion",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifies when a timer completes"
                enableVibration(true)
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannels(listOf(timerChannel, completionChannel))
        }
    }

    /**
     * 진행 중인 타이머 알림을 위한 빌더를 생성합니다.
     *
     * @return [NotificationCompat.Builder] 객체
     */
    fun getOngoingTimerNotificationBuilder(): NotificationCompat.Builder {
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            0,
            launchIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, TIMER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Placeholder
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
    }

    /**
     * 타이머 완료 알림을 위한 빌더를 생성합니다.
     *
     * @return [NotificationCompat.Builder] 객체
     */
    fun getCompletionNotificationBuilder(): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, COMPLETION_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm) // Placeholder
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
    }
}
