package com.fittrack.android.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.fittrack.android.data.model.ExerciseMaxWeight
import com.fittrack.android.data.model.ExerciseSet
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseSetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: ExerciseSet): Long

    @Update
    suspend fun update(set: ExerciseSet)

    @Delete
    suspend fun delete(set: ExerciseSet)

    @Query("SELECT * FROM exercise_sets WHERE sessionId = :sessionId ORDER BY exerciseId, setNumber")
    fun getSetsBySession(sessionId: Long): Flow<List<ExerciseSet>>

    @Query("SELECT * FROM exercise_sets WHERE sessionId = :sessionId AND exerciseId = :exerciseId ORDER BY setNumber")
    fun getSetsBySessionAndExercise(sessionId: Long, exerciseId: Long): Flow<List<ExerciseSet>>

    @Query("""
        SELECT es.* FROM exercise_sets es
        INNER JOIN training_sessions ts ON es.sessionId = ts.id
        WHERE es.exerciseId = :exerciseId AND es.isCompleted = 1
        ORDER BY ts.date DESC
        LIMIT 20
    """)
    suspend fun getRecentSetsForExercise(exerciseId: Long): List<ExerciseSet>

    /**
     * 前回のセッションでの同エクササイズの記録を取得
     */
    @Query("""
        SELECT es.* FROM exercise_sets es
        INNER JOIN training_sessions ts ON es.sessionId = ts.id
        WHERE es.exerciseId = :exerciseId 
            AND es.isCompleted = 1
            AND ts.id != :currentSessionId
        ORDER BY ts.date DESC
        LIMIT 10
    """)
    suspend fun getPreviousSetsForExercise(exerciseId: Long, currentSessionId: Long): List<ExerciseSet>

    /**
     * エクササイズ別の最大重量推移（統計画面用）
     */
    @Query("""
        SELECT 
            es.exerciseId,
            e.name AS exerciseName,
            MAX(es.weightKg) AS maxWeight,
            ts.date
        FROM exercise_sets es
        INNER JOIN exercises e ON es.exerciseId = e.id
        INNER JOIN training_sessions ts ON es.sessionId = ts.id
        WHERE es.isCompleted = 1 AND es.exerciseId = :exerciseId
        GROUP BY ts.date
        ORDER BY ts.date ASC
    """)
    fun getMaxWeightHistory(exerciseId: Long): Flow<List<ExerciseMaxWeight>>

    /**
     * 全エクササイズの現在の最大重量
     */
    @Query("""
        SELECT 
            es.exerciseId,
            e.name AS exerciseName,
            MAX(es.weightKg) AS maxWeight,
            MAX(ts.date) AS date
        FROM exercise_sets es
        INNER JOIN exercises e ON es.exerciseId = e.id
        INNER JOIN training_sessions ts ON es.sessionId = ts.id
        WHERE es.isCompleted = 1
        GROUP BY es.exerciseId
        ORDER BY e.name
    """)
    fun getAllExerciseMaxWeights(): Flow<List<ExerciseMaxWeight>>

    @Query("DELETE FROM exercise_sets WHERE sessionId = :sessionId")
    suspend fun deleteBySession(sessionId: Long)

    @Query("SELECT DISTINCT exerciseId FROM exercise_sets WHERE sessionId = :sessionId")
    suspend fun getExerciseIdsForSession(sessionId: Long): List<Long>
}
