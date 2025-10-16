package com.telematics.core.data.datasource.local

import android.content.Context
import com.google.gson.Gson
import com.telematics.core.common.di.IoDispatcher
import com.telematics.core.datastore.PreferenceStorage
import com.telematics.core.model.intercom.IntercomUserModel
import com.telematics.core.model.measures.DateMeasure
import com.telematics.core.model.measures.DistanceMeasure
import com.telematics.core.model.measures.MapStyle
import com.telematics.core.model.measures.TimeMeasure
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDataLocalDataSourceImpl @Inject constructor(
    private val preferenceStorage: PreferenceStorage,
    @param:ApplicationContext private val context: Context,
    @param:IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : UserDataLocalDataSource {

    private val _cashedSimpleMode = MutableStateFlow<Boolean>(false)

    init {
        CoroutineScope(ioDispatcher).launch {
            _cashedSimpleMode.value = simpleMode
        }
    }

    override var intercomUserModel: IntercomUserModel
        get() {
            val data = preferenceStorage.intercomUser
            return try {
                Gson().fromJson(data, IntercomUserModel::class.java)
                    ?: IntercomUserModel.getEmptyModel()
            } catch (e: Exception) {
                IntercomUserModel.getEmptyModel()
            }
        }
        set(value) {
            val convertedModel = Gson().toJson(value)
            preferenceStorage.intercomUser = convertedModel
        }

    override var mapState
        get() = preferenceStorage.mapState
        set(value) {
            preferenceStorage.mapState = value
        }

    override var sharedPrefsMigrated
        get() = preferenceStorage.sharedPrefsMigrated
        set(value) {
            preferenceStorage.sharedPrefsMigrated = value
        }

    override var simpleMode
        get() = preferenceStorage.simpleMode
        set(value) {
            preferenceStorage.simpleMode = value
            _cashedSimpleMode.update { value }
        }

    override fun getSimpleModeFlow(): Flow<Boolean> = _cashedSimpleMode.asStateFlow()

    override suspend fun clearSimpleMode() {
        _cashedSimpleMode.update { false }
    }

    override fun migrateUserData() {
        val distanceKey = "DISTANCE"
        val dateKey = "DATE"
        val timeKey = "TIME"
        val mapStyleKey = "MAP_STYLE"

        fun getSharedPreferences(prefName: String = "MEASURES") =
            context.getSharedPreferences(prefName, Context.MODE_PRIVATE)

        fun getDistanceMeasure(): DistanceMeasure {
            val value = getSharedPreferences().getString(distanceKey, null)
            return DistanceMeasure.parse(value)
        }

        fun getDateMeasure(): DateMeasure {
            val value = getSharedPreferences().getString(dateKey, null)
            return DateMeasure.parse(value)
        }

        fun getTimeMeasure(): TimeMeasure {
            val value = getSharedPreferences().getString(timeKey, null)
            return TimeMeasure.parse(value)
        }

        fun getMapStyle(): MapStyle {
            val value = getSharedPreferences().getString(mapStyleKey, null)
            return MapStyle.parse(value)
        }

        with(preferenceStorage) {
            distanceMeasure = getDistanceMeasure().value
            timeMeasure = getTimeMeasure().value
            dateMeasure = getDateMeasure().value
            mapStyle = getMapStyle().value
        }

    }
}

interface UserDataLocalDataSource {
    var intercomUserModel: IntercomUserModel

    var mapState: Boolean

    var sharedPrefsMigrated: Boolean

    fun migrateUserData()

    var simpleMode: Boolean
    fun getSimpleModeFlow(): Flow<Boolean>
    suspend fun clearSimpleMode()

}