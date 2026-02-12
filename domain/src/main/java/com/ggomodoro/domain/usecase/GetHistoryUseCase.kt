package com.ggomodoro.domain.usecase

import com.ggomodoro.domain.model.TimerSession
import com.ggomodoro.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * 완료된 타이머 히스토리 목록을 조회하는 유스케이스입니다.
 *
 * @property historyRepository 히스토리 데이터 접근을 위한 리포지토리
 */
class GetHistoryUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    /**
     * 모든 타이머 세션을 조회합니다.
     *
     * @return 타이머 세션 리스트의 Flow
     */
    operator fun invoke(): Flow<List<TimerSession>> {
        return historyRepository.getAllSessions()
    }
}
