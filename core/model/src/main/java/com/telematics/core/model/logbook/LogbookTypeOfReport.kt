package com.telematics.core.model.logbook

import com.telematics.core.content.R.string

enum class LogbookTypeOfReport(val value: String, val stringId: Int) {
    DETAILED("detailed", string.logbook_type_of_report_detailed),
    DAILY_SUMMARY("daily_summary", string.logbook_type_of_report_daily_summary)
}