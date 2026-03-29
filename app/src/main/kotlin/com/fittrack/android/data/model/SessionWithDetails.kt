package com.fittrack.android.data.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * セッションとそのセット情報を結合したデータクラス
 */
data class SessionWithSets(
    @Embedded val session: TrainingSession,
    @Relation(
        parentColumn = "id",
        entityColumn = "sessionId"
    )
    val sets: List<ExerciseSet>
)

/**
 * 統計用：エクササイズ別の最大重量
 */
data class ExerciseMaxWeight(
    val exerciseId: Long,
    val exerciseName: String,
    val maxWeight: Float,
    val date: Long
)

/**
 * セッションのサマリー情報
 */
data class SessionSummary(
    val sessionId: Long,
    val date: Long,
    val exerciseCount: Int,
    val totalSets: Int,
    val totalVolume: Float
)
