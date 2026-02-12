package com.ggomodoro.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ggomodoro.core.database.model.TimerSessionEntity
import kotlinx.coroutines.flow.Flow

/**
 * 타이머 세션 데이터에 접근하기 위한 Data Access Object (DAO)입니다.
 */
@Dao
interface TimerSessionDao {
    /**
     * 모든 타이머 세션을 시작 시간 역순으로 조회합니다.
     *
     * @return 타이머 세션 엔티티 리스트의 Flow
     */
    @Query("SELECT * FROM timer_sessions ORDER BY startTimeEpochMillis DESC")
    fun observeAllSessions(): Flow<List<TimerSessionEntity>>

    /**
     * 새로운 타이머 세션을 추가하거나, 충돌 시 교체합니다.
     *
     * @param session 추가할 세션 엔티티
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: TimerSessionEntity)

    /**
     * 특정 세션의 메모를 업데이트합니다.
     *
     * @param id 업데이트할 세션 ID
     * @param memo 새로운 메모 내용
     */
    @Query("UPDATE timer_sessions SET memo = :memo WHERE id = :id")
    suspend fun updateMemo(id: Long, memo: String)
}
