package com.fittrack.android.ui.session

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.fittrack.android.data.model.ExerciseSet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionScreen(
    onNavigateBack: () -> Unit,
    onNavigateToExerciseSelect: (Long) -> Unit,
    viewModel: SessionViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("トレーニング記録") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.endSession()
                            onNavigateBack()
                        }
                    ) {
                        Text("終了", color = MaterialTheme.colorScheme.error)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    uiState.session?.let { session ->
                        onNavigateToExerciseSelect(session.id)
                    }
                },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("エクササイズ追加") }
            )
        }
    ) { padding ->
        if (uiState.exercises.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        "エクササイズを追加してトレーニングを始めましょう",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.exercises) { exerciseRecord ->
                    ExerciseRecordCard(
                        record = exerciseRecord,
                        onTogglePrevious = { viewModel.togglePreviousRecord(exerciseRecord.exercise.id) },
                        onAddSet = { viewModel.addSet(exerciseRecord.exercise.id) },
                        onUpdateWeight = { set, weight -> viewModel.updateSetWeight(set, weight) },
                        onUpdateReps = { set, reps -> viewModel.updateSetReps(set, reps) },
                        onCompleteSet = { set -> viewModel.completeSet(set) },
                        onDeleteSet = { set -> viewModel.deleteSet(set) }
                    )
                }
                // FABの分のスペース
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
private fun ExerciseRecordCard(
    record: ExerciseRecordState,
    onTogglePrevious: () -> Unit,
    onAddSet: () -> Unit,
    onUpdateWeight: (ExerciseSet, Float) -> Unit,
    onUpdateReps: (ExerciseSet, Int) -> Unit,
    onCompleteSet: (ExerciseSet) -> Unit,
    onDeleteSet: (ExerciseSet) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // エクササイズ名 + 前回を見るボタン
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.exercise.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onTogglePrevious) {
                    Text(
                        if (record.showPrevious) "閉じる" else "前回を見る",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            // 前回の記録（展開表示）
            AnimatedVisibility(visible = record.showPrevious) {
                if (record.previousSets.isNotEmpty()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "前回の記録",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            record.previousSets.forEach { prevSet ->
                                Text(
                                    "セット${prevSet.setNumber}: ${prevSet.weightKg}kg × ${prevSet.reps}回",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        "前回の記録なし",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // ヘッダー行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "SET",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.width(36.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "重量(kg)",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "回数",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(48.dp))
            }

            // セット一覧
            record.sets.forEach { set ->
                SetInputRow(
                    set = set,
                    onUpdateWeight = { weight -> onUpdateWeight(set, weight) },
                    onUpdateReps = { reps -> onUpdateReps(set, reps) },
                    onComplete = { onCompleteSet(set) },
                    onDelete = { onDeleteSet(set) }
                )
            }

            // セット追加ボタン
            TextButton(
                onClick = onAddSet,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("セット追加")
            }
        }
    }
}

@Composable
private fun SetInputRow(
    set: ExerciseSet,
    onUpdateWeight: (Float) -> Unit,
    onUpdateReps: (Int) -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    var weightText by remember(set.id, set.weightKg) {
        mutableStateOf(if (set.weightKg > 0) set.weightKg.toString() else "")
    }
    var repsText by remember(set.id, set.reps) {
        mutableStateOf(if (set.reps > 0) set.reps.toString() else "")
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // セット番号
        Text(
            text = "${set.setNumber}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(36.dp)
        )

        // 重量入力
        OutlinedTextField(
            value = weightText,
            onValueChange = { value ->
                weightText = value
                value.toFloatOrNull()?.let { onUpdateWeight(it) }
            },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            suffix = { Text("kg") },
            enabled = !set.isCompleted,
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )

        // 回数入力
        OutlinedTextField(
            value = repsText,
            onValueChange = { value ->
                repsText = value
                value.toIntOrNull()?.let { onUpdateReps(it) }
            },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            suffix = { Text("回") },
            enabled = !set.isCompleted,
            textStyle = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )

        // 完了/削除ボタン
        if (set.isCompleted) {
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "完了",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            FilledIconButton(
                onClick = onComplete,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = "記録")
            }
        }
    }
}
