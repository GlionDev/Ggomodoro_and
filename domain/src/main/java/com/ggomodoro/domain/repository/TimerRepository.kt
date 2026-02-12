package com.ggomodoro.domain.repository

/**
 * 현재 실행 중인 타이머 상태를 관리하는 리포지토리 인터페이스입니다.
 * 앱이 종료되거나 백그라운드로 전환될 때 타이머 상태를 보존하기 위해 사용됩니다.
 */
interface TimerRepository {
    /**
     * 현재 타이머 상태를 저장합니다.
     *
     * @param startTimeMillis 타이머 시작 시간 (Epoch Millis)
     * @param durationMinutes 타이머 지속 시간 (분)
     */
    suspend fun saveTimerState(startTimeMillis: Long, durationMinutes: Int)

    /**
     * 저장된 타이머 상태를 조회합니다.
     *
     * @return 저장된 타이머 상태 객체, 없으면 null
     */
    suspend fun getTimerState(): RunningTimerState?

    /**
     * 저장된 타이머 상태를 초기화합니다.
     */
    suspend fun clearTimerState()
}

/**
 * 실행 중인 타이머의 상태 정보를 담는 데이터 클래스입니다.
 *
 * @property startTimeMillis 타이머 시작 시간
 * @property durationMinutes 설정된 타이머 시간
 */
data class RunningTimerState(
    val startTimeMillis: Long,
    val durationMinutes: Int
)
