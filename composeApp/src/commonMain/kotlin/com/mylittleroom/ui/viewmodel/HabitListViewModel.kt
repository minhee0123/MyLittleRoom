package com.mylittleroom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mylittleroom.data.entity.HabitEntity
import com.mylittleroom.data.repository.FurnitureRepository
import com.mylittleroom.data.repository.HabitRepository
import com.mylittleroom.data.repository.UserRepository
import com.mylittleroom.domain.RewardEngine
import com.mylittleroom.domain.RewardEvent
import com.mylittleroom.domain.model.CharacterStage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

data class HabitWithStatus(
    val habit: HabitEntity,
    val isCompletedToday: Boolean,
    val streak: Int = 0
)

data class HabitListUiState(
    val habits: List<HabitWithStatus> = emptyList(),
    val isLoading: Boolean = true
)

class HabitListViewModel(
    private val habitRepository: HabitRepository,
    private val userRepository: UserRepository,
    private val furnitureRepository: FurnitureRepository
) : ViewModel() {

    private val _streaks = MutableStateFlow<Map<Long, Int>>(emptyMap())
    private val _rewardEvents = MutableSharedFlow<RewardEvent>()
    val rewardEvents = _rewardEvents.asSharedFlow()

    val uiState: StateFlow<HabitListUiState> = combine(
        habitRepository.getAllHabits(),
        habitRepository.getTodayLogs(),
        _streaks.asStateFlow()
    ) { habits, logs, streaks ->
        val todayHabits = filterTodayHabits(habits)
        HabitListUiState(
            habits = todayHabits.map { habit ->
                HabitWithStatus(
                    habit = habit,
                    isCompletedToday = logs.any { it.habitId == habit.id },
                    streak = streaks[habit.id] ?: 0
                )
            },
            isLoading = false
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HabitListUiState())

    init {
        loadStreaks()
    }

    private fun loadStreaks() {
        viewModelScope.launch {
            habitRepository.getAllHabits().collect { habits ->
                val newStreaks = habits.associate { habit ->
                    habit.id to habitRepository.calculateStreak(habit.id)
                }
                _streaks.value = newStreaks
            }
        }
    }

    fun toggleHabitCompletion(habitId: Long) {
        viewModelScope.launch {
            val nowCompleted = habitRepository.toggleCompletion(habitId)
            val streak = habitRepository.calculateStreak(habitId)
            _streaks.value = _streaks.value + (habitId to streak)

            if (nowCompleted) {
                val expResult = userRepository.addExp(streak)

                // Check level up → random furniture box
                if (expResult.didLevelUp) {
                    val newStage = CharacterStage.fromLevel(expResult.newLevel)
                    val oldStage = CharacterStage.fromLevel(expResult.oldLevel)
                    val stageName = if (newStage != oldStage) newStage.stageName else null
                    _rewardEvents.emit(RewardEvent.LevelUp(expResult.newLevel, stageName))

                    // Give random furniture box on level up
                    val unlocked = furnitureRepository.tryRandomUnlock()
                    if (unlocked != null) {
                        _rewardEvents.emit(RewardEvent.FurnitureUnlocked(unlocked))
                    }
                }

                // Check streak milestone
                if (RewardEngine.checkStreakMilestone(streak)) {
                    val habit = habitRepository.getAllHabits().let { flow ->
                        var result: HabitEntity? = null
                        uiState.value.habits.find { it.habit.id == habitId }?.habit
                    }
                    val title = habit?.title ?: ""
                    _rewardEvents.emit(RewardEvent.StreakMilestone(title, streak))

                    // Also give furniture on streak milestone
                    val unlocked = furnitureRepository.tryRandomUnlock()
                    if (unlocked != null) {
                        _rewardEvents.emit(RewardEvent.FurnitureUnlocked(unlocked))
                    }
                }
            }
        }
    }

    fun addHabit(title: String, emoji: String, repeatDays: String) {
        viewModelScope.launch {
            habitRepository.addHabit(title, emoji, repeatDays)
        }
    }

    fun updateHabit(habit: HabitEntity) {
        viewModelScope.launch {
            habitRepository.updateHabit(habit)
        }
    }

    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habit)
        }
    }

    private fun filterTodayHabits(habits: List<HabitEntity>): List<HabitEntity> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val dayIndex = today.dayOfWeek.ordinal
        return habits.filter { habit ->
            val days = habit.repeatDays.split(",").mapNotNull { it.trim().toIntOrNull() }
            dayIndex in days
        }
    }
}
