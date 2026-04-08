package com.mylittleroom.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mylittleroom.data.entity.UserStatusEntity
import kotlinx.coroutines.flow.Flow

/** 유저 상태 DAO — 레벨/경험치 읽기·쓰기 (싱글톤 행 id=1) */
@Dao
interface UserStatusDao {

    /** 유저 상태 삽입 또는 갱신 */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(userStatus: UserStatusEntity)

    /** 유저 상태 실시간 관찰 (Flow) */
    @Query("SELECT * FROM user_status WHERE id = 1")
    fun getUserStatus(): Flow<UserStatusEntity?>

    /** 레벨과 경험치를 한 번에 갱신 */
    @Query("UPDATE user_status SET level = :level, currentExp = :exp WHERE id = 1")
    suspend fun updateLevelAndExp(level: Int, exp: Int)

    /** 유저 상태 1회성 스냅샷 조회 (위젯용) */
    @Query("SELECT * FROM user_status WHERE id = 1")
    suspend fun getUserStatusOnce(): UserStatusEntity?
}
