package com.telematics.core.model.logbook

import com.telematics.core.content.R.string

enum class LogbookUnits(val value: String, val stringId: Int) {
    METRIC("metric", string.logbook_units_metric),
    IMPERIAL("imperial", string.logbook_units_imperial)
}