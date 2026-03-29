package com.fittrack.android.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.android.data.model.Exercise
import com.fittrack.android.data.model.ExerciseMaxWeight
import com.fittrack.android.data.repository.ExerciseRepository
import com.fittrack.android.data.repository.SessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StatsUiState(
    val exercises: List<Exercise> = emptyList(),
    val selectedExercise: Exercise? = null,
    val maxWeightHistory: List<ExerciseMaxWeight> = emptyList(),
    val allMaxWeights: List<ExerciseMaxWeight> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository,
    private val sessionRepository: SessionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            // 全エクササイズの最大重量
            sessionRepository.getAllExerciseMaxWeights().collect { maxWeights ->
                _uiState.value = _uiState.value.copy(
                    allMaxWeights = maxWeights,
                    isLoading = false
                )
            }
        }
        viewModelScope.launch {
            exerciseRepository.getAllExercises().collect { exercises ->
                _uiState.value = _uiState.value.copy(exercises = exercises)
                // 最初のエクササイズを自動選択
                if (_uiState.value.selectedExercise == null && exercises.isNotEmpty()) {
                    selectExercise(exercises.first())
                }
            }
        }
    }

    fun selectExercise(exercise: Exercise) {
        _uiState.value = _uiState.value.copy(selectedExercise = exercise)
        viewModelScope.launch {
            sessionRepository.getMaxWeightHistory(exercise.id).collect { history ->
                _uiState.value = _uiState.value.copy(maxWeightHistory = history)
            }
        }
    }
}
