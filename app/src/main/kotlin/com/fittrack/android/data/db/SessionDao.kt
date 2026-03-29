package com.fittrack.android.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.fittrack.android.data.model.SessionSummary
import com.fittrack.android.data.model.SessionWithSets
import com.fittrack.android.data.model.TrainingSession
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: TrainingSession): Long

    @Update
    suspend fun update(session: TrainingSession)

    @Delete
    suspend fun delete(session: TrainingSession)

    @Query("SELECT * FROM training_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): TrainingSession?

    @Query("SELECT * FROM training_sessions ORDER BY date DESC")
    fun getAllSessions(): Flow<List<TrainingSession>>

    @Transaction
    @Query("SELECT * FROM training_sessions WHERE id = :sessionId")
    suspend fun getSessionWithSets(sessionId: Long): SessionWithSets?

    @Transaction
    @Query("SELECT * FROM training_sessions ORDER BY date DESC")
    fun getAllSessionsWithSets(): Flow<List<SessionWithSets>>

    @Query("""
        SELECT 
            ts.id AS sessionId,
            ts.date,
            COUNT(DISTINCT es.exerciseId) AS exerciseCount,
            COUNT(es.id) AS totalSets,
            COALESCE(SUM(es.weightKg * es.reps), 0) AS totalVolume
        FROM training_sessions ts
        LEFT JOIN exercise_sets es ON ts.id = es.sessionId AND es.isCompleted = 1
        GROUP BY ts.id
        ORDER BY ts.date DESC
        LIMIT :limit
    """)
    fun getRecentSessionSummaries(limit: Int = 10): Flow<List<SessionSummary>>

    @Query("""
        SELECT 
            ts.id AS sessionId,
            ts.date,
            COUNT(DISTINCT es.exerciseId) AS exerciseCount,
            COUNT(es.id) AS totalSets,
            COALESCE(SUM(es.weightKg * es.reps), 0) AS totalVolume
        FROM training_sessions ts
        LEFT JOIN exercise_sets es ON ts.id = es.sessionId AND es.isCompleted = 1
        GROUP BY ts.id
        ORDER BY ts.date DESC
    """)
    fun getAllSessionSummaries(): Flow<List<SessionSummary>>

    @Query("SELECT * FROM training_sessions WHERE endTime IS NULL ORDER BY startTime DESC LIMIT 1")
    suspend fun getActiveSession(): TrainingSession?
}
