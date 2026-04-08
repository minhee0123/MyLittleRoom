package com.mylittleroom.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock

/**
 * 습관 엔티티 — 사용자가 등록한 습관 정보를 저장한다.
 *
 * @property id          자동 생성되는 고유 ID
 * @property title       습관 이름 (예: "물 8잔 마시기")
 * @property emoji       대표 이모지 아이콘
 * @property repeatDays  반복 요일 CSV (0=월 ~ 6=일, 예: "0,1,2,3,4")
 * @property createdAt   생성 시각 (epoch milliseconds)
 */
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val emoji: String,
    val repeatDays: String = "0,1,2,3,4,5,6",
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)
