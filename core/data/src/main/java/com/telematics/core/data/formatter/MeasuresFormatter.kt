package com.telematics.core.data.formatter

import android.os.Build
import android.util.Log
import com.telematics.core.data.repository.SettingsRepository
import com.telematics.core.model.measures.DateMeasure
import com.telematics.core.model.measures.DistanceMeasure
import com.telematics.core.model.measures.TimeMeasure
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MeasuresFormatterImpl @Inject constructor(
    private val settingsRepository: SettingsRepository
) : MeasuresFormatter {

    private val dateMeasure: DateMeasure
        get() {
            return settingsRepository.getDateMeasure()
        }

    private val timeMeasure: TimeMeasure
        get() {
            return settingsRepository.getTimeMeasure()
        }

    private val distanceMeasure: DistanceMeasure
        get() {
            return settingsRepository.getDistanceMeasure()
        }

    private val dateMonthDay = SimpleDateFormat("MMM d", Locale.ENGLISH)

    private val fullNewDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)

    private val serverDateFormatOld =
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH)

    val serverDateFormatNew = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.US)
    } else {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ", Locale.US)
    }

    private val hh_mm_ss = SimpleDateFormat("HH:mm:ss", Locale.ENGLISH)
    private val hh_mm_ss_a = SimpleDateFormat("hh:mm:ssaa", Locale.ENGLISH)
    private val hh_mm = SimpleDateFormat("HH:mm", Locale.ENGLISH)
    private val hh_mm_a = SimpleDateFormat("hh:mm a", Locale.ENGLISH)

    private val MMMM_d_yyyy = SimpleDateFormat("MMMM d yyyy", Locale.ENGLISH)
    private val d_MMMM_yyyy = SimpleDateFormat("d MMMM yyyy", Locale.ENGLISH)

    private val dd_MM = SimpleDateFormat("dd.MM", Locale.ENGLISH)
    private val MM_dd = SimpleDateFormat("MM.dd", Locale.ENGLISH)

    private val MM_dd_ = SimpleDateFormat("MM/dd", Locale.ENGLISH)
    private val dd_MM_ = SimpleDateFormat("dd/MM", Locale.ENGLISH)


    override fun getFullNewDate(date: Date?): String {
        return if (date == null) {
            ""
        } else fullNewDate.format(date)
    }

    override fun getDateMonthDay(date: Date?): String {
        return if (date == null) {
            ""
        } else dateMonthDay.format(date)
    }

    override fun parseDate(date: String): Date? {
        return try {
            serverDateFormatNew.parse(date)
        } catch (_: ParseException) {
            try {
                serverDateFormatOld.parse(date)
            } catch (_: Exception) {
                null
            }
        }
    }

    override fun getTime(date: Date?): String {
        if (date == null) return ""

        return when (timeMeasure) {
            TimeMeasure.H24 -> hh_mm.format(date)
            TimeMeasure.H12 -> hh_mm_a.format(date)
        }
    }

    override fun getDateYearTime(date: Date?): String {
        if (date == null) return ""
        val d = when (dateMeasure) {
            DateMeasure.DD_MM -> d_MMMM_yyyy.format(date)
            DateMeasure.MM_DD -> MMMM_d_yyyy.format(date)
        }
        val t = when (timeMeasure) {
            TimeMeasure.H24 -> hh_mm.format(date)
            TimeMeasure.H12 -> hh_mm_a.format(date)
        }
        return "$d, $t"
    }

    override fun parseFullNewDate(date: String): Date? {
        return try {
            fullNewDate.parse(date)
        } catch (e: ParseException) {
            Log.d("TAG", "ParseException " + e.message)
            null
        }
    }

    override fun getDateWithTime(date: Date?): String {

        if (date == null) return ""

        val d = when (dateMeasure) {
            DateMeasure.DD_MM -> dd_MM.format(date)
            DateMeasure.MM_DD -> MM_dd.format(date)
        }

        val t = when (timeMeasure) {
            TimeMeasure.H24 -> hh_mm.format(date)
            TimeMeasure.H12 -> hh_mm_a.format(date)
        }

        return "$d, $t"
    }

    override fun getDistanceByKm(km: Int): Double {
        return getDistanceByKm(km.toDouble())
    }

    override fun getDistanceByKm(km: Float): Double {
        return getDistanceByKm(km.toDouble())
    }

    override fun getDistanceByKm(km: Double): Double {

        val k = when (distanceMeasure) {
            DistanceMeasure.KM -> 1.0
            DistanceMeasure.MI -> 0.621371
        }
        return km * k
    }

    override fun getDistanceMeasureValue(): DistanceMeasure {
        return distanceMeasure
    }

    override fun getDateForDemandMode(time: Long?): String {
        val date = if (time != null) Date(time) else Date(System.currentTimeMillis())

        val t = when (timeMeasure) {
            TimeMeasure.H24 -> hh_mm_ss.format(date)
            TimeMeasure.H12 -> hh_mm_ss_a.format(date)
        }
        val d = when (dateMeasure) {
            DateMeasure.DD_MM -> dd_MM_.format(date)
            DateMeasure.MM_DD -> MM_dd_.format(date)
        }
        return "$d $t"
    }
}

interface MeasuresFormatter {

    fun getFullNewDate(date: Date?): String
    fun getDateMonthDay(date: Date?): String
    fun parseDate(date: String): Date?
    fun getTime(date: Date?): String
    fun getDateYearTime(date: Date?): String

    fun parseFullNewDate(date: String): Date?
    fun getDateWithTime(date: Date?): String

    fun getDistanceByKm(km: Double): Double
    fun getDistanceByKm(km: Float): Double
    fun getDistanceByKm(km: Int): Double
    fun getDistanceMeasureValue(): DistanceMeasure

    fun getDateForDemandMode(time: Long?): String
}