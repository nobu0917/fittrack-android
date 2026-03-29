package com.fittrack.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * トレーニングセッション
 */
@Entity(tableName = "training_sessions")
data class TrainingSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long,           // エポックミリ秒
    val startTime: Long,
    val endTime: Long? = null,
    val note: String = ""
)
