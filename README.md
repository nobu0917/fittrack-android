# FitTrack - フィットネストラッキングアプリ

FitTrackは、ジムでのトレーニング記録を効率的に管理するためのAndroidアプリです。エクササイズの重量・回数を記録し、成長を可視化します。

## 機能一覧

### ホーム画面
- 今日のセッション開始ボタン
- 最近のトレーニング履歴サマリー（種目数・セット数・総ボリューム）
- 進行中のセッション再開機能

### セッション記録画面（メイン機能）
- エクササイズ別のセット記録（重量・回数）
- **ワンタップで前回の同エクササイズ記録を展開表示**
- セットの追加・完了・削除
- 前回の重量を自動引き継ぎ
- メモ入力対応

### エクササイズ選択画面
- 部位別カテゴリフィルタリング（胸・背中・肩・腕・脚・体幹・有酸素）
- テキスト検索機能
- 35種類以上のプリセットエクササイズ

### 履歴一覧・詳細画面
- 日付別セッション一覧
- セッションの全記録表示（エクササイズ別・セット別）
- セッション削除機能

### 統計画面
- エクササイズ別の最大重量推移グラフ（Vicoチャート）
- 自己ベスト一覧

### 設定画面
- ダークモード切替（システム追従・ライト・ダーク）
- 重量単位切替（kg / lbs）

## 技術スタック

| 項目 | 技術 |
|------|------|
| 言語 | Kotlin |
| UIフレームワーク | Jetpack Compose |
| ナビゲーション | Navigation Compose |
| ローカルDB | Room (SQLite) |
| 状態管理 | ViewModel + StateFlow |
| DI | Hilt |
| グラフ | Vico |
| minSdk | 26 (Android 8.0) |
| targetSdk | 35 |
| ビルド | Gradle (Kotlin DSL) |

## プロジェクト構成

```
fittrack-android/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── kotlin/com/fittrack/android/
│           ├── MainActivity.kt
│           ├── FitTrackApplication.kt
│           ├── data/
│           │   ├── SettingsDataStore.kt
│           │   ├── db/
│           │   │   ├── FitTrackDatabase.kt
│           │   │   ├── Converters.kt
│           │   │   ├── ExerciseDao.kt
│           │   │   ├── SessionDao.kt
│           │   │   └── ExerciseSetDao.kt
│           │   ├── model/
│           │   │   ├── Exercise.kt
│           │   │   ├── TrainingSession.kt
│           │   │   ├── ExerciseSet.kt
│           │   │   └── SessionWithDetails.kt
│           │   └── repository/
│           │       ├── ExerciseRepository.kt
│           │       └── SessionRepository.kt
│           ├── ui/
│           │   ├── home/
│           │   │   ├── HomeScreen.kt
│           │   │   └── HomeViewModel.kt
│           │   ├── session/
│           │   │   ├── SessionScreen.kt
│           │   │   └── SessionViewModel.kt
│           │   ├── exercise/
│           │   │   ├── ExerciseSelectScreen.kt
│           │   │   └── ExerciseSelectViewModel.kt
│           │   ├── history/
│           │   │   ├── HistoryScreen.kt
│           │   │   ├── HistoryViewModel.kt
│           │   │   ├── HistoryDetailScreen.kt
│           │   │   └── HistoryDetailViewModel.kt
│           │   ├── stats/
│           │   │   ├── StatsScreen.kt
│           │   │   └── StatsViewModel.kt
│           │   ├── settings/
│           │   │   ├── SettingsScreen.kt
│           │   │   └── SettingsViewModel.kt
│           │   ├── navigation/
│           │   │   └── FitTrackNavigation.kt
│           │   └── theme/
│           │       ├── Color.kt
│           │       ├── Type.kt
│           │       └── Theme.kt
│           └── di/
│               └── AppModule.kt
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
├── .gitignore
└── README.md
```

## セットアップ手順

### 前提条件
- Android Studio Ladybug (2024.2.1) 以降
- JDK 17
- Android SDK 35

### ビルド手順

1. リポジトリをクローン
```bash
git clone https://github.com/YOUR_USERNAME/fittrack-android.git
cd fittrack-android
```

2. Android Studioでプロジェクトを開く
```
File → Open → fittrack-android フォルダを選択
```

3. Gradleの同期を待つ（自動で開始されます）

4. エミュレータまたは実機でビルド・実行
```
Run → Run 'app' (Shift+F10)
```

### デバッグAPKの生成
```bash
./gradlew assembleDebug
```
APKは `app/build/outputs/apk/debug/app-debug.apk` に生成されます。

## プリセットエクササイズ

アプリ初回起動時に以下のエクササイズが自動登録されます：

| カテゴリ | エクササイズ |
|---------|------------|
| 胸 | ベンチプレス、インクラインベンチプレス、ダンベルフライ、チェストプレス、プッシュアップ |
| 背中 | デッドリフト、ラットプルダウン、ベントオーバーロウ、シーテッドロウ、チンアップ、懸垂 |
| 肩 | ショルダープレス、サイドレイズ、フロントレイズ、リアデルトフライ |
| 腕 | バーベルカール、ハンマーカール、トライセプスプッシュダウン、ディップス、スカルクラッシャー |
| 脚 | スクワット、レッグプレス、ルーマニアンデッドリフト、レッグカール、レッグエクステンション、カーフレイズ、ランジ |
| 体幹 | プランク、クランチ、レッグレイズ、ロシアンツイスト |
| 有酸素 | ランニング、サイクリング、ロープ跳び、ウォーキング |

## デザイン

- **Material Design 3** 準拠
- **ダイナミックカラー** 対応（Android 12+）、フォールバックはBlue系
- ダークモード / ライトモード切替対応
- タップ領域は最低44dp確保
- ボトムナビゲーションバーで主要画面を切り替え

## データモデル

### Exercise（エクササイズマスター）
- id, name, category, muscleGroup, isCustom

### TrainingSession（トレーニングセッション）
- id, date, startTime, endTime, note

### ExerciseSet（エクササイズセット）
- id, sessionId, exerciseId, setNumber, weightKg, reps, isCompleted, note

## ライセンス

このプロジェクトはMITライセンスの下で公開されています。

## バージョン

- v1.0.0 - MVP初期リリース
