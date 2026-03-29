package com.fittrack.android.data.repository

import com.fittrack.android.data.db.ExerciseSetDao
import com.fittrack.android.data.db.SessionDao
import com.fittrack.android.data.model.ExerciseMaxWeight
import com.fittrack.android.data.model.ExerciseSet
import com.fittrack.android.data.model.SessionSummary
import com.fittrack.android.data.model.SessionWithSets
import com.fittrack.android.data.model.TrainingSession
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionRepository @Inject constructor(
    private val sessionDao: SessionDao,
    private val exerciseSetDao: ExerciseSetDao
) {
    // セッション
    suspend fun createSession(session: TrainingSession): Long = sessionDao.insert(session)

    suspend fun updateSession(session: TrainingSession) = sessionDao.update(session)

    suspend fun deleteSession(session: TrainingSession) = sessionDao.delete(session)

    suspend fun getSessionById(id: Long): TrainingSession? = sessionDao.getSessionById(id)

    suspend fun getSessionWithSets(sessionId: Long): SessionWithSets? =
        sessionDao.getSessionWithSets(sessionId)

    fun getAllSessions(): Flow<List<TrainingSession>> = sessionDao.getAllSessions()

    fun getAllSessionsWithSets(): Flow<List<SessionWithSets>> = sessionDao.getAllSessionsWithSets()

    fun getRecentSessionSummaries(limit: Int = 10): Flow<List<SessionSummary>> =
        sessionDao.getRecentSessionSummaries(limit)

    fun getAllSessionSummaries(): Flow<List<SessionSummary>> = sessionDao.getAllSessionSummaries()

    suspend fun getActiveSession(): TrainingSession? = sessionDao.getActiveSession()

    // セット
    suspend fun insertSet(set: ExerciseSet): Long = exerciseSetDao.insert(set)

    suspend fun updateSet(set: ExerciseSet) = exerciseSetDao.update(set)

    suspend fun deleteSet(set: ExerciseSet) = exerciseSetDao.delete(set)

    fun getSetsBySession(sessionId: Long): Flow<List<ExerciseSet>> =
        exerciseSetDao.getSetsBySession(sessionId)

    fun getSetsBySessionAndExercise(sessionId: Long, exerciseId: Long): Flow<List<ExerciseSet>> =
        exerciseSetDao.getSetsBySessionAndExercise(sessionId, exerciseId)

    suspend fun getPreviousSetsForExercise(exerciseId: Long, currentSessionId: Long): List<ExerciseSet> =
        exerciseSetDao.getPreviousSetsForExercise(exerciseId, currentSessionId)

    suspend fun getExerciseIdsForSession(sessionId: Long): List<Long> =
        exerciseSetDao.getExerciseIdsForSession(sessionId)

    // 統計
    fun getMaxWeightHistory(exerciseId: Long): Flow<List<ExerciseMaxWeight>> =
        exerciseSetDao.getMaxWeightHistory(exerciseId)

    fun getAllExerciseMaxWeights(): Flow<List<ExerciseMaxWeight>> =
        exerciseSetDao.getAllExerciseMaxWeights()
}
