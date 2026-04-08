package com.mylittleroom.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey

/**
 * 습관 완료 로그 — 특정 습관이 특정 날짜에 완료되었는지 기록한다.
 * (habitId + completedDate) 복합 기본키, 습관 삭제 시 CASCADE 삭제.
 */
@Entity(
    tableName = "habit_logs",
    primaryKeys = ["habitId", "completedDate"],
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class HabitLogEntity(
    val habitId: Long,
    val completedDate: String // ISO format "2026-03-28"
)
