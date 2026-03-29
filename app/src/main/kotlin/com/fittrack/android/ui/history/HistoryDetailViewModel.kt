package com.fittrack.android.ui.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.android.data.model.Exercise
import com.fittrack.android.data.model.ExerciseSet
import com.fittrack.android.data.model.TrainingSession
import com.fittrack.android.data.repository.ExerciseRepository
import com.fittrack.android.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseWithSets(
    val exercise: Exercise,
    val sets: List<ExerciseSet>
)

data class HistoryDetailUiState(
    val session: TrainingSession? = null,
    val exercisesWithSets: List<ExerciseWithSets> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class HistoryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val sessionId: Long = savedStateHandle.get<Long>("sessionId") ?: 0L

    private val _uiState = MutableStateFlow(HistoryDetailUiState())
    val uiState: StateFlow<HistoryDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    private fun loadDetail() {
        viewModelScope.launch {
            val sessionWithSets = sessionRepository.getSessionWithSets(sessionId)
            if (sessionWithSets != null) {
                val exerciseIds = sessionWithSets.sets.map { it.exerciseId }.distinct()
                val exercisesWithSets = exerciseIds.mapNotNull { exId ->
                    val exercise = exerciseRepository.getExerciseById(exId)
                    exercise?.let {
                        ExerciseWithSets(
                            exercise = it,
                            sets = sessionWithSets.sets
                                .filter { s -> s.exerciseId == exId }
                                .sortedBy { s -> s.setNumber }
                        )
                    }
                }
                _uiState.value = HistoryDetailUiState(
                    session = sessionWithSets.session,
                    exercisesWithSets = exercisesWithSets,
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }
}
