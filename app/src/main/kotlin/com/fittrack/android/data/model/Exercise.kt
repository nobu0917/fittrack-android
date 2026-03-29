package com.fittrack.android.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * エクササイズのカテゴリ（部位）
 */
enum class ExerciseCategory {
    CHEST, BACK, SHOULDER, ARM, LEG, CORE, CARDIO;

    fun displayName(): String = when (this) {
        CHEST -> "胸"
        BACK -> "背中"
        SHOULDER -> "肩"
        ARM -> "腕"
        LEG -> "脚"
        CORE -> "体幹"
        CARDIO -> "有酸素"
    }
}

/**
 * エクササイズマスターデータ
 */
@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: ExerciseCategory,
    val muscleGroup: String = "",
    val isCustom: Boolean = false
)
