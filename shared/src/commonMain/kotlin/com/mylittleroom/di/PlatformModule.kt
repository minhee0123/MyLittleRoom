package com.mylittleroom.di

import org.koin.core.module.Module

/** 플랫폼별 DI 모듈 (Android: Context 기반 DB / iOS: 파일 경로 기반 DB) */
expect val platformModule: Module
