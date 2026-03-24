package com.mylittleroom.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mylittleroom.data.converter.StringListConverter
import com.mylittleroom.data.dao.HabitDao
import com.mylittleroom.data.dao.UserStatusDao
import com.mylittleroom.data.entity.HabitEntity
import com.mylittleroom.data.entity.UserStatusEntity

@Database(
    entities = [HabitEntity::class, UserStatusEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun userStatusDao(): UserStatusDao

    companion object {
        const val DATABASE_NAME = "my_little_room.db"
    }
}
