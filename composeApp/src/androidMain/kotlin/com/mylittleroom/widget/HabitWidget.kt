package com.mylittleroom.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.mylittleroom.MainActivity
import com.mylittleroom.data.AndroidDatabaseFactory
import com.mylittleroom.data.buildAppDatabase
import com.mylittleroom.data.entity.HabitLogEntity
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn

/** Android 홈 화면 위젯 — 오늘의 습관 목록과 캐릭터 레벨을 표시한다. */
class HabitWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = loadWidgetData(context)

        provideContent {
            GlanceTheme {
                WidgetContent(data)
            }
        }
    }
}

@Composable
private fun WidgetContent(data: WidgetData) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFFFFFBFC))
            .padding(12.dp)
            .clickable(actionStartActivity<MainActivity>()),
        verticalAlignment = Alignment.Top,
        horizontalAlignment = Alignment.Start
    ) {
        // Header
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = data.characterStage.emoji,
                style = TextStyle(fontSize = 22.sp)
            )
            Spacer(modifier = GlanceModifier.width(8.dp))
            Column {
                Text(
                    text = "Lv.${data.level} ${data.characterStage.stageName}",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ColorProvider(Color(0xFFAD3362))
                    )
                )
                Text(
                    text = "${data.completedCount}/${data.totalCount} 완료",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = ColorProvider(Color(0xFF605D62))
                    )
                )
            }
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        // Habits
        if (data.habits.isEmpty()) {
            Text(
                text = "오늘의 습관이 없어요 ✨",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = ColorProvider(Color(0xFF79767A))
                )
            )
        } else {
            data.habits.take(5).forEach { habit ->
                HabitRow(habit)
                Spacer(modifier = GlanceModifier.height(4.dp))
            }
        }
    }
}

@Composable
private fun HabitRow(habit: WidgetHabit) {
    val bgColor = if (habit.isCompleted) Color(0xFFFDE4ED) else Color(0xFFFFF0F5)
    val textColor = if (habit.isCompleted) Color(0xFF79767A) else Color(0xFF1C1B1E)

    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .background(bgColor)
            .cornerRadius(8.dp)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .clickable(
                actionRunCallback<ToggleHabitAction>(
                    actionParametersOf(habitIdKey to habit.id)
                )
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = habit.emoji,
            style = TextStyle(fontSize = 16.sp)
        )
        Spacer(modifier = GlanceModifier.width(6.dp))
        Text(
            text = habit.title,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = if (habit.isCompleted) FontWeight.Normal else FontWeight.Medium,
                color = ColorProvider(textColor)
            ),
            modifier = GlanceModifier.defaultWeight()
        )
        Text(
            text = if (habit.isCompleted) "✅" else "⬜",
            style = TextStyle(fontSize = 16.sp)
        )
    }
}

val habitIdKey = ActionParameters.Key<Long>("habit_id")

/** 위젯에서 습관 완료를 토글하는 Glance 액션 콜백 */
class ToggleHabitAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        val habitId = parameters[habitIdKey] ?: return
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()

        val db = buildAppDatabase(AndroidDatabaseFactory(context))
        try {
            val isCompleted = db.habitLogDao().isCompletedOn(habitId, today)
            if (isCompleted) {
                db.habitLogDao().deleteLog(habitId, today)
            } else {
                db.habitLogDao().insertLog(HabitLogEntity(habitId = habitId, completedDate = today))
            }
        } finally {
            db.close()
        }

        HabitWidget().update(context, glanceId)
    }
}
