package com.mylittleroom.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mylittleroom.data.entity.HabitEntity
import com.mylittleroom.data.repository.HabitRepository
import com.mylittleroom.ui.screen.AddHabitScreen
import com.mylittleroom.ui.screen.CharacterRoomScreen
import com.mylittleroom.ui.screen.EditHabitScreen
import com.mylittleroom.ui.screen.FurniturePlacementScreen
import com.mylittleroom.ui.screen.HabitListScreen
import com.mylittleroom.ui.viewmodel.HabitListViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject

// ── 네비게이션 라우트 정의 (type-safe, kotlinx.serialization) ──
@Serializable object RoomRoute              // 캐릭터 방 (홈)
@Serializable object HabitsRoute            // 습관 목록
@Serializable object AddHabitRoute          // 습관 추가
@Serializable data class EditHabitRoute(val habitId: Long)  // 습관 수정
@Serializable object FurniturePlacementRoute // 가구 배치

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: Any
)

/**
 * 앱 최상위 내비게이션 — 바텀 내비게이션(마이룸/습관) + NavHost 5개 화면.
 * RoomRoute, HabitsRoute에서만 바텀바 표시.
 */
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        BottomNavItem("마이룸", Icons.Default.Home, RoomRoute),
        BottomNavItem("습관", Icons.Default.CheckCircle, HabitsRoute)
    )

    val showBottomBar = currentDestination?.let { dest ->
        dest.hasRoute<RoomRoute>() || dest.hasRoute<HabitsRoute>()
    } ?: true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    bottomNavItems.forEach { item ->
                        val selected = when (item.route) {
                            is RoomRoute -> currentDestination?.hasRoute<RoomRoute>() == true
                            is HabitsRoute -> currentDestination?.hasRoute<HabitsRoute>() == true
                            else -> false
                        }
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    navController.navigate(item.route) {
                                        popUpTo(RoomRoute) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, style = MaterialTheme.typography.labelSmall) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            )
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = RoomRoute,
            modifier = Modifier.padding(padding)
        ) {
            composable<RoomRoute> {
                CharacterRoomScreen(
                    onFurniturePlacement = {
                        navController.navigate(FurniturePlacementRoute)
                    }
                )
            }
            composable<HabitsRoute> {
                val viewModel: HabitListViewModel = koinViewModel()
                HabitListScreen(
                    onAddHabit = { navController.navigate(AddHabitRoute) },
                    onEditHabit = { habitId -> navController.navigate(EditHabitRoute(habitId)) },
                    viewModel = viewModel
                )
            }
            composable<AddHabitRoute> {
                val parentEntry = navController.getBackStackEntry(HabitsRoute)
                val viewModel: HabitListViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
                AddHabitScreen(
                    onSave = { title, emoji, repeatDays ->
                        viewModel.addHabit(title, emoji, repeatDays)
                        navController.popBackStack()
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable<EditHabitRoute> { backStackEntry ->
                val parentEntry = navController.getBackStackEntry(HabitsRoute)
                val viewModel: HabitListViewModel = koinViewModel(viewModelStoreOwner = parentEntry)
                val habitRepository: HabitRepository = koinInject()

                val route = backStackEntry.arguments
                val habitId = route?.getLong("habitId") ?: 0L

                var habit by remember { mutableStateOf<HabitEntity?>(null) }
                LaunchedEffect(habitId) {
                    habit = habitRepository.getHabitById(habitId)
                }

                habit?.let { h ->
                    EditHabitScreen(
                        habit = h,
                        onSave = { updated ->
                            viewModel.updateHabit(updated)
                            navController.popBackStack()
                        },
                        onBack = { navController.popBackStack() }
                    )
                }
            }
            composable<FurniturePlacementRoute> {
                FurniturePlacementScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
