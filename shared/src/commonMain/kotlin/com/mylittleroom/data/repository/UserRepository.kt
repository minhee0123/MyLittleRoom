package com.mylittleroom.data.repository

import com.mylittleroom.data.dao.UserStatusDao
import com.mylittleroom.data.entity.UserStatusEntity
import com.mylittleroom.domain.GamificationEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

data class ExpResult(
    val oldLevel: Int,
    val newLevel: Int,
    val expGained: Int,
    val didLevelUp: Boolean
)

class UserRepository(private val userStatusDao: UserStatusDao) {

    fun getUserStatus(): Flow<UserStatusEntity> {
        return userStatusDao.getUserStatus().map { it ?: UserStatusEntity() }
    }

    suspend fun ensureUserExists() {
        userStatusDao.upsert(UserStatusEntity())
    }

    suspend fun addExp(streakDays: Int): ExpResult {
        val current = userStatusDao.getUserStatus().first() ?: UserStatusEntity()
        val expGain = GamificationEngine.calculateExpGain(streakDays)
        val updated = GamificationEngine.applyExp(current, expGain)
        userStatusDao.updateLevelAndExp(updated.level, updated.currentExp)
        return ExpResult(
            oldLevel = current.level,
            newLevel = updated.level,
            expGained = expGain,
            didLevelUp = updated.level > current.level
        )
    }
}
