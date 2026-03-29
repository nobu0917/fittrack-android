package com.fittrack.android

import android.app.Application
import com.fittrack.android.data.db.FitTrackDatabase
import com.fittrack.android.data.repository.ExerciseRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class FitTrackApplication : Application() {

    @Inject
    lateinit var exerciseRepository: ExerciseRepository

    override fun onCreate() {
        super.onCreate()
        // プリセットエクササイズデータの初期投入
        CoroutineScope(Dispatchers.IO).launch {
            if (exerciseRepository.getCount() == 0) {
                exerciseRepository.insertAll(FitTrackDatabase.getPresetExercises())
            }
        }
    }
}
