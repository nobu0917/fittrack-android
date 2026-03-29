package com.fittrack.android.data.repository

import com.fittrack.android.data.db.ExerciseDao
import com.fittrack.android.data.model.Exercise
import com.fittrack.android.data.model.ExerciseCategory
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExerciseRepository @Inject constructor(
    private val exerciseDao: ExerciseDao
) {
    fun getAllExercises(): Flow<List<Exercise>> = exerciseDao.getAllExercises()

    fun getExercisesByCategory(category: ExerciseCategory): Flow<List<Exercise>> =
        exerciseDao.getExercisesByCategory(category)

    fun searchExercises(query: String): Flow<List<Exercise>> =
        exerciseDao.searchExercises(query)

    suspend fun getExerciseById(id: Long): Exercise? = exerciseDao.getExerciseById(id)

    suspend fun insert(exercise: Exercise): Long = exerciseDao.insert(exercise)

    suspend fun insertAll(exercises: List<Exercise>) = exerciseDao.insertAll(exercises)

    suspend fun getCount(): Int = exerciseDao.getCount()
}
