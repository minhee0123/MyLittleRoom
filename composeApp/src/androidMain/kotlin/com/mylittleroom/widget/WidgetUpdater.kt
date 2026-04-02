package com.mylittleroom.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAll

suspend fun updateHabitWidgets(context: Context) {
    HabitWidget().updateAll(context)
}
