package com.mylittleroom.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mylittleroom.data.entity.UserStatusEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(userStatus: UserStatusEntity)

    @Query("SELECT * FROM user_status WHERE id = 1")
    fun getUserStatus(): Flow<UserStatusEntity?>

    @Query("UPDATE user_status SET level = :level, currentExp = :exp WHERE id = 1")
    suspend fun updateLevelAndExp(level: Int, exp: Int)

    @Query("SELECT * FROM user_status WHERE id = 1")
    suspend fun getUserStatusOnce(): UserStatusEntity?
}
