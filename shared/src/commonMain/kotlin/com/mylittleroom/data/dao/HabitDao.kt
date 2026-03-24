package com.mylittleroom.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mylittleroom.data.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: HabitEntity): Long

    @Update
    suspend fun update(habit: HabitEntity)

    @Delete
    suspend fun delete(habit: HabitEntity)

    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Long): HabitEntity?

    @Query("UPDATE habits SET isCompleted = :isCompleted WHERE id = :id")
    suspend fun updateCompletion(id: Long, isCompleted: Boolean)

    @Query("UPDATE habits SET currentStreak = :streak WHERE id = :id")
    suspend fun updateStreak(id: Long, streak: Int)

    @Query("UPDATE habits SET isCompleted = 0")
    suspend fun resetAllCompletions()
}
