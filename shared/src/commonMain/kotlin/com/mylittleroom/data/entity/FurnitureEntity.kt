package com.mylittleroom.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 가구 엔티티 — 방 꾸미기용 가구 아이템 정보를 저장한다.
 *
 * @property id           고유 ID (예: "sofa_basic")
 * @property name         가구 이름 (예: "기본 소파")
 * @property category     배치 카테고리 (wall / floor / desk)
 * @property isUnlocked   잠금 해제 여부 (레벨업/연속달성 보상으로 해금)
 * @property isPlaced     방에 배치되었는지 여부
 * @property slotPosition 배치된 슬롯 위치 (wall / wall2 / floor / desk)
 */
@Entity(tableName = "furniture")
data class FurnitureEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val category: String,
    val isUnlocked: Boolean = false,
    val isPlaced: Boolean = false,
    val slotPosition: String? = null
)
