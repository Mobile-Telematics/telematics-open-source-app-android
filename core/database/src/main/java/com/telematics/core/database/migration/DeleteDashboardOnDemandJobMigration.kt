package com.telematics.core.database.migration

import androidx.room.DeleteTable
import androidx.room.migration.AutoMigrationSpec

@DeleteTable.Entries(
    DeleteTable(tableName = "DashboardOnDemandJob")
)
class DeleteDashboardOnDemandJobMigration : AutoMigrationSpec