package com.ggomodoro.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 타이머 세션 정보를 저장하는 Room 엔티티입니다.
 *
 * @property id 자동 생성되는 기본 키
 * @property startTimeEpochMillis 세션 시작 시간
 * @property endTimeEpochMillis 세션 종료 시간
 * @property plannedDurationMinutes 계획된 시간 (분)
 * @property status 상태 코드 (0: 성공, 1: 실패)
 * @property memo 사용자 메모
 */
@Entity(tableName = "timer_sessions")
data class TimerSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTimeEpochMillis: Long,
    val endTimeEpochMillis: Long,
    val plannedDurationMinutes: Int,
    val status: Int, // 0: SUCCESS, 1: FAILED
    val memo: String? = null
)
