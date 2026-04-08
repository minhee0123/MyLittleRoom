package com.mylittleroom.data

import androidx.room.RoomDatabase

/**
 * Platform-specific database builder factory.
 * Android requires Context, iOS does not — so we use an interface
 * and provide platform implementations via Koin.
 */
interface DatabaseFactory {
    fun create(): RoomDatabase.Builder<AppDatabase>
}

/** DatabaseFactory를 사용하여 AppDatabase를 빌드한다 (마이그레이션 실패 시 파괴적 재생성). */
fun buildAppDatabase(factory: DatabaseFactory): AppDatabase {
    return factory.create()
        .fallbackToDestructiveMigration(true)
        .build()
}
