package com.mylittleroom.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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
