package com.telematics.core.data.datasource.local

import com.telematics.core.datastore.PreferenceStorage
import com.telematics.core.model.TripRecordMode
import javax.inject.Inject

class TrackingLocalDataSourceImpl @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
) : TrackingLocalDataSource {

    override var tripRecordMode
        get() = try {
            TripRecordMode.valueOf(preferenceStorage.tripRecordMode)
        } catch (_: Exception) {
            TripRecordMode.ALWAYS_ON
        }
        set(value) {
            preferenceStorage.tripRecordMode = value.name
        }

    override var isTripRecordModeActive
        get() = preferenceStorage.isTripRecordModeActive
        set(value) {
            preferenceStorage.isTripRecordModeActive = value
        }
}

interface TrackingLocalDataSource {
    var tripRecordMode: TripRecordMode
    var isTripRecordModeActive: Boolean
}