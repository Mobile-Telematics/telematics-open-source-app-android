package com.telematics.core.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.telematics.core.datastore.PreferenceStorage
import com.telematics.core.model.measures.DateMeasure
import com.telematics.core.model.measures.DistanceMeasure
import com.telematics.core.model.measures.MapStyle
import com.telematics.core.model.measures.TimeMeasure
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val preferenceStorage: PreferenceStorage
) : SettingsRepository {

    override fun getTelematicsLink(context: Context): String {
        return telematicsSettingsLink(context)
    }

    private fun telematicsSettingsLink(ctx: Context): String {
        if (isMIUI(ctx)) return "http://help.telematicssdk.com/en/articles/3732818-android-9-settings-guide-miui-11-xiaomi"
        return when (android.os.Build.VERSION.SDK_INT) {
            android.os.Build.VERSION_CODES.Q -> "http://help.telematicssdk.com/en/articles/3732972-android-10-settings-guide"
            android.os.Build.VERSION_CODES.R -> "https://help.telematicssdk.com/en/articles/4885122-android-11-settings-guide"
            else -> "http://help.telematicssdk.com/en/articles/3728524-android-9-settings-guide"
        }
    }

    private fun isMIUI(ctx: Context): Boolean {
        return isIntentResolved(
            ctx,
            Intent("miui.intent.action.OP_AUTO_START").addCategory(Intent.CATEGORY_DEFAULT)
        )
                || isIntentResolved(
            ctx,
            Intent().setComponent(
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            )
        )
                || isIntentResolved(
            ctx, Intent("miui.intent.action.POWER_HIDE_MODE_APP_LIST").addCategory(
                Intent.CATEGORY_DEFAULT
            )
        )
                || isIntentResolved(
            ctx,
            Intent().setComponent(
                ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.powercenter.PowerSettings"
                )
            )
        )
    }

    private fun isIntentResolved(ctx: Context, intent: Intent?): Boolean {
        return intent != null && ctx.packageManager.resolveActivity(
            intent,
            PackageManager.MATCH_DEFAULT_ONLY
        ) != null
    }

    override fun getDateMeasure(): DateMeasure {
        val data = preferenceStorage.dateMeasure
        return DateMeasure.parse(data)
    }

    override fun getDistanceMeasure(): DistanceMeasure {

        val data = preferenceStorage.distanceMeasure
        return DistanceMeasure.parse(data)
    }

    override fun getTimeMeasure(): TimeMeasure {
        val data = preferenceStorage.timeMeasure
        return TimeMeasure.parse(data)
    }

    override fun getMapStyle(): MapStyle {
        val data = preferenceStorage.mapStyle
        return MapStyle.parse(data)
    }

    override fun setDateMeasure(dateMeasure: DateMeasure) {
        preferenceStorage.dateMeasure = dateMeasure.value
    }

    override fun setDistanceMeasure(distanceMeasure: DistanceMeasure) {
        preferenceStorage.distanceMeasure = distanceMeasure.value
    }

    override fun setTimeMeasure(timeMeasure: TimeMeasure) {
        preferenceStorage.timeMeasure = timeMeasure.value
    }

    override fun setMapStyle(mapStyle: MapStyle) {
        preferenceStorage.mapStyle = mapStyle.value
    }

    override fun isNotificationPermissionCompleted(): Boolean =
        preferenceStorage.notificationPermissions

    override fun setNotificationPermissionCompleted() {
        preferenceStorage.notificationPermissions = true
    }
}

interface SettingsRepository {
    fun getTelematicsLink(context: Context): String
    fun getDateMeasure(): DateMeasure
    fun getDistanceMeasure(): DistanceMeasure
    fun getTimeMeasure(): TimeMeasure
    fun getMapStyle(): MapStyle
    fun setDateMeasure(dateMeasure: DateMeasure)
    fun setDistanceMeasure(distanceMeasure: DistanceMeasure)
    fun setTimeMeasure(timeMeasure: TimeMeasure)
    fun setMapStyle(mapStyle: MapStyle)

    fun isNotificationPermissionCompleted(): Boolean
    fun setNotificationPermissionCompleted()
}