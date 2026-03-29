package com.fittrack.android.di

import android.content.Context
import androidx.room.Room
import com.fittrack.android.data.db.ExerciseDao
import com.fittrack.android.data.db.ExerciseSetDao
import com.fittrack.android.data.db.FitTrackDatabase
import com.fittrack.android.data.db.SessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FitTrackDatabase {
        return Room.databaseBuilder(
            context,
            FitTrackDatabase::class.java,
            "fittrack_database"
        ).build()
    }

    @Provides
    fun provideExerciseDao(database: FitTrackDatabase): ExerciseDao = database.exerciseDao()

    @Provides
    fun provideSessionDao(database: FitTrackDatabase): SessionDao = database.sessionDao()

    @Provides
    fun provideExerciseSetDao(database: FitTrackDatabase): ExerciseSetDao = database.exerciseSetDao()
}
