package com.ggomodoro.data.datasource.local

import com.ggomodoro.core.database.dao.TimerSessionDao
import com.ggomodoro.core.database.model.TimerSessionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * [LocalDataSource]의 구현체입니다.
 * Room DAO를 사용하여 로컬 데이터베이스에 접근합니다.
 *
 * @property dao Room DAO 객체
 */
class LocalDataSourceImpl @Inject constructor(
    private val dao: TimerSessionDao
) : LocalDataSource {

    override fun observeAllSessions(): Flow<List<TimerSessionEntity>> {
        return dao.observeAllSessions()
    }

    override suspend fun insertSession(session: TimerSessionEntity) {
        dao.insertSession(session)
    }

    override suspend fun updateMemo(id: Long, memo: String) {
        dao.updateMemo(id, memo)
    }

    override suspend fun deleteSession(id: Long) {
        dao.deleteSession(id)
    }
}
