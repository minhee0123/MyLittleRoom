package com.mylittleroom.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mylittleroom.data.entity.HabitLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitLogDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLog(log: HabitLogEntity)

    @Query("DELETE FROM habit_logs WHERE habitId = :habitId AND completedDate = :date")
    suspend fun deleteLog(habitId: Long, date: String)

    @Query("SELECT EXISTS(SELECT 1 FROM habit_logs WHERE habitId = :habitId AND completedDate = :date)")
    suspend fun isCompletedOn(habitId: Long, date: String): Boolean

    @Query("SELECT completedDate FROM habit_logs WHERE habitId = :habitId ORDER BY completedDate DESC")
    suspend fun getLogDatesForHabit(habitId: Long): List<String>

    @Query("SELECT * FROM habit_logs WHERE completedDate = :date")
    fun getLogsForDate(date: String): Flow<List<HabitLogEntity>>
}
