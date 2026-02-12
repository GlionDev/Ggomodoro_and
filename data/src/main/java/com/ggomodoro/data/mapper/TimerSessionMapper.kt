package com.ggomodoro.data.mapper

import com.ggomodoro.core.database.model.TimerSessionEntity
import com.ggomodoro.domain.model.SessionStatus
import com.ggomodoro.domain.model.TimerSession

/**
 * [TimerSessionEntity]를 도메인 모델인 [TimerSession]으로 변환합니다.
 *
 * @return 변환된 TimerSession 객체
 */
fun TimerSessionEntity.toDomain(): TimerSession {
    return TimerSession(
        id = id,
        startTimeEpochMillis = startTimeEpochMillis,
        endTimeEpochMillis = endTimeEpochMillis,
        plannedDurationMinutes = plannedDurationMinutes,
        status = if (status == 0) SessionStatus.SUCCESS else SessionStatus.FAILED,
        memo = memo
    )
}

/**
 * 도메인 모델인 [TimerSession]을 데이터베이스 엔티티인 [TimerSessionEntity]로 변환합니다.
 *
 * @return 변환된 TimerSessionEntity 객체
 */
fun TimerSession.toEntity(): TimerSessionEntity {
    return TimerSessionEntity(
        id = id,
        startTimeEpochMillis = startTimeEpochMillis,
        endTimeEpochMillis = endTimeEpochMillis,
        plannedDurationMinutes = plannedDurationMinutes,
        status = if (status == SessionStatus.SUCCESS) 0 else 1,
        memo = memo
    )
}
