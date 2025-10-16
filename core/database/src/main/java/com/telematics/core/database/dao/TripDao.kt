package com.telematics.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.telematics.core.database.entity.TripEntity

@Dao
interface TripDao {
    @Query("SELECT * FROM trip WHERE user_id = :userId")
    fun getUserTrip(userId: String): TripEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserTrip(entity: TripEntity)

    @Query("DELETE FROM trip WHERE user_id = :userId")
    fun deleteUserTrips(userId: String)
}