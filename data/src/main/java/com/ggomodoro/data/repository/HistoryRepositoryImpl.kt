package com.ggomodoro.data.repository

import com.ggomodoro.data.datasource.local.LocalDataSource
import com.ggomodoro.data.mapper.toDomain
import com.ggomodoro.data.mapper.toEntity
import com.ggomodoro.domain.model.TimerSession
import com.ggomodoro.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * [HistoryRepository]의 구현체입니다.
 * [LocalDataSource]를 사용하여 타이머 세션 데이터를 관리합니다.
 *
 * @property localDataSource 로컬 데이터 소스
 */
class HistoryRepositoryImpl @Inject constructor(
    private val localDataSource: LocalDataSource
) : HistoryRepository {

    override fun getAllSessions(): Flow<List<TimerSession>> {
        return localDataSource.observeAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertSession(session: TimerSession) {
        localDataSource.insertSession(session.toEntity())
    }

    override suspend fun updateMemo(id: Long, memo: String) {
        localDataSource.updateMemo(id, memo)
    }

    override suspend fun deleteSession(id: Long) {
        localDataSource.deleteSession(id)
    }
}
