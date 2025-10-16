package com.telematics.core.database

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.telematics.core.database.converter.DateConverter
import com.telematics.core.database.dao.DailyScoreDao
import com.telematics.core.database.dao.DriveCoinsDao
import com.telematics.core.database.dao.EcoScoreDao
import com.telematics.core.database.dao.LeaderboardDataDao
import com.telematics.core.database.dao.LogEventDao
import com.telematics.core.database.dao.ScoreDao
import com.telematics.core.database.dao.StatisticsDao
import com.telematics.core.database.dao.TripDao
import com.telematics.core.database.dao.VideoDataDao
import com.telematics.core.database.entity.DailyScoreEntity
import com.telematics.core.database.entity.DriveCoinsEntity
import com.telematics.core.database.entity.EcoScoreEntity
import com.telematics.core.database.entity.LeaderboardDataEntity
import com.telematics.core.database.entity.LogEventEntity
import com.telematics.core.database.entity.ScoreEntity
import com.telematics.core.database.entity.StatisticsEntity
import com.telematics.core.database.entity.TripEntity
import com.telematics.core.database.entity.VideoDataEntity
import com.telematics.core.database.migration.DeleteDashboardOnDemandJobMigration

@Database(
    entities = [
        LogEventEntity::class,
        EcoScoreEntity::class,
        DailyScoreEntity::class,
        ScoreEntity::class,
        StatisticsEntity::class,
        TripEntity::class,
        VideoDataEntity::class,
        LeaderboardDataEntity::class,
        DriveCoinsEntity::class
    ],
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = DeleteDashboardOnDemandJobMigration::class)
    ],
    version = BuildConfig.DATABASE_VERSION,
    exportSchema = true
)
@TypeConverters(DateConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract val logEventDao: LogEventDao
    abstract val ecoScoreDao: EcoScoreDao
    abstract val scoreDao: ScoreDao
    abstract val dailyScoreDao: DailyScoreDao
    abstract val statisticsDao: StatisticsDao
    abstract val tripDao: TripDao
    abstract val videoDataDao: VideoDataDao
    abstract val leaderboardDataDao: LeaderboardDataDao
    abstract val driveCoinsDao: DriveCoinsDao

    companion object {
        @VisibleForTesting
        val DATABASE_NAME = BuildConfig.DATABASE_NAME

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        fun getDatabasePath(context: Context) = context.getDatabasePath(DATABASE_NAME).absolutePath
        fun getDatabaseFile(context: Context) = context.getDatabasePath(DATABASE_NAME)
        fun getDatabaseSize(context: Context) = context.getDatabasePath(DATABASE_NAME).length()

        private fun buildDatabase(context: Context) = Room
            .databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
            .addMigrations(
//                MIGRATION_1_2,
            )
            .build()

        // Add database migration
        //@VisibleForTesting
        //val MIGRATION_1_2 = Migration1To2()
    }
}