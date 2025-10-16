package com.telematics.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.telematics.core.database.BuildConfig
import com.telematics.core.database.entity.LogEventEntity
import java.util.Date

@Dao
interface LogEventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(logs: LogEventEntity)

    @Query("SELECT * from log_event WHERE date >= :minDate ORDER BY date DESC")
    fun getLogs(minDate: Date = Date(Date().time - BuildConfig.LOG_KEEPING_PERIOD)): List<LogEventEntity>

    @Query("DELETE FROM log_event WHERE date <= :minDate")
    fun deleteOldLogs(minDate: Date = Date(Date().time - BuildConfig.LOG_KEEPING_PERIOD))
}