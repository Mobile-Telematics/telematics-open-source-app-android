package com.telematics.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.telematics.core.database.entity.DriveCoinsEntity

@Dao
interface DriveCoinsDao {
    @Query("SELECT * FROM drive_coins WHERE user_id = :userId")
    fun fetch(userId: String): DriveCoinsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entity: DriveCoinsEntity)

    @Query("DELETE FROM drive_coins WHERE user_id = :userId")
    fun clear(userId: String)
}