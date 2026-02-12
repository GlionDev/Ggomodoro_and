package com.ggomodoro.domain.usecase

import com.ggomodoro.domain.repository.HistoryRepository
import javax.inject.Inject

/**
 * 특정 타이머 세션을 삭제하는 유스케이스입니다.
 *
 * @property historyRepository 히스토리 데이터 삭제를 위한 리포지토리
 */
class DeleteSessionUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    /**
     * 타이머 세션을 삭제합니다.
     *
     * @param id 삭제할 세션의 ID
     */
    suspend operator fun invoke(id: Long) {
        historyRepository.deleteSession(id)
    }
}
