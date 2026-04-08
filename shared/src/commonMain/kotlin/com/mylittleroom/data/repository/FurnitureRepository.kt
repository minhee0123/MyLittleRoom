package com.mylittleroom.data.repository

import com.mylittleroom.data.dao.FurnitureDao
import com.mylittleroom.data.entity.FurnitureEntity
import kotlinx.coroutines.flow.Flow

/**
 * 가구 리포지토리 — 기본 가구 초기화, 배치, 잠금 해제, 랜덤 보상을 담당한다.
 */
class FurnitureRepository(private val furnitureDao: FurnitureDao) {

    /** 방에 배치된 가구 실시간 관찰 */
    fun getPlacedFurniture(): Flow<List<FurnitureEntity>> = furnitureDao.getPlacedFurniture()

    /** 해금된 가구 실시간 관찰 */
    fun getUnlockedFurniture(): Flow<List<FurnitureEntity>> = furnitureDao.getUnlockedFurniture()

    /** 앱 최초 실행 시 기본 가구 8개를 삽입한다 (IGNORE이므로 중복 안전). */
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

    /** 특정 슬롯에 가구를 배치한다 (기존 슬롯 가구는 자동 제거). */
    suspend fun placeFurniture(furnitureId: String, slot: String) {
        furnitureDao.clearSlot(slot)
        furnitureDao.placeFurniture(furnitureId, slot)
    }

    /** 특정 가구를 잠금 해제한다. */
    suspend fun unlockFurniture(furnitureId: String) {
        furnitureDao.unlockFurniture(furnitureId)
    }

    /** 아직 잠긴 가구 목록을 반환한다. */
    suspend fun getLockedFurniture(): List<FurnitureEntity> {
        return furnitureDao.getLockedFurniture()
    }

    /** 잠긴 가구 중 랜덤 1개를 해금한다. 모두 해금됐으면 null 반환. */
    suspend fun tryRandomUnlock(): FurnitureEntity? {
        val locked = furnitureDao.getLockedFurniture()
        val pick = com.mylittleroom.domain.RewardEngine.pickRandomLockedFurniture(locked)
        if (pick != null) {
            furnitureDao.unlockFurniture(pick.id)
        }
        return pick
    }
}
