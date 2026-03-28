package com.mylittleroom.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey

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
