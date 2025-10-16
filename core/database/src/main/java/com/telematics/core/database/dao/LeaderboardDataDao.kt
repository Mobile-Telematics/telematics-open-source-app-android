package com.telematics.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.telematics.core.database.entity.LeaderboardDataEntity

@Dao
interface LeaderboardDataDao {
    @Query("SELECT * FROM leaderboard_data WHERE user_id = :userId ORDER BY position ASC")
    fun getLeaderboardData(userId: String): List<LeaderboardDataEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLeaderboardData(entity: List<LeaderboardDataEntity>)

    @Query("DELETE FROM leaderboard_data WHERE user_id = :userId")
    fun deleteLeaderboardData(userId: String)
}