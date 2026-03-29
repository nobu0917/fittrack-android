package com.fittrack.android.ui.exercise

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fittrack.android.data.model.Exercise
import com.fittrack.android.data.model.ExerciseCategory
import com.fittrack.android.data.repository.ExerciseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseSelectUiState(
    val exercises: List<Exercise> = emptyList(),
    val selectedCategory: ExerciseCategory? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = true
)

@HiltViewModel
class ExerciseSelectViewModel @Inject constructor(
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseSelectUiState())
    val uiState: StateFlow<ExerciseSelectUiState> = _uiState.asStateFlow()

    init {
        loadAllExercises()
    }

    private fun loadAllExercises() {
        viewModelScope.launch {
            exerciseRepository.getAllExercises().collect { exercises ->
                _uiState.value = _uiState.value.copy(
                    exercises = exercises,
                    isLoading = false
                )
            }
        }
    }

    fun selectCategory(category: ExerciseCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        viewModelScope.launch {
            if (category != null) {
                exerciseRepository.getExercisesByCategory(category).collect { exercises ->
                    _uiState.value = _uiState.value.copy(exercises = exercises)
                }
            } else {
                exerciseRepository.getAllExercises().collect { exercises ->
                    _uiState.value = _uiState.value.copy(exercises = exercises)
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch {
            if (query.isBlank()) {
                val category = _uiState.value.selectedCategory
                if (category != null) {
                    exerciseRepository.getExercisesByCategory(category).collect { exercises ->
                        _uiState.value = _uiState.value.copy(exercises = exercises)
                    }
                } else {
                    exerciseRepository.getAllExercises().collect { exercises ->
                        _uiState.value = _uiState.value.copy(exercises = exercises)
                    }
                }
            } else {
                exerciseRepository.searchExercises(query).collect { exercises ->
                    _uiState.value = _uiState.value.copy(exercises = exercises)
                }
            }
        }
    }
}
