package com.fittrack.android.data.db

import androidx.room.TypeConverter
import com.fittrack.android.data.model.ExerciseCategory

class Converters {
    @TypeConverter
    fun fromExerciseCategory(value: ExerciseCategory): String = value.name

    @TypeConverter
    fun toExerciseCategory(value: String): ExerciseCategory = ExerciseCategory.valueOf(value)
}
