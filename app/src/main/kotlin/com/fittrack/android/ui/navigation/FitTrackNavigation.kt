package com.fittrack.android.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.fittrack.android.ui.exercise.ExerciseSelectScreen
import com.fittrack.android.ui.history.HistoryDetailScreen
import com.fittrack.android.ui.history.HistoryScreen
import com.fittrack.android.ui.home.HomeScreen
import com.fittrack.android.ui.session.SessionScreen
import com.fittrack.android.ui.settings.SettingsScreen
import com.fittrack.android.ui.stats.StatsScreen

/**
 * ナビゲーションルート定義
 */
object Routes {
    const val HOME = "home"
    const val HISTORY = "history"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val SESSION = "session/{sessionId}"
    const val EXERCISE_SELECT = "exercise_select/{sessionId}"
    const val HISTORY_DETAIL = "history_detail/{sessionId}"

    fun session(sessionId: Long) = "session/$sessionId"
    fun exerciseSelect(sessionId: Long) = "exercise_select/$sessionId"
    fun historyDetail(sessionId: Long) = "history_detail/$sessionId"
}

/**
 * ボトムナビゲーションのアイテム
 */
sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Home : BottomNavItem(Routes.HOME, "ホーム", Icons.Default.Home)
    data object History : BottomNavItem(Routes.HISTORY, "履歴", Icons.Default.DateRange)
    data object Stats : BottomNavItem(Routes.STATS, "統計", Icons.Default.ShowChart)
    data object Settings : BottomNavItem(Routes.SETTINGS, "設定", Icons.Default.Settings)
}

val bottomNavItems = listOf(
    BottomNavItem.Home,
    BottomNavItem.History,
    BottomNavItem.Stats,
    BottomNavItem.Settings
)

@Composable
fun FitTrackNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // ボトムナビを表示するルート
    val showBottomBar = currentDestination?.route in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = {
                                Text(
                                    item.label,
                                    fontWeight = if (currentDestination?.hierarchy?.any { it.route == item.route } == true)
                                        FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ホーム画面
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToSession = { sessionId ->
                        navController.navigate(Routes.session(sessionId))
                    },
                    onNavigateToSessionDetail = { sessionId ->
                        navController.navigate(Routes.historyDetail(sessionId))
                    }
                )
            }

            // セッション記録画面
            composable(
                route = Routes.SESSION,
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) {
                SessionScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToExerciseSelect = { sessionId ->
                        navController.navigate(Routes.exerciseSelect(sessionId))
                    }
                )
            }

            // エクササイズ選択画面
            composable(
                route = Routes.EXERCISE_SELECT,
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getLong("sessionId") ?: 0L
                ExerciseSelectScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onExerciseSelected = { exercise ->
                        // セッション画面のViewModelにエクササイズを追加
                        // popBackStackで戻り、SessionViewModelがFlowで自動更新
                        navController.previousBackStackEntry?.savedStateHandle?.set(
                            "selectedExerciseId", exercise.id
                        )
                        navController.popBackStack()
                    }
                )
            }

            // 履歴一覧画面
            composable(Routes.HISTORY) {
                HistoryScreen(
                    onNavigateToDetail = { sessionId ->
                        navController.navigate(Routes.historyDetail(sessionId))
                    }
                )
            }

            // 履歴詳細画面
            composable(
                route = Routes.HISTORY_DETAIL,
                arguments = listOf(navArgument("sessionId") { type = NavType.LongType })
            ) {
                HistoryDetailScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // 統計画面
            composable(Routes.STATS) {
                StatsScreen()
            }

            // 設定画面
            composable(Routes.SETTINGS) {
                SettingsScreen()
            }
        }
    }
}
