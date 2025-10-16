package com.telematics.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.telematics.core.database.entity.ScoreEntity

@Dao
interface ScoreDao {
    @Query("SELECT * FROM score WHERE user_id = :userId ORDER BY position ASC")
    fun getUserScores(userId: String): List<ScoreEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserScores(entities: List<ScoreEntity>)

    @Query("DELETE FROM score WHERE user_id = :userId")
    fun deleteUserScores(userId: String)
}