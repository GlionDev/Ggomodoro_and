package com.ggomodoro.domain.usecase

import com.ggomodoro.domain.repository.HistoryRepository
import javax.inject.Inject

/**
 * 특정 타이머 세션의 메모를 업데이트하는 유스케이스입니다.
 * 비즈니스 규칙: 메모는 최대 30자까지만 저장됩니다.
 *
 * @property historyRepository 히스토리 데이터 수정을 위한 리포지토리
 */
class UpdateMemoUseCase @Inject constructor(
    private val historyRepository: HistoryRepository
) {
    /**
     * 세션의 메모를 업데이트합니다.
     *
     * @param id 업데이트할 세션의 ID
     * @param memo 새로운 메모 내용 (최대 30자)
     */
    suspend operator fun invoke(id: Long, memo: String) {
        // Enforce max 30 characters rule here in Domain
        val validMemo = if (memo.length > 30) memo.take(30) else memo
        historyRepository.updateMemo(id, validMemo)
    }
}
