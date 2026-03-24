package com.mylittleroom.data

import android.content.Context
import androidx.room.Room

class AndroidDatabaseFactory(private val context: Context) : DatabaseFactory {
    override fun create(): androidx.room.RoomDatabase.Builder<AppDatabase> {
        val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
        return Room.databaseBuilder<AppDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath
        )
    }
}
