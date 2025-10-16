package com.telematics.core.model.logbook

import com.telematics.core.content.R.string

enum class LogbookDateFormat(val value: String, val stringId: Int) {
    STANDARD("standard", string.logbook_date_format_standard),
    ISO("iso", string.logbook_date_format_iso),
    US("us", string.logbook_date_format_us),
    EU("eu", string.logbook_date_format_eu)
}

/*
"DateFormat": "standard"  // YYYY-MM-DD HH:MM:SS (default)
"DateFormat": "iso"       // ISO 8601 format (2024-01-01T12:00:00+00:00)
"DateFormat": "us"        // MM/DD/YYYY HH:MM AM/PM
"DateFormat": "eu"        // DD/MM/YYYY HH:MM
*/
