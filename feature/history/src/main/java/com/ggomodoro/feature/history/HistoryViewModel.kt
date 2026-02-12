package com.ggomodoro.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ggomodoro.domain.model.TimerSession
import com.ggomodoro.domain.usecase.GetHistoryUseCase
import com.ggomodoro.domain.usecase.UpdateMemoUseCase
import com.ggomodoro.domain.usecase.DeleteSessionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 히스토리 화면의 비즈니스 로직과 데이터 상태를 관리하는 ViewModel입니다.
 * 저장된 세션 목록을 로드하고 메모 업데이트 및 세션 삭제 기능을 제공합니다.
 *
 * @property getHistoryUseCase 히스토리 조회 유스케이스
 * @property updateMemoUseCase 메모 수정 유스케이스
 * @property deleteSessionUseCase 세션 삭제 유스케이스
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    getHistoryUseCase: GetHistoryUseCase,
    private val updateMemoUseCase: UpdateMemoUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase
) : ViewModel() {

    val historyState: StateFlow<List<TimerSession>> = getHistoryUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    /**
     * 특정 세션의 메모를 업데이트합니다.
     *
     * @param session 업데이트할 세션 객체
     * @param newMemo 수정할 메모 내용
     */
    fun updateMemo(session: TimerSession, newMemo: String) {
        viewModelScope.launch {
            updateMemoUseCase(session.id, newMemo)
        }
    }

    /**
     * 특정 세션을 삭제합니다.
     *
     * @param session 삭제할 세션 객체
     */
    fun deleteSession(session: TimerSession) {
        viewModelScope.launch {
            deleteSessionUseCase(session.id)
        }
    }
}
