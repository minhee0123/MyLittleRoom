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

fun buildAppDatabase(factory: DatabaseFactory): AppDatabase {
    return factory.create()
        .fallbackToDestructiveMigration(true)
        .build()
}
