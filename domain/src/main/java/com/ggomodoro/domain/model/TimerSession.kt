package com.ggomodoro.domain.model

/**
 * 뽀모도로 타이머 세션 정보를 담는 데이터 클래스입니다.
 *
 * @property id 세션의 고유 식별자 (DB 자동 생성)
 * @property startTimeEpochMillis 세션 시작 시간 (Epoch Millis)
 * @property endTimeEpochMillis 세션 종료 시간 (Epoch Millis)
 * @property plannedDurationMinutes 계획된 세션 지속 시간 (분)
 * @property status 세션의 최종 상태 (성공/실패)
 * @property memo 세션에 대한 사용자 메모 (선택 사항)
 */
data class TimerSession(
    val id: Long = 0,
    val startTimeEpochMillis: Long,
    val endTimeEpochMillis: Long,
    val plannedDurationMinutes: Int,
    val status: SessionStatus,
    val memo: String? = null
)

/**
 * 타이머 세션의 완료 상태를 나타내는 열거형입니다.
 */
enum class SessionStatus {
    /** 세션이 성공적으로 완료됨 */
    SUCCESS,
    /** 세션이 중도 포기되거나 실패함 */
    FAILED
}
