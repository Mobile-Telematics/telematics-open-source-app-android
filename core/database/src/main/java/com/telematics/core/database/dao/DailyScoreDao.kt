package com.telematics.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.telematics.core.database.entity.DailyScoreEntity

@Dao
interface DailyScoreDao {
    @Query("SELECT * FROM daily_score WHERE user_id = :userId")
    fun getUserScores(userId: String): List<DailyScoreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserScores(entities: List<DailyScoreEntity>)

    @Query("DELETE FROM daily_score WHERE user_id = :userId")
    fun deleteUserScores(userId: String)
}