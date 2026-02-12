package com.ggomodoro.domain.repository

import com.ggomodoro.domain.model.TimerSession
import kotlinx.coroutines.flow.Flow

/**
 * 타이머 세션 히스토리 데이터에 접근하기 위한 리포지토리 인터페이스입니다.
 */
interface HistoryRepository {
    /**
     * 저장된 모든 타이머 세션을 조회합니다.
     *
     * @return 타이머 세션 리스트를 방출하는 Flow
     */
    fun getAllSessions(): Flow<List<TimerSession>>

    /**
     * 새로운 타이머 세션을 저장합니다.
     *
     * @param session 저장할 타이머 세션 정보
     */
    suspend fun insertSession(session: TimerSession)

    /**
     * 특정 세션의 메모를 수정합니다.
     *
     * @param id 수정할 세션의 ID
     * @param memo 수정할 메모 내용
     */
    suspend fun updateMemo(id: Long, memo: String)

    /**
     * 특정 타이머 세션을 삭제합니다.
     *
     * @param id 삭제할 세션의 ID
     */
    suspend fun deleteSession(id: Long)
}
