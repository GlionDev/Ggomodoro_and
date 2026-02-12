package com.ggomodoro.feature.timer

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggomodoro.feature.timer.service.TimerService
import com.ggomodoro.feature.timer.service.TimerState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 타이머 기능의 비즈니스 로직과 UI 상태를 관리하는 ViewModel입니다.
 * [TimerService]와 바인딩하여 타이머 상태를 실시간으로 관찰하고 제어합니다.
 *
 * @property context 애플리케이션 컨텍스트 (서비스 바인딩용 isBound)
 */
@HiltViewModel
class TimerViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var timerService: TimerService? = null
    private var isBound = false

    private val _uiState = MutableStateFlow<TimerState>(TimerState.Idle)
    val uiState: StateFlow<TimerState> = _uiState.asStateFlow()

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as TimerService.LocalBinder
            timerService = binder.getService()
            isBound = true
            
            // Observe Service State
            viewModelScope.launch {
                timerService?.timerState?.collect { state ->
                    _uiState.value = state
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            timerService = null
            isBound = false
        }
    }

    init {
        bindService()
    }

    /**
     * 타이머를 시작합니다.
     * Service가 실행 중이 아니라면 시작하고, 이미 실행 중이면 바인딩만 수행합니다.
     *
     * @param durationMinutes 타이머 지속 시간 (분)
     */
    fun startTimer(durationMinutes: Int) {
        val intent = Intent(context, TimerService::class.java) // Start service if not running
        context.startService(intent) // Ensure it's started as a service, not just bound
        timerService?.startTimer(durationMinutes)
    }

    /**
     * 타이머를 정지합니다.
     */
    fun stopTimer() {
        timerService?.stopTimer(canceled = true)
    }

    private fun bindService() {
        val intent = Intent(context, TimerService::class.java)
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindService() {
        if (isBound) {
            context.unbindService(serviceConnection)
            isBound = false
        }
    }

    override fun onCleared() {
        super.onCleared()
        unbindService()
    }
}
