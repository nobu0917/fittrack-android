package com.fittrack.android.ui.session

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

/**
 * 1つのエクササイズの記録状態
 */
data class ExerciseRecordState(
    val exercise: Exercise,
    val sets: List<ExerciseSet> = emptyList(),
    val previousSets: List<ExerciseSet> = emptyList(),
    val showPrevious: Boolean = false,
    val memo: String = ""
)

data class SessionUiState(
    val session: TrainingSession? = null,
    val exercises: List<ExerciseRecordState> = emptyList(),
    val isLoading: Boolean = true,
    val elapsedSeconds: Long = 0
)

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val sessionRepository: SessionRepository,
    private val exerciseRepository: ExerciseRepository
) : ViewModel() {

    private val sessionId: Long = savedStateHandle.get<Long>("sessionId") ?: 0L

    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        loadSession()
        observeSelectedExercise()
    }

    /**
     * エクササイズ選択画面からの戻りを監視
     */
    private fun observeSelectedExercise() {
        viewModelScope.launch {
            savedStateHandle.getStateFlow<Long?>("selectedExerciseId", null).collect { exerciseId ->
                if (exerciseId != null && exerciseId > 0) {
                    val exercise = exerciseRepository.getExerciseById(exerciseId)
                    if (exercise != null) {
                        addExercise(exercise)
                    }
                    // 処理後にクリア
                    savedStateHandle["selectedExerciseId"] = null
                }
            }
        }
    }

    private fun loadSession() {
        viewModelScope.launch {
            val session = sessionRepository.getSessionById(sessionId)
            _uiState.value = _uiState.value.copy(session = session, isLoading = false)

            // 既存のセットを読み込む
            sessionRepository.getSetsBySession(sessionId).collect { sets ->
                val exerciseIds = sets.map { it.exerciseId }.distinct()
                val exerciseStates = exerciseIds.mapNotNull { exId ->
                    val exercise = exerciseRepository.getExerciseById(exId)
                    exercise?.let {
                        val exSets = sets.filter { s -> s.exerciseId == exId }
                        val existing = _uiState.value.exercises.find { e -> e.exercise.id == exId }
                        ExerciseRecordState(
                            exercise = it,
                            sets = exSets,
                            previousSets = existing?.previousSets ?: emptyList(),
                            showPrevious = existing?.showPrevious ?: false,
                            memo = existing?.memo ?: ""
                        )
                    }
                }
                _uiState.value = _uiState.value.copy(exercises = exerciseStates)
            }
        }
    }

    /**
     * エクササイズを追加
     */
    fun addExercise(exercise: Exercise) {
        viewModelScope.launch {
            // 最初のセットを自動追加
            val set = ExerciseSet(
                sessionId = sessionId,
                exerciseId = exercise.id,
                setNumber = 1,
                weightKg = 0f,
                reps = 0,
                isCompleted = false
            )
            sessionRepository.insertSet(set)
        }
    }

    /**
     * セットを追加
     */
    fun addSet(exerciseId: Long) {
        viewModelScope.launch {
            val currentSets = _uiState.value.exercises
                .find { it.exercise.id == exerciseId }?.sets ?: emptyList()
            val nextSetNumber = (currentSets.maxOfOrNull { it.setNumber } ?: 0) + 1

            // 前のセットの重量を引き継ぐ
            val lastSet = currentSets.lastOrNull()
            val set = ExerciseSet(
                sessionId = sessionId,
                exerciseId = exerciseId,
                setNumber = nextSetNumber,
                weightKg = lastSet?.weightKg ?: 0f,
                reps = 0,
                isCompleted = false
            )
            sessionRepository.insertSet(set)
        }
    }

    /**
     * セットの重量を更新
     */
    fun updateSetWeight(set: ExerciseSet, weight: Float) {
        viewModelScope.launch {
            sessionRepository.updateSet(set.copy(weightKg = weight))
        }
    }

    /**
     * セットのレップ数を更新
     */
    fun updateSetReps(set: ExerciseSet, reps: Int) {
        viewModelScope.launch {
            sessionRepository.updateSet(set.copy(reps = reps))
        }
    }

    /**
     * セットを完了/記録
     */
    fun completeSet(set: ExerciseSet) {
        viewModelScope.launch {
            sessionRepository.updateSet(set.copy(isCompleted = true))
        }
    }

    /**
     * セットを削除
     */
    fun deleteSet(set: ExerciseSet) {
        viewModelScope.launch {
            sessionRepository.deleteSet(set)
        }
    }

    /**
     * 前回の記録を表示/非表示
     */
    fun togglePreviousRecord(exerciseId: Long) {
        viewModelScope.launch {
            val exercises = _uiState.value.exercises.toMutableList()
            val index = exercises.indexOfFirst { it.exercise.id == exerciseId }
            if (index >= 0) {
                val current = exercises[index]
                if (current.previousSets.isEmpty() && !current.showPrevious) {
                    // 前回の記録を読み込む
                    val previousSets = sessionRepository.getPreviousSetsForExercise(
                        exerciseId, sessionId
                    )
                    exercises[index] = current.copy(
                        previousSets = previousSets,
                        showPrevious = true
                    )
                } else {
                    exercises[index] = current.copy(showPrevious = !current.showPrevious)
                }
                _uiState.value = _uiState.value.copy(exercises = exercises)
            }
        }
    }

    /**
     * セッションを終了
     */
    fun endSession() {
        viewModelScope.launch {
            val session = _uiState.value.session
            if (session != null) {
                sessionRepository.updateSession(
                    session.copy(endTime = System.currentTimeMillis())
                )
            }
        }
    }
}
