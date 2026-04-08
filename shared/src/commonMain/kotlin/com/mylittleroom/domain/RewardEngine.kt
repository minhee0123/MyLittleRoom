package com.mylittleroom.domain

import com.mylittleroom.data.entity.FurnitureEntity
import kotlin.random.Random

/**
 * 보상 이벤트 — UI에서 다이얼로그로 표시할 일회성 이벤트.
 */
sealed class RewardEvent {
    /** 레벨업 보상 (진화 시 새 단계 이름 포함) */
    data class LevelUp(val newLevel: Int, val newStageName: String?) : RewardEvent()
    /** 랜덤 가구 박스 해금 보상 */
    data class FurnitureUnlocked(val furniture: FurnitureEntity) : RewardEvent()
    /** 연속 달성 마일스톤 보상 */
    data class StreakMilestone(val habitTitle: String, val days: Int) : RewardEvent()
}

/**
 * 보상 엔진 — 연속 마일스톤 판정, 레벨업 보상, 랜덤 가구 선택을 담당한다.
 */
object RewardEngine {

    /** 보상이 주어지는 연속일수 마일스톤 */
    private val STREAK_MILESTONES = setOf(3, 7, 14, 21, 30, 50, 100)

    /** 연속일수가 마일스톤(3,7,14,21,30,50,100일)에 해당하는지 확인 */
    fun checkStreakMilestone(streak: Int): Boolean = streak in STREAK_MILESTONES

    /** 레벨업 시 랜덤 박스를 지급할지 판정 */
    fun shouldGiveRandomBox(oldLevel: Int, newLevel: Int): Boolean {
        return newLevel > oldLevel
    }

    /** 잠긴 가구 목록에서 랜덤 1개를 선택한다 (비어있으면 null) */
    fun pickRandomLockedFurniture(locked: List<FurnitureEntity>): FurnitureEntity? {
        if (locked.isEmpty()) return null
        return locked[Random.nextInt(locked.size)]
    }
}
