package com.mylittleroom.data.repository

import com.mylittleroom.data.dao.UserStatusDao
import com.mylittleroom.data.entity.UserStatusEntity
import com.mylittleroom.domain.GamificationEngine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * 경험치 추가 결과 — 레벨업 여부 판정에 사용.
 */
data class ExpResult(
    val oldLevel: Int,
    val newLevel: Int,
    val expGained: Int,
    val didLevelUp: Boolean
)

/**
 * 유저 리포지토리 — 레벨/경험치 관리 및 레벨업 판정을 담당한다.
 */
class UserRepository(private val userStatusDao: UserStatusDao) {

    /** 유저 상태를 실시간 관찰 (없으면 기본값 반환) */
    fun getUserStatus(): Flow<UserStatusEntity> {
        return userStatusDao.getUserStatus().map { it ?: UserStatusEntity() }
    }

    /** 유저 레코드가 없으면 기본값으로 생성한다 (앱 최초 실행 시). */
    suspend fun ensureUserExists() {
        userStatusDao.upsert(UserStatusEntity())
    }

    /** 연속일수 기반으로 경험치를 추가하고 레벨업 여부를 반환한다. */
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
