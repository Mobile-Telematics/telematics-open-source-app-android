package com.telematics.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.telematics.core.database.entity.EcoScoreEntity

@Dao
interface EcoScoreDao {
    @Query("SELECT * FROM eco_score WHERE user_id = :userId")
    fun getUserEcoScore(userId: String): EcoScoreEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserEcoScore(entity: EcoScoreEntity)

    @Query("DELETE FROM eco_score WHERE user_id = :userId")
    fun deleteUserEcoScores(userId: String)
}