package com.mylittleroom.data

import android.content.Context
import androidx.room.Room

/** Android DB 팩토리 — Context를 이용하여 앱 DB 경로에 Room DB를 생성한다. */
class AndroidDatabaseFactory(private val context: Context) : DatabaseFactory {
    override fun create(): androidx.room.RoomDatabase.Builder<AppDatabase> {
        val dbFile = context.getDatabasePath(AppDatabase.DATABASE_NAME)
        return Room.databaseBuilder<AppDatabase>(
            context = context.applicationContext,
            name = dbFile.absolutePath
        )
    }
}
