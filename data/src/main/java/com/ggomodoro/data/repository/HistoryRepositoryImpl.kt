package com.ggomodoro.data.repository

import com.ggomodoro.core.database.dao.TimerSessionDao
import com.ggomodoro.data.mapper.toDomain
import com.ggomodoro.data.mapper.toEntity
import com.ggomodoro.domain.model.TimerSession
import com.ggomodoro.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * [HistoryRepository]의 구현체입니다.
 * Room 데이터베이스를 사용하여 타이머 세션 데이터를 관리합니다.
 *
 * @property dao Room DAO 객체
 */
class HistoryRepositoryImpl @Inject constructor(
    private val dao: TimerSessionDao
) : HistoryRepository {

    override fun getAllSessions(): Flow<List<TimerSession>> {
        return dao.observeAllSessions().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertSession(session: TimerSession) {
        dao.insertSession(session.toEntity())
    }

    override suspend fun updateMemo(id: Long, memo: String) {
        dao.updateMemo(id, memo)
    }

    override suspend fun deleteSession(id: Long) {
        dao.deleteSession(id)
    }
}
