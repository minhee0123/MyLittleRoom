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

/** 습관 + 오늘 완료 여부 + 연속일수를 묶은 UI 모델 */
data class HabitWithStatus(
    val habit: HabitEntity,
    val isCompletedToday: Boolean,
    val streak: Int = 0
)

/** 습관 목록 화면의 UI 상태 */
data class HabitListUiState(
    val habits: List<HabitWithStatus> = emptyList(),
    val isLoading: Boolean = true
)

/**
 * 습관 목록 ViewModel — 습관 CRUD, 완료 토글, 보상 이벤트 발행을 담당한다.
 * 습관 체크 → EXP 추가 → 레벨업/마일스톤 판정 → 보상 다이얼로그 트리거.
 */
class HabitListViewModel(
    private val habitRepository: HabitRepository,
    private val userRepository: UserRepository,
    private val furnitureRepository: FurnitureRepository
) : ViewModel() {

    private val _streaks = MutableStateFlow<Map<Long, Int>>(emptyMap())

    /** 보상 이벤트 (레벨업/마일스톤/가구) — UI에서 collect하여 다이얼로그 표시 */
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

    /** 습관 완료 토글 — 완료 시 EXP 추가 + 레벨업/마일스톤/가구 보상 판정 */
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

    /** 새 습관을 추가한다. */
    fun addHabit(title: String, emoji: String, repeatDays: String) {
        viewModelScope.launch {
            habitRepository.addHabit(title, emoji, repeatDays)
        }
    }

    /** 기존 습관 정보를 수정한다. */
    fun updateHabit(habit: HabitEntity) {
        viewModelScope.launch {
            habitRepository.updateHabit(habit)
        }
    }

    /** 습관을 삭제한다. */
    fun deleteHabit(habit: HabitEntity) {
        viewModelScope.launch {
            habitRepository.deleteHabit(habit)
        }
    }

    /** 오늘 요일에 해당하는 습관만 필터링 (repeatDays CSV 기준) */
    private fun filterTodayHabits(habits: List<HabitEntity>): List<HabitEntity> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val dayIndex = today.dayOfWeek.ordinal
        return habits.filter { habit ->
            val days = habit.repeatDays.split(",").mapNotNull { it.trim().toIntOrNull() }
            dayIndex in days
        }
    }
}
