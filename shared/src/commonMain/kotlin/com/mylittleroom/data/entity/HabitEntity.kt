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
    val repeatDays: String = "0,1,2,3,4,5,6", // 0=Mon..6=Sun, CSV
    val createdAt: Long = Clock.System.now().toEpochMilliseconds()
)
