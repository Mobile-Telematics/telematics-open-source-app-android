package com.telematics.core.logger

import android.util.Log
import com.telematics.core.database.dao.LogEventDao
import com.telematics.core.database.entity.LogEventEntity
import com.telematics.core.logger.Logger.Companion.TAG
import java.util.Date
import javax.inject.Inject

class LoggerImpl @Inject constructor(
    private val logEventDao: LogEventDao
) : Logger {


    @Volatile
    override var logEnabled: Boolean = false

    @Volatile
    override var databaseEnabled: Boolean = false

    @Volatile
    override var level: Logger.LogLevel = Logger.LogLevel.NONE

    private fun saveEvent(tag: String, log: String, logLevel: Logger.LogLevel) {
        if (databaseEnabled) logEventDao.insert(
            LogEventEntity(
                message = if (tag != TAG) "$tag: $log" else log,
                type = logLevel.name,
                date = Date()
            )
        )
    }

    private fun logEvent(tag: String, log: String, logLevel: Logger.LogLevel) {
        if (logEnabled) {
            when (logLevel) {
                Logger.LogLevel.VERBOSE -> Log.v(tag, log)
                Logger.LogLevel.INFO -> Log.i(tag, log)
                Logger.LogLevel.DEBUG -> Log.d(tag, log)
                Logger.LogLevel.WARNING -> Log.w(tag, log)
                Logger.LogLevel.ERROR -> Log.e(tag, log)
                Logger.LogLevel.NONE -> {}
            }
        }
    }

    private fun handleEvent(tag: String, log: String, logLevel: Logger.LogLevel) {
        if (logLevel >= level) {
            logEvent(tag, log, logLevel)
            saveEvent(tag, log, logLevel)
        }
    }

    override fun v(tag: String, log: String) {
        handleEvent(tag, log, Logger.LogLevel.VERBOSE)
    }

    override fun i(tag: String, log: String) {
        handleEvent(tag, log, Logger.LogLevel.INFO)
    }

    override fun d(tag: String, log: String) {
        handleEvent(tag, log, Logger.LogLevel.DEBUG)
    }

    override fun w(tag: String, log: String) {
        handleEvent(tag, log, Logger.LogLevel.WARNING)
    }

    override fun e(tag: String, log: String) {
        handleEvent(tag, log, Logger.LogLevel.ERROR)
    }
}

interface Logger {

    var logEnabled: Boolean
    var databaseEnabled: Boolean
    var level: LogLevel
    fun v(tag: String, log: String)
    fun i(tag: String, log: String)
    fun d(tag: String, log: String)
    fun w(tag: String, log: String)
    fun e(tag: String, log: String)

    fun v(log: String) = v(TAG, log)
    fun i(log: String) = i(TAG, log)
    fun d(log: String) = d(TAG, log)
    fun w(log: String) = w(TAG, log)
    fun e(log: String) = e(TAG, log)

    enum class LogLevel {
        VERBOSE,
        INFO,
        DEBUG,
        WARNING,
        ERROR,
        NONE,
    }

    companion object {
        const val TAG = "Logger"
    }
}
