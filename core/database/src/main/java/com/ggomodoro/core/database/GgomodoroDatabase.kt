package com.ggomodoro.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ggomodoro.core.database.dao.TimerSessionDao
import com.ggomodoro.core.database.model.TimerSessionEntity

/**
 * 앱의 Room 데이터베이스 정의 클래스입니다.
 * [TimerSessionEntity]를 포함합니다.
 */
@Database(
    entities = [TimerSessionEntity::class],
    version = 1,
    exportSchema = true
)
abstract class GgomodoroDatabase : RoomDatabase() {
    /**
     * 타이머 세션 DAO를 반환합니다.
     *
     * @return [TimerSessionDao] 인스턴스
     */
    abstract fun timerSessionDao(): TimerSessionDao
}
