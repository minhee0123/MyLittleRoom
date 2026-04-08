package com.mylittleroom

/** 플랫폼 정보 인터페이스 — 플랫폼별 이름(Android/iOS) 제공 */
interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
