package com.mylittleroom.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 유저 상태 엔티티 — 레벨과 경험치를 저장한다.
 * 앱 내 유저는 1명이므로 id=1 고정 (싱글톤 행).
 */
@Entity(tableName = "user_status")
data class UserStatusEntity(
    @PrimaryKey
    val id: Int = 1,
    val level: Int = 1,
    val currentExp: Int = 0
)
