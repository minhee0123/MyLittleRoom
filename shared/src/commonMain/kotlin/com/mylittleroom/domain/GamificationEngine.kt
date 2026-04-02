package com.mylittleroom.domain

import com.mylittleroom.data.entity.UserStatusEntity

object GamificationEngine {

    private const val BASE_EXP = 10
    private const val MAX_STREAK_BONUS = 20

    fun calculateExpGain(streakDays: Int): Int {
        val streakBonus = (streakDays * 2).coerceAtMost(MAX_STREAK_BONUS)
        return BASE_EXP + streakBonus
    }

    fun expForNextLevel(level: Int): Int = level * 100

    fun applyExp(current: UserStatusEntity, expGain: Int): UserStatusEntity {
        var level = current.level
        var exp = current.currentExp + expGain
        var needed = expForNextLevel(level)

        while (exp >= needed) {
            exp -= needed
            level++
            needed = expForNextLevel(level)
        }

        return current.copy(level = level, currentExp = exp)
    }
}
