package com.mylittleroom.domain

import com.mylittleroom.data.entity.UserStatusEntity

/**
 * 게이미피케이션 엔진 — 경험치 계산과 레벨업 로직을 담당한다.
 *
 * - EXP 공식: 기본 10 + min(연속일수 * 2, 20)
 * - 레벨업 공식: 필요 EXP = 현재 레벨 * 100
 */
object GamificationEngine {

    private const val BASE_EXP = 10
    private const val MAX_STREAK_BONUS = 20

    /** 연속일수 기반 획득 경험치 계산 */
    fun calculateExpGain(streakDays: Int): Int {
        val streakBonus = (streakDays * 2).coerceAtMost(MAX_STREAK_BONUS)
        return BASE_EXP + streakBonus
    }

    /** 다음 레벨에 필요한 총 경험치 */
    fun expForNextLevel(level: Int): Int = level * 100

    /** 경험치를 적용하고 레벨업 루프를 돌려 최종 상태를 반환한다. */
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
