package com.mylittleroom

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
