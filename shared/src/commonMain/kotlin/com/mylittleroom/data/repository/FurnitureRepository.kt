package com.mylittleroom.data.repository

import com.mylittleroom.data.dao.FurnitureDao
import com.mylittleroom.data.entity.FurnitureEntity
import kotlinx.coroutines.flow.Flow

class FurnitureRepository(private val furnitureDao: FurnitureDao) {

    fun getPlacedFurniture(): Flow<List<FurnitureEntity>> = furnitureDao.getPlacedFurniture()

    fun getUnlockedFurniture(): Flow<List<FurnitureEntity>> = furnitureDao.getUnlockedFurniture()

    suspend fun initDefaultFurniture() {
        val defaults = listOf(
            FurnitureEntity("sofa_basic", "기본 소파", "wall", isUnlocked = true),
            FurnitureEntity("frame_basic", "기본 액자", "wall"),
            FurnitureEntity("plant_basic", "기본 화분", "floor", isUnlocked = true),
            FurnitureEntity("lamp_basic", "기본 조명", "desk"),
            FurnitureEntity("rug_basic", "기본 러그", "floor"),
            FurnitureEntity("shelf_basic", "기본 선반", "wall"),
            FurnitureEntity("clock_basic", "기본 시계", "wall"),
            FurnitureEntity("cushion_basic", "기본 쿠션", "floor"),
        )
        furnitureDao.insertAll(defaults)
    }

    suspend fun placeFurniture(furnitureId: String, slot: String) {
        furnitureDao.clearSlot(slot)
        furnitureDao.placeFurniture(furnitureId, slot)
    }

    suspend fun unlockFurniture(furnitureId: String) {
        furnitureDao.unlockFurniture(furnitureId)
    }
}
