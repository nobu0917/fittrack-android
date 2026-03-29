package com.fittrack.android.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.fittrack.android.data.model.Exercise
import com.fittrack.android.data.model.ExerciseCategory
import com.fittrack.android.data.model.ExerciseSet
import com.fittrack.android.data.model.TrainingSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [Exercise::class, TrainingSession::class, ExerciseSet::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FitTrackDatabase : RoomDatabase() {

    abstract fun exerciseDao(): ExerciseDao
    abstract fun sessionDao(): SessionDao
    abstract fun exerciseSetDao(): ExerciseSetDao

    companion object {
        /**
         * プリセットエクササイズデータ
         */
        fun getPresetExercises(): List<Exercise> = listOf(
            // 胸（CHEST）
            Exercise(name = "ベンチプレス", category = ExerciseCategory.CHEST, muscleGroup = "大胸筋"),
            Exercise(name = "インクラインベンチプレス", category = ExerciseCategory.CHEST, muscleGroup = "大胸筋上部"),
            Exercise(name = "ダンベルフライ", category = ExerciseCategory.CHEST, muscleGroup = "大胸筋"),
            Exercise(name = "チェストプレス", category = ExerciseCategory.CHEST, muscleGroup = "大胸筋"),
            Exercise(name = "プッシュアップ", category = ExerciseCategory.CHEST, muscleGroup = "大胸筋"),

            // 背中（BACK）
            Exercise(name = "デッドリフト", category = ExerciseCategory.BACK, muscleGroup = "脊柱起立筋"),
            Exercise(name = "ラットプルダウン", category = ExerciseCategory.BACK, muscleGroup = "広背筋"),
            Exercise(name = "ベントオーバーロウ", category = ExerciseCategory.BACK, muscleGroup = "広背筋"),
            Exercise(name = "シーテッドロウ", category = ExerciseCategory.BACK, muscleGroup = "広背筋"),
            Exercise(name = "チンアップ", category = ExerciseCategory.BACK, muscleGroup = "広背筋"),
            Exercise(name = "懸垂", category = ExerciseCategory.BACK, muscleGroup = "広背筋"),

            // 肩（SHOULDER）
            Exercise(name = "ショルダープレス", category = ExerciseCategory.SHOULDER, muscleGroup = "三角筋"),
            Exercise(name = "サイドレイズ", category = ExerciseCategory.SHOULDER, muscleGroup = "三角筋中部"),
            Exercise(name = "フロントレイズ", category = ExerciseCategory.SHOULDER, muscleGroup = "三角筋前部"),
            Exercise(name = "リアデルトフライ", category = ExerciseCategory.SHOULDER, muscleGroup = "三角筋後部"),

            // 腕（ARM）
            Exercise(name = "バーベルカール", category = ExerciseCategory.ARM, muscleGroup = "上腕二頭筋"),
            Exercise(name = "ハンマーカール", category = ExerciseCategory.ARM, muscleGroup = "上腕二頭筋"),
            Exercise(name = "トライセプスプッシュダウン", category = ExerciseCategory.ARM, muscleGroup = "上腕三頭筋"),
            Exercise(name = "ディップス", category = ExerciseCategory.ARM, muscleGroup = "上腕三頭筋"),
            Exercise(name = "スカルクラッシャー", category = ExerciseCategory.ARM, muscleGroup = "上腕三頭筋"),

            // 脚（LEG）
            Exercise(name = "スクワット", category = ExerciseCategory.LEG, muscleGroup = "大腿四頭筋"),
            Exercise(name = "レッグプレス", category = ExerciseCategory.LEG, muscleGroup = "大腿四頭筋"),
            Exercise(name = "ルーマニアンデッドリフト", category = ExerciseCategory.LEG, muscleGroup = "ハムストリング"),
            Exercise(name = "レッグカール", category = ExerciseCategory.LEG, muscleGroup = "ハムストリング"),
            Exercise(name = "レッグエクステンション", category = ExerciseCategory.LEG, muscleGroup = "大腿四頭筋"),
            Exercise(name = "カーフレイズ", category = ExerciseCategory.LEG, muscleGroup = "下腿三頭筋"),
            Exercise(name = "ランジ", category = ExerciseCategory.LEG, muscleGroup = "大腿四頭筋"),

            // 体幹（CORE）
            Exercise(name = "プランク", category = ExerciseCategory.CORE, muscleGroup = "腹直筋"),
            Exercise(name = "クランチ", category = ExerciseCategory.CORE, muscleGroup = "腹直筋"),
            Exercise(name = "レッグレイズ", category = ExerciseCategory.CORE, muscleGroup = "腹直筋下部"),
            Exercise(name = "ロシアンツイスト", category = ExerciseCategory.CORE, muscleGroup = "腹斜筋"),

            // 有酸素（CARDIO）
            Exercise(name = "ランニング", category = ExerciseCategory.CARDIO, muscleGroup = "全身"),
            Exercise(name = "サイクリング", category = ExerciseCategory.CARDIO, muscleGroup = "下半身"),
            Exercise(name = "ロープ跳び", category = ExerciseCategory.CARDIO, muscleGroup = "全身"),
            Exercise(name = "ウォーキング", category = ExerciseCategory.CARDIO, muscleGroup = "全身"),
        )
    }
}
