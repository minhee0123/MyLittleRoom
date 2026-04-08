package com.mylittleroom.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mylittleroom.data.entity.HabitEntity
import kotlinx.coroutines.flow.Flow

/** 습관 테이블 접근 DAO — 습관 CRUD 및 조회 */
@Dao
interface HabitDao {

    /** 습관 추가 (동일 ID 존재 시 교체) */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(habit: HabitEntity): Long

    /** 습관 수정 */
    @Update
    suspend fun update(habit: HabitEntity)

    /** 습관 삭제 (관련 로그는 CASCADE 삭제) */
    @Delete
    suspend fun delete(habit: HabitEntity)

    /** 모든 습관을 생성일 역순으로 실시간 관찰 (Flow) */
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    /** ID로 습관 단건 조회 */
    @Query("SELECT * FROM habits WHERE id = :id")
    suspend fun getHabitById(id: Long): HabitEntity?

    /** 모든 습관 1회성 스냅샷 조회 (위젯용) */
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    suspend fun getAllHabitsOnce(): List<HabitEntity>
}
