package com.telematics.core.common.extension

import android.content.Context
import java.text.DecimalFormat
import java.util.Locale

fun Float.format(format: String = "0.0"): String {
    Locale.setDefault(Locale.US)
    return DecimalFormat(format).format(this)
}

fun convertDpToPx(context: Context, dp: Float): Float {
    return dp * context.resources.displayMetrics.density
}