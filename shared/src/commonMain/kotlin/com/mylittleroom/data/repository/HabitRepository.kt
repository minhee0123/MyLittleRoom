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

/**
 * 습관 리포지토리 — 습관 CRUD, 완료 토글, 연속일수 계산을 담당한다.
 * DAO 2개(HabitDao, HabitLogDao)를 조합하여 비즈니스 로직을 제공.
 */
class HabitRepository(
    private val habitDao: HabitDao,
    private val habitLogDao: HabitLogDao
) {
    /** 모든 습관 실시간 관찰 */
    fun getAllHabits(): Flow<List<HabitEntity>> = habitDao.getAllHabits()

    /** 오늘 날짜의 완료 로그 실시간 관찰 */
    fun getTodayLogs(): Flow<List<HabitLogEntity>> {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()
        return habitLogDao.getLogsForDate(today)
    }

    /** 새 습관을 추가한다. @return 생성된 습관 ID */
    suspend fun addHabit(title: String, emoji: String, repeatDays: String): Long {
        return habitDao.insert(
            HabitEntity(title = title, emoji = emoji, repeatDays = repeatDays)
        )
    }

    /** 습관을 삭제한다 (관련 로그도 CASCADE 삭제). */
    suspend fun deleteHabit(habit: HabitEntity) {
        habitDao.delete(habit)
    }

    /** 습관 정보를 수정한다. */
    suspend fun updateHabit(habit: HabitEntity) {
        habitDao.update(habit)
    }

    /** ID로 습관을 조회한다. */
    suspend fun getHabitById(id: Long): HabitEntity? {
        return habitDao.getHabitById(id)
    }

    /** 오늘의 완료 상태를 토글한다. @return 토글 후 완료 여부 */
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

    /** 오늘부터 과거로 거슬러 올라가며 연속 완료 일수를 계산한다. */
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
