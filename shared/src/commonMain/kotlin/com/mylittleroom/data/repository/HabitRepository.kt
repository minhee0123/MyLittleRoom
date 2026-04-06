package com.mylittleroom.data.repository

import com.mylittleroom.data.dao.HabitDao
import com.mylittleroom.data.dao.HabitLogDao
import com.mylittleroom.data.entity.HabitEntity
import com.mylittleroom.data.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

class HabitRepository(
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao
) {
    fun getAllHabits(): Flow<List<HabitEntity>> = habitDao.getAllHabits()

    fun getTodayLogs(): Flow<List<HabitLogEntity>> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        return habitLogDao.getLogsForDate(today)
    }

    suspend fun addHabit(title: String, emoji: String, repeatDays: String): Long {
        return habitDao.insert(
            HabitEntity(title = title, emoji = emoji, repeatDays = repeatDays)
        )
    }

    suspend fun deleteHabit(habit: HabitEntity) {
        habitDao.delete(habit)
    }

    suspend fun updateHabit(habit: HabitEntity) {
        habitDao.update(habit)
    }

    suspend fun getHabitById(id: Long): HabitEntity? {
        return habitDao.getHabitById(id)
    }

    suspend fun toggleCompletion(habitId: Long): Boolean {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        val isCompleted = habitLogDao.isCompletedOn(habitId, today)
        if (isCompleted) {
            habitLogDao.deleteLog(habitId, today)
        } else {
            habitLogDao.insertLog(HabitLogEntity(habitId = habitId, completedDate = today))
        }
        return !isCompleted
    }

    suspend fun calculateStreak(habitId: Long): Int {
        val dates = habitLogDao.getLogDatesForHabit(habitId)
        if (dates.isEmpty()) return 0

        var streak = 0
        var expected = Clock.System.todayIn(TimeZone.currentSystemDefault())

        for (dateStr in dates) {
            val date = LocalDate.parse(dateStr)
            if (date == expected) {
                streak++
                expected = LocalDate.fromEpochDays(expected.toEpochDays() - 1)
            } else if (date < expected) {
                break
            }
        }
        return streak
    }
}
