package com.mylittleroom.data

import androidx.room.Room
import platform.Foundation.NSHomeDirectory

/** iOS DB 팩토리 — Documents 디렉토리에 Room DB를 생성한다. */
class IosDatabaseFactory : DatabaseFactory {
    override fun create(): androidx.room.RoomDatabase.Builder<AppDatabase> {
        val dbFilePath = NSHomeDirectory() + "/Documents/${AppDatabase.DATABASE_NAME}"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFilePath
        )
    }
}
