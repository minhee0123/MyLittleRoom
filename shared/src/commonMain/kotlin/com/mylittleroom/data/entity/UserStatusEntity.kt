package com.mylittleroom.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_status")
data class UserStatusEntity(
    @PrimaryKey
    val id: Int = 1, // Singleton row
    val level: Int = 1,
    val currentExp: Int = 0
)
