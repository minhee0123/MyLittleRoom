package com.mylittleroom.platform

/** 위젯 동기화 — 앱에서 습관 토글 시 위젯을 즉시 갱신한다 (플랫폼별 구현). */
expect suspend fun syncWidgets()
