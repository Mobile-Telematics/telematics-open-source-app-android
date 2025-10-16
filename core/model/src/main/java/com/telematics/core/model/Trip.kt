package com.telematics.core.model

import com.telematics.core.model.tracking.TripData.TripType

data class Trip(
    var timeStart: String = "",
    var timeEnd: String = "",
    var dist: Float = 0f,

    var streetStart: String = "",
    var streetEnd: String = "",

    var cityStart: String = "",
    var cityEnd: String = "",

    var type: TripType = TripType.DRIVER,
    var isOriginChanged: Boolean = false,
)
