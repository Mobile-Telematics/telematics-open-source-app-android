package com.telematics.core.model

enum class TripRecordMode(val titleId: Int, descriptionId: Int?) {
    ALWAYS_ON(R.string.dashboard_always_on, R.string.dashboard_always_on_des),
    SHIFT_MODE(R.string.dashboard_shift_mode, R.string.dashboard_always_on_des),
    ON_DEMAND(R.string.dashboard_on_demand, R.string.dashboard_always_on_des),
    DISABLED(R.string.dashboard_disabled, null)
}