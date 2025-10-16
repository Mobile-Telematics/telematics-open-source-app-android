package com.telematics.core.data.repository

import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.telematicssdk.tracking.Settings
import com.telematicssdk.tracking.TrackingApi
import com.telematicssdk.tracking.server.model.Locale
import com.telematicssdk.tracking.server.model.sdk.Track
import com.telematicssdk.tracking.server.model.sdk.TrackTag
import com.telematicssdk.tracking.services.main.elm.BluetoothUtils
import com.telematicssdk.tracking.services.main.elm.Constants
import com.telematicssdk.tracking.utils.permissions.PermissionsWizardActivity
import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.data.BuildConfig
import com.telematics.core.data.datasource.local.TrackingLocalDataSource
import com.telematics.core.data.datasource.local.UserAuthLocalDataSource
import com.telematics.core.data.extension.awaitLinkingListener
import com.telematics.core.data.mappers.TripsMapper
import com.telematics.core.model.TripRecordMode
import com.telematics.core.model.tracking.ChangeTripEvent
import com.telematics.core.model.tracking.ElmDevice
import com.telematics.core.model.tracking.ElmManagerLinkingResult
import com.telematics.core.model.tracking.TripData
import com.telematics.core.model.tracking.TripDetailsData
import com.telematics.core.network.api.TripEventTypeApi
import com.telematics.core.network.model.tracking.ChangeEventBody
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

@SuppressLint("MissingPermission")
class TrackingRepositoryImpl @Inject constructor(
    private val userAuthLocalDataSource: UserAuthLocalDataSource,
    private val trackingLocalDataSource: TrackingLocalDataSource,
    private val tripsMapper: TripsMapper,
    private val tripEventTypeApi: TripEventTypeApi,
    private val trackingApi: TrackingApi,
    @param:ApplicationContext private val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : TrackingRepository {

    companion object {
        private const val TAG = "TrackingRepository"
    }

    private val locale = Locale.EN

    override fun initializeSdk() {
        val setting = Settings(
            Settings.stopTrackingTimeHigh, 150,
            autoStartOn = true,
            elmOn = false
        )
        trackingApi.initialize(context, setting)
        if (trackingApi.isInitialized()) {
            updateFirebaseCrashlytics()
        }
    }

    private fun updateFirebaseCrashlytics() {
        try {
            FirebaseCrashlytics.getInstance().setUserId(trackingApi.getDeviceId())
        } catch (_: Exception) {

        }
    }

    override fun setDeviceToken(deviceToken: String) {

        if (deviceToken.isNotBlank() && trackingApi.getDeviceId() != deviceToken) {
            Log.d(TAG, "setDeviceToken: deviceId $deviceToken")
            trackingApi.setDeviceID(deviceToken)
            updateFirebaseCrashlytics()
        }
    }

    override fun checkPermissions(): Flow<Boolean> {
        return flow {
            val data = trackingApi.areAllRequiredPermissionsGranted()
            emit(data)
        }
    }

    override fun checkPermissionAndStartWizard(activity: Activity) {
        if (!trackingApi.areAllRequiredPermissionsAndSensorsGranted()) {
            activity.startActivityForResult(
                PermissionsWizardActivity.getStartWizardIntent(
                    activity,
                    enableAggressivePermissionsWizard = true,
                    enableAggressivePermissionsWizardPage = true
                ), PermissionsWizardActivity.WIZARD_PERMISSIONS_CODE
            )
        }
    }

    override fun enableTrackingSDK() {
        trackingApi.setEnableSdk(true)
    }

    override fun disableTrackingSDK() {
        trackingApi.setEnableSdk(false)
    }

    override fun startTracking() {
        trackingApi.startTracking()
    }

    override fun stopTracking() {
        trackingApi.stopTracking()
    }

    override fun setIntentForNotification(intent: Intent) {
        trackingApi.setIntentForNotification(intent)
    }

    override fun logout() {
        trackingApi.logout()
        updateFirebaseCrashlytics()
    }

    private suspend fun getTracks(
        locale: Locale,
        startDate: String?,
        endDate: String?,
        offset: Int,
        limit: Int
    ): Array<Track> =
        withContext(ioDispatcher) {
            trackingApi.getTracks(
                locale,
                startDate,
                endDate,
                offset,
                limit
            )
        }

    override suspend fun getLastTrip(): TripData? =
        withContext(ioDispatcher) {
            try {

                val arrayOfTracks = getTracks(
                    locale,
                    null,
                    null,
                    0,
                    20
                )
                val listTripData =
                    tripsMapper.transformTripsList(arrayOfTracks.asList()).filter { !it.isDeleted }
                tripsMapper.sort(listTripData, null)
                listTripData.firstOrNull()
            } catch (e: Exception) {
                null
            }
        }

    override fun getTripImage(token: String): Flow<Bitmap?> {

        return flow {
            emit(null)
        }
    }

    private var tripData: TripData? = null
    private fun getTripsFromSdk(offset: Int, limit: Int): List<TripData> {

        if (offset == 0) tripData = null
        val arrayOfTracks = trackingApi.getTracks(
            locale,
            null,
            null,
            offset,
            limit
        )
        val listTripData =
            tripsMapper.transformTripsList(arrayOfTracks.asList()).filter { !it.isDeleted }
        tripsMapper.sort(listTripData, tripData)
        if (listTripData.isNotEmpty()) {
            tripData = listTripData[0]
        }
        return listTripData
    }

    private fun getTripDetailsFromSdk(tripId: String): TripDetailsData? {

        val trackDetails = trackingApi.getTrackDetails(tripId, locale)
        return tripsMapper.transformTripDetails(trackDetails)
    }

    override fun getTrips(offset: Int, limit: Int): Flow<List<TripData>> {

        return flow {
            val data = getTripsFromSdk(offset, limit)
            emit(data)
        }
    }

    override fun getTripDetailsByPos(position: Int): Flow<TripDetailsData?> {
        return flow {
            val tripData = getTripsFromSdk(position, 1).firstOrNull()
            tripData?.id?.let { tripId ->
                val data = getTripDetailsFromSdk(tripId)
                emit(data)
            } ?: run {
                emit(null)
            }
        }
    }

    override fun changeTripType(tripId: String, toTripType: TripData.TripType): Flow<Boolean> {

        return flow {
            val data = trackingApi.changeTrackOrigin(tripId, toTripType.toString())
            emit(data)
        }
    }

    override fun changeTripEvent(tripId: String, changeTripEvent: ChangeTripEvent): Flow<Boolean> {

        return flow {
            val body = ChangeEventBody(
                changeTripEvent.eventType,
                changeTripEvent.latitude,
                changeTripEvent.longitude,
                changeTripEvent.pointDate,
                changeTripEvent.changeTypeTo
            )
            val deviceToken = trackingApi.getDeviceId()
            tripEventTypeApi.changeEvent(deviceToken, tripId, body)
            emit(true)
        }
    }

    override fun hideTrip(tripId: String): Flow<Unit> {

        return flow {
            val tag = TrackTag(tag = "DEL", source = BuildConfig.SOURCE)
            trackingApi.addTrackTags(tripId, arrayOf(tag))
            emit(Unit)
        }
    }

    override fun setDeleteStatus(tripId: String): Flow<Unit> {

        return flow {
            val deviceToken = trackingApi.getDeviceId()
            tripEventTypeApi.setDeleted(tripId, deviceToken)
            emit(Unit)
        }
    }

    override fun getLastSession(): Flow<Long> {

        return flow {
            val data = trackingApi.getElmManager()?.getLastSession()
            emit(data?.second ?: 0)
        }
    }

    override fun getBluetoothAdapter(context: Context): BluetoothAdapter? {

        return BluetoothUtils.getBluetoothAdapter(context)
    }

    override fun getRequestBluetoothEnableCode() = Constants.REQUEST_BLUETOOTH_ENABLE_CODE

    override fun getElmManagerLinkingResult(): Flow<ElmManagerLinkingResult?> {

        return flow {
            val data = trackingApi.getElmManager()?.awaitLinkingListener()
            emit(data)
        }
    }

    override fun getElmDevice(): Flow<Unit> {

        return flow {
            trackingApi.getElmManager()?.getElmDevices()
            emit(Unit)
        }
    }

    override fun connectSelectedDevice(device: ElmDevice, token: String): Flow<Unit> {

        return flow {
            trackingApi.getElmManager()
                ?.connectAndRegisterDevice(device.deviceMacAddress ?: "", token)
            emit(Unit)
        }
    }

    override fun removeFutureTrackTag(tag: String): Flow<Unit> {

        return flow {
            trackingApi.removeFutureTrackTag(tag)
            emit(Unit)
        }
    }

    override fun addFutureTrackTag(tag: String): Flow<Unit> {

        return flow {
            trackingApi.addFutureTrackTag(tag)
            emit(Unit)
        }
    }

    private fun deleteTripTags(tripId: String) {
        val tags = arrayOf(
            TrackTag(TripData.TagType.PERSONAL.toString(), source = BuildConfig.SOURCE),
            TrackTag(TripData.TagType.BUSINESS.toString(), source = BuildConfig.SOURCE)
        )

        trackingApi.removeTrackTags(tags = tags, trackId = tripId)
    }

    override fun changeTripTag(tripId: String, type: TripData.TagType): Flow<Unit> {
        return flow {
            deleteTripTags(tripId)
            if (type != TripData.TagType.NONE) {
                val tags = arrayOf(TrackTag(tag = type.toString(), source = BuildConfig.SOURCE))
                trackingApi.addTrackTags(tripId, tags)
            }
            emit(Unit)
        }
    }

    override suspend fun setTripRecordMode(mode: TripRecordMode, isActive: Boolean): Result<Unit> =
        withContext(ioDispatcher) {
            try {
                with(trackingApi) {
                    when (mode) {
                        TripRecordMode.ALWAYS_ON -> {
                            if (trackingLocalDataSource.tripRecordMode != mode) {
                                if (!isSdkEnabled()) {
                                    if (!isDeviceIdValid()) {
                                        setDeviceToken(userAuthLocalDataSource.deviceToken)
                                    }
                                    setEnableSdk(true)
                                }
                            }
                        }

                        TripRecordMode.SHIFT_MODE -> {
                            if (trackingLocalDataSource.tripRecordMode == mode) {
                                if (isActive) {
                                    setEnableSdk(true)
                                } else {
                                    stopTracking()
                                    setEnableSdk(false)
                                }
                            } else {
                                if (isSdkEnabled()) {
                                    stopTracking()
                                    setEnableSdk(false)
                                } else {
                                    if (!isDeviceIdValid()) {
                                        setDeviceToken(userAuthLocalDataSource.deviceToken)
                                    }
                                }
                            }
                        }

                        TripRecordMode.ON_DEMAND -> {
                            if (trackingLocalDataSource.tripRecordMode == mode) {
                                if (isActive) {
                                    setEnableSdk(true)
                                    startPersistentTracking()
                                } else {
                                    stopTracking()
                                    setEnableSdk(false)
                                }
                            } else {
                                if (isSdkEnabled()) {
                                    stopTracking()
                                    setEnableSdk(false)
                                } else {
                                    if (!isDeviceIdValid()) {
                                        setDeviceToken(userAuthLocalDataSource.deviceToken)
                                    }
                                }
                            }
                        }

                        TripRecordMode.DISABLED -> {
                            if (trackingLocalDataSource.tripRecordMode != mode) {
                                stopTracking()
                                setEnableSdk(false)
                            }
                        }
                    }
                    return@with
                }
                trackingLocalDataSource.tripRecordMode = mode
                trackingLocalDataSource.isTripRecordModeActive = isActive

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    override suspend fun getTripRecordMode(): Result<Pair<TripRecordMode, Boolean>> =
        withContext(ioDispatcher) {
            try {
                Result.success(
                    Pair(
                        trackingLocalDataSource.tripRecordMode,
                        trackingLocalDataSource.isTripRecordModeActive
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

interface TrackingRepository {
    /** handle sdk*/
    fun initializeSdk()
    fun setDeviceToken(deviceToken: String)

    fun checkPermissions(): Flow<Boolean>
    fun checkPermissionAndStartWizard(activity: Activity)

    fun enableTrackingSDK()
    fun disableTrackingSDK()

    fun startTracking()
    fun stopTracking()

    fun setIntentForNotification(intent: Intent)

    fun logout()

    /** handle tracks */
    suspend fun getLastTrip(): TripData?
    fun getTripImage(token: String): Flow<Bitmap?>
    fun getTrips(offset: Int, limit: Int): Flow<List<TripData>>
    fun getTripDetailsByPos(position: Int): Flow<TripDetailsData?>
    fun changeTripType(tripId: String, toTripType: TripData.TripType): Flow<Boolean>
    fun changeTripEvent(tripId: String, changeTripEvent: ChangeTripEvent): Flow<Boolean>
    fun hideTrip(tripId: String): Flow<Unit>
    fun setDeleteStatus(tripId: String): Flow<Unit>

    /** handle elm */
    fun getLastSession(): Flow<Long>
    fun getBluetoothAdapter(context: Context): BluetoothAdapter?
    fun getRequestBluetoothEnableCode(): Int
    fun getElmManagerLinkingResult(): Flow<ElmManagerLinkingResult?>
    fun getElmDevice(): Flow<Unit>
    fun connectSelectedDevice(device: ElmDevice, token: String): Flow<Unit>

    /** tags */
    fun removeFutureTrackTag(tag: String): Flow<Unit>
    fun addFutureTrackTag(tag: String): Flow<Unit>
    fun changeTripTag(tripId: String, type: TripData.TagType): Flow<Unit>

    suspend fun getTripRecordMode(): Result<Pair<TripRecordMode, Boolean>>
    suspend fun setTripRecordMode(mode: TripRecordMode, isActive: Boolean): Result<Unit>
}