package com.mylittleroom.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mylittleroom.data.entity.FurnitureEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FurnitureDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(furniture: List<FurnitureEntity>)

    @Query("SELECT * FROM furniture WHERE isUnlocked = 1")
    fun getUnlockedFurniture(): Flow<List<FurnitureEntity>>

    @Query("SELECT * FROM furniture WHERE isPlaced = 1")
    fun getPlacedFurniture(): Flow<List<FurnitureEntity>>

    @Query("UPDATE furniture SET isUnlocked = 1 WHERE id = :furnitureId")
    suspend fun unlockFurniture(furnitureId: String)

    @Query("UPDATE furniture SET isPlaced = 1, slotPosition = :slot WHERE id = :furnitureId")
    suspend fun placeFurniture(furnitureId: String, slot: String)

    @Query("UPDATE furniture SET isPlaced = 0, slotPosition = NULL WHERE slotPosition = :slot")
    suspend fun clearSlot(slot: String)

    @Query("SELECT * FROM furniture WHERE isUnlocked = 0")
    suspend fun getLockedFurniture(): List<FurnitureEntity>
}
