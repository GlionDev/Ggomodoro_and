package com.ggomodoro.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ggomodoro.domain.repository.RunningTimerState
import com.ggomodoro.domain.repository.TimerRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "timer_prefs")

/**
 * [TimerRepository]의 구현체입니다.
 * DataStore를 사용하여 실행 중인 타이머 상태를 영구 저장소에 보관합니다.
 *
 * @property context 애플리케이션 컨텍스트 (DataStore 접근용)
 */
class TimerRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : TimerRepository {

    companion object {
        val KEY_START_TIME = longPreferencesKey("start_time")
        val KEY_DURATION = intPreferencesKey("duration")
    }

    override suspend fun saveTimerState(startTimeMillis: Long, durationMinutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[KEY_START_TIME] = startTimeMillis
            prefs[KEY_DURATION] = durationMinutes
        }
    }

    override suspend fun getTimerState(): RunningTimerState? {
        val prefs = context.dataStore.data.first()
        val startTime = prefs[KEY_START_TIME]
        val duration = prefs[KEY_DURATION]

        return if (startTime != null && duration != null) {
            RunningTimerState(startTime, duration)
        } else {
            null
        }
    }

    override suspend fun clearTimerState() {
        context.dataStore.edit { prefs ->
            prefs.remove(KEY_START_TIME)
            prefs.remove(KEY_DURATION)
        }
    }
}
