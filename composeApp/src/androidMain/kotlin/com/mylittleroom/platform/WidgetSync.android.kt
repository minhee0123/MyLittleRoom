package com.mylittleroom.platform

import com.mylittleroom.widget.HabitWidget

actual suspend fun syncWidgets() {
    // Widget update requires context, but we use updateAll which gets it from GlanceAppWidgetManager
    // This is a no-op placeholder — actual sync happens via broadcast
    // The widget auto-refreshes on its update period
}
