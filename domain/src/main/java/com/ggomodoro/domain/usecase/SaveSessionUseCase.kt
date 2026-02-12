package com.ggomodoro.domain.usecase

import com.ggomodoro.domain.model.TimerSession
import com.ggomodoro.domain.repository.HistoryRepository
import javax.inject.Inject

/**
 * 완료된 타이머 세션을 저장하는 유스케이스입니다.
 *
 * @property historyRepository 히스토리 데이터 저장을 위한 리포지토리
 */
class SaveSessionUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    /**
     * 타이머 세션을 저장합니다.
     *
     * @param session 저장할 타이머 세션 객체
     */
    suspend operator fun invoke(session: TimerSession) {
        historyRepository.insertSession(session)
    }
}
