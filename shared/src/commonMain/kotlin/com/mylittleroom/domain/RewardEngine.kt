package com.mylittleroom.domain

import com.mylittleroom.data.entity.FurnitureEntity
import kotlin.random.Random

sealed class RewardEvent {
    data class LevelUp(val newLevel: Int, val newStageName: String?) : RewardEvent()
    data class FurnitureUnlocked(val furniture: FurnitureEntity) : RewardEvent()
    data class StreakMilestone(val habitTitle: String, val days: Int) : RewardEvent()
}

object RewardEngine {

    private val STREAK_MILESTONES = setOf(3, 7, 14, 21, 30, 50, 100)

    fun checkStreakMilestone(streak: Int): Boolean = streak in STREAK_MILESTONES

    fun shouldGiveRandomBox(oldLevel: Int, newLevel: Int): Boolean {
        return newLevel > oldLevel
    }

    fun pickRandomLockedFurniture(locked: List<FurnitureEntity>): FurnitureEntity? {
        if (locked.isEmpty()) return null
        return locked[Random.nextInt(locked.size)]
    }
}
