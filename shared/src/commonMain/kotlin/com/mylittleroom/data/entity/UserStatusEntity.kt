package com.mylittleroom.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mylittleroom.data.converter.StringListConverter

@Entity(tableName = "user_status")
@TypeConverters(StringListConverter::class)
data class UserStatusEntity(
    @PrimaryKey
    val id: Int = 1, // Singleton row
    val level: Int = 1,
    val currentExp: Int = 0,
    val unlockedFurnitureIds: List<String> = emptyList()
)
