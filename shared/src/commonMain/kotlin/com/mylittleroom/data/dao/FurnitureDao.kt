package com.mylittleroom.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mylittleroom.data.entity.FurnitureEntity
import kotlinx.coroutines.flow.Flow

/** 가구 DAO — 가구 해금/배치/조회 관리 */
@Dao
interface FurnitureDao {

    /** 기본 가구 일괄 삽입 (이미 존재하면 무시) */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(furniture: List<FurnitureEntity>)

    /** 해금된 가구 실시간 관찰 */
    @Query("SELECT * FROM furniture WHERE isUnlocked = 1")
    fun getUnlockedFurniture(): Flow<List<FurnitureEntity>>

    /** 현재 방에 배치된 가구 실시간 관찰 */
    @Query("SELECT * FROM furniture WHERE isPlaced = 1")
    fun getPlacedFurniture(): Flow<List<FurnitureEntity>>

    /** 특정 가구를 잠금 해제한다 */
    @Query("UPDATE furniture SET isUnlocked = 1 WHERE id = :furnitureId")
    suspend fun unlockFurniture(furnitureId: String)

    /** 특정 슬롯에 가구를 배치한다 */
    @Query("UPDATE furniture SET isPlaced = 1, slotPosition = :slot WHERE id = :furnitureId")
    suspend fun placeFurniture(furnitureId: String, slot: String)

    /** 특정 슬롯에 배치된 가구를 제거 (새 가구 배치 전 호출) */
    @Query("UPDATE furniture SET isPlaced = 0, slotPosition = NULL WHERE slotPosition = :slot")
    suspend fun clearSlot(slot: String)

    /** 아직 잠긴 가구 목록 (랜덤 보상 후보) */
    @Query("SELECT * FROM furniture WHERE isUnlocked = 0")
    suspend fun getLockedFurniture(): List<FurnitureEntity>
}
