package com.mylittleroom.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mylittleroom.data.entity.HabitEntity
import com.mylittleroom.data.entity.HabitLogEntity
import com.mylittleroom.data.repository.HabitRepository
import com.mylittleroom.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    private val userRepository: UserRepository
) : ViewModel() {

    private val _streaks = MutableStateFlow<Map<Long, Int>>(emptyMap())

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
            if (nowCompleted) {
                val streak = habitRepository.calculateStreak(habitId)
                userRepository.addExp(streak)
                _streaks.value = _streaks.value + (habitId to streak)
            } else {
                val streak = habitRepository.calculateStreak(habitId)
                _streaks.value = _streaks.value + (habitId to streak)
            }
        }
    }

    fun addHabit(title: String, emoji: String, repeatDays: String) {
        viewModelScope.launch {
            habitRepository.addHabit(title, emoji, repeatDays)
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
