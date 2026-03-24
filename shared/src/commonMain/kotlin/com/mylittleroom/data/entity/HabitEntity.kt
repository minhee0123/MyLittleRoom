package com.mylittleroom.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val emoji: String,
    val isCompleted: Boolean = false,
    val currentStreak: Int = 0,
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)
