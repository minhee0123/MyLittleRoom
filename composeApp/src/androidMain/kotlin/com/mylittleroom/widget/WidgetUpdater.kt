package com.mylittleroom.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll

/** 모든 HabitWidget 인스턴스를 강제 갱신한다. */
suspend fun updateHabitWidgets(context: Context) {
    HabitWidget().updateAll(context)
}
