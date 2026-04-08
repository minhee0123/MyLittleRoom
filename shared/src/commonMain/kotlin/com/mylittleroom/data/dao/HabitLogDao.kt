package com.mylittleroom.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mylittleroom.data.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow

/** 습관 완료 로그 DAO — 날짜별 습관 완료 기록 관리 */
@Dao
interface HabitLogDao {

    /** 완료 로그 추가 (이미 존재하면 무시) */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLog(log: HabitLogEntity)

    /** 특정 날짜의 완료 로그 삭제 (토글 해제용) */
    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND completedDate = :date")
    suspend fun deleteLog(habitId: Long, date: String)

    /** 특정 습관이 특정 날짜에 완료되었는지 확인 */
    @Query("SELECT EXISTS(SELECT 1 FROM habit_logs WHERE habitId = :habitId AND completedDate = :date)")
    suspend fun isCompletedOn(habitId: Long, date: String): Boolean

    /** 특정 습관의 완료 날짜 목록 (연속일수 계산용, 최신순) */
    @Query("SELECT completedDate FROM habit_logs WHERE habitId = :habitId ORDER BY completedDate DESC")
    suspend fun getLogDatesForHabit(habitId: Long): List<String>

    /** 특정 날짜의 모든 완료 로그 실시간 관찰 (오늘의 습관 현황용) */
    @Query("SELECT * FROM habit_logs WHERE completedDate = :date")
    fun getLogsForDate(date: String): Flow<List<HabitLogEntity>>
}
