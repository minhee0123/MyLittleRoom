package com.mylittleroom.platform

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

private var appContext: Context? = null

/** Application.onCreate에서 호출하여 Context를 초기화한다. */
fun initHapticFeedback(context: Context) {
    appContext = context.applicationContext
}

/** Android 햅틱 피드백 구현 — Vibrator 서비스를 통해 진동 */
actual object HapticFeedback {
    /** 지정 밀리초만큼 진동 (API 26+ VibrationEffect 사용) */
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
