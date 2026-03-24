package com.mylittleroom.data

import androidx.room.Room
import platform.Foundation.NSHomeDirectory

class IosDatabaseFactory : DatabaseFactory {
    override fun create(): androidx.room.RoomDatabase.Builder<AppDatabase> {
        val dbFilePath = NSHomeDirectory() + "/Documents/${AppDatabase.DATABASE_NAME}"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFilePath
        )
    }
}
