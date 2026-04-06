package com.mylittleroom.platform

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

private var appContext: Context? = null

fun initHapticFeedback(context: Context) {
    appContext = context.applicationContext
}

actual object HapticFeedback {
    private fun vibrate(millis: Long) {
        val context = appContext ?: return
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            manager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
        vibrator?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                it.vibrate(VibrationEffect.createOneShot(millis, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                it.vibrate(millis)
            }
        }
    }

    actual fun light() = vibrate(10)
    actual fun medium() = vibrate(30)
    actual fun success() = vibrate(50)
}
