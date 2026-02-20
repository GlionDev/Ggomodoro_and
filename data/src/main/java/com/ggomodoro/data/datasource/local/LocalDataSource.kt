package com.ggomodoro.data.datasource.local

import com.ggomodoro.core.database.model.TimerSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * 로컬 데이터베이스와 통신하기 위한 데이터 소스 인터페이스입니다.
 */
interface LocalDataSource {
    fun observeAllSessions(): Flow<List<TimerSessionEntity>>
    suspend fun insertSession(session: TimerSessionEntity)
    suspend fun updateMemo(id: Long, memo: String)
    suspend fun deleteSession(id: Long)
}
