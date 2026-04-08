package com.mylittleroom.platform

/**
 * 햅틱 피드백 — 플랫폼별 진동 피드백 제공.
 * Android: Vibrator API / iOS: UIImpactFeedbackGenerator
 */
expect object HapticFeedback {
    fun light()
    fun medium()
    fun success()
}
