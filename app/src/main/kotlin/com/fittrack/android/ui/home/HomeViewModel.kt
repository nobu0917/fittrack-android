package com.fittrack.android.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.android.data.repository.SessionRepository
import com.fittrack.android.data.model.SessionSummary
import com.fittrack.android.data.model.TrainingSession
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val recentSessions: List<SessionSummary> = emptyList(),
    val activeSessionId: Long? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // アクティブセッションの確認
            val activeSession = sessionRepository.getActiveSession()
            _uiState.value = _uiState.value.copy(activeSessionId = activeSession?.id)
        }
        viewModelScope.launch {
            sessionRepository.getRecentSessionSummaries(5).collect { summaries ->
                _uiState.value = _uiState.value.copy(
                    recentSessions = summaries,
                    isLoading = false
                )
            }
        }
    }

    fun startNewSession(): Long {
        var sessionId = 0L
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val session = TrainingSession(
                date = now,
                startTime = now
            )
            sessionId = sessionRepository.createSession(session)
            _uiState.value = _uiState.value.copy(activeSessionId = sessionId)
        }
        return sessionId
    }

    suspend fun startNewSessionAsync(): Long {
        val now = System.currentTimeMillis()
        val session = TrainingSession(
            date = now,
            startTime = now
        )
        val sessionId = sessionRepository.createSession(session)
        _uiState.value = _uiState.value.copy(activeSessionId = sessionId)
        return sessionId
    }
}
