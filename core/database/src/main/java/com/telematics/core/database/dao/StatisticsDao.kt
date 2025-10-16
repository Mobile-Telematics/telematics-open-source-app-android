package com.telematics.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.telematics.core.database.entity.StatisticsEntity

@Dao
interface StatisticsDao {
    @Query("SELECT * FROM statistics WHERE user_id = :userId")
    fun getUserStatistics(userId: String): StatisticsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserStatistics(entity: StatisticsEntity)

    @Query("DELETE FROM statistics WHERE user_id = :userId")
    fun deleteUserStatistics(userId: String)
}