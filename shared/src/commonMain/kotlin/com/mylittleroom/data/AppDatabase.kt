package com.mylittleroom.data

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.mylittleroom.data.dao.FurnitureDao
import com.mylittleroom.data.dao.HabitDao
import com.mylittleroom.data.dao.HabitLogDao
import com.mylittleroom.data.dao.UserStatusDao
import com.mylittleroom.data.entity.FurnitureEntity
import com.mylittleroom.data.entity.HabitEntity
import com.mylittleroom.data.entity.HabitLogEntity
import com.mylittleroom.data.entity.UserStatusEntity

/**
 * Room 데이터베이스 — habits, habit_logs, furniture, user_status 4개 테이블.
 * KSP가 AppDatabaseConstructor를 통해 플랫폼별 구현체를 자동 생성한다.
 */
@Database(
    entities = [
        HabitEntity::class,
        HabitLogEntity::class,
        FurnitureEntity::class,
        UserStatusEntity::class
    ],
    version = 2,
    exportSchema = true
)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun habitLogDao(): HabitLogDao
    abstract fun furnitureDao(): FurnitureDao
    abstract fun userStatusDao(): UserStatusDao

    companion object {
        const val DATABASE_NAME = "my_little_room.db"
    }
}

// Room KSP will generate the actual implementation
@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}
