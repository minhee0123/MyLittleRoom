package com.mylittleroom.widget

import android.content.Context
import com.mylittleroom.data.AndroidDatabaseFactory
import com.mylittleroom.data.buildAppDatabase
import com.mylittleroom.data.entity.UserStatusEntity
import com.mylittleroom.domain.GamificationEngine
import com.mylittleroom.domain.model.CharacterStage
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/** 위젯에 표시할 데이터 모델 */
data class WidgetData(
    val level: Int,
    val currentExp: Int,
    val maxExp: Int,
    val characterStage: CharacterStage,
    val habits: List<WidgetHabit>,
    val completedCount: Int,
    val totalCount: Int
)

data class WidgetHabit(
    val id: Long,
    val title: String,
    val emoji: String,
    val isCompleted: Boolean
)

/** Room DB에 직접 접근하여 위젯용 데이터를 로드한다 (Koin 미사용). */
suspend fun loadWidgetData(context: Context): WidgetData {
    val db = buildAppDatabase(AndroidDatabaseFactory(context))
    try {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        val dayIndex = today.dayOfWeek.ordinal

        val allHabits = db.habitDao().getAllHabitsOnce()
        val todayHabits = allHabits.filter { habit ->
            val days = habit.repeatDays.split(",").mapNotNull { it.trim().toIntOrNull() }
            dayIndex in days
        }

        val todayStr = today.toString()
        val widgetHabits = todayHabits.map { habit ->
            val isCompleted = db.habitLogDao().isCompletedOn(habit.id, todayStr)
            WidgetHabit(
                id = habit.id,
                title = habit.title,
                emoji = habit.emoji,
                isCompleted = isCompleted
            )
        }

        val userStatus = db.userStatusDao().getUserStatusOnce() ?: UserStatusEntity()

        return WidgetData(
            level = userStatus.level,
            currentExp = userStatus.currentExp,
            maxExp = GamificationEngine.expForNextLevel(userStatus.level),
            characterStage = CharacterStage.fromLevel(userStatus.level),
            habits = widgetHabits,
            completedCount = widgetHabits.count { it.isCompleted },
            totalCount = widgetHabits.size
        )
    } finally {
        db.close()
    }
}
