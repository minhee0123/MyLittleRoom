package com.mylittleroom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mylittleroom.data.entity.FurnitureEntity
import com.mylittleroom.data.entity.HabitEntity
import com.mylittleroom.data.entity.HabitLogEntity
import com.mylittleroom.data.entity.UserStatusEntity
import com.mylittleroom.data.repository.FurnitureRepository
import com.mylittleroom.data.repository.HabitRepository
import com.mylittleroom.data.repository.UserRepository
import com.mylittleroom.domain.GamificationEngine
import com.mylittleroom.domain.model.CharacterStage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class CharacterRoomUiState(
    val level: Int = 1,
    val currentExp: Int = 0,
    val maxExp: Int = 100,
    val characterStage: CharacterStage = CharacterStage.STAR_DUST,
    val placedFurniture: List<FurnitureEntity> = emptyList(),
    val todayHabitsTotal: Int = 0,
    val todayHabitsCompleted: Int = 0,
    val isLoading: Boolean = true
)

class CharacterRoomViewModel(
    private val userRepository: UserRepository,
    private val habitRepository: HabitRepository,
    private val furnitureRepository: FurnitureRepository
) : ViewModel() {

    val uiState: StateFlow<CharacterRoomUiState> = combine(
        userRepository.getUserStatus(),
        habitRepository.getAllHabits(),
        habitRepository.getTodayLogs(),
        furnitureRepository.getPlacedFurniture()
    ) { user, habits, logs, furniture ->
        val todayHabits = filterTodayHabits(habits)
        CharacterRoomUiState(
            level = user.level,
            currentExp = user.currentExp,
            maxExp = GamificationEngine.expForNextLevel(user.level),
            characterStage = CharacterStage.fromLevel(user.level),
            placedFurniture = furniture,
            todayHabitsTotal = todayHabits.size,
            todayHabitsCompleted = todayHabits.count { habit ->
                logs.any { it.habitId == habit.id }
            },
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CharacterRoomUiState())

    init {
        viewModelScope.launch {
            userRepository.ensureUserExists()
            furnitureRepository.initDefaultFurniture()
        }
    }

    private fun filterTodayHabits(habits: List<HabitEntity>): List<HabitEntity> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        // dayOfWeek: MONDAY=1 ... SUNDAY=7, our format: 0=Mon..6=Sun
        val dayIndex = today.dayOfWeek.ordinal
        return habits.filter { habit ->
            val days = habit.repeatDays.split(",").mapNotNull { it.trim().toIntOrNull() }
            dayIndex in days
        }
    }
}
