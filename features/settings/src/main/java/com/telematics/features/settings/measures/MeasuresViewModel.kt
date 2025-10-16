package com.telematics.features.settings.measures

import androidx.lifecycle.ViewModel
import com.telematics.core.data.repository.SettingsRepository
import com.telematics.core.model.measures.DateMeasure
import com.telematics.core.model.measures.DistanceMeasure
import com.telematics.core.model.measures.MapStyle
import com.telematics.core.model.measures.TimeMeasure
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MeasuresViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    fun getDateMeasure(): DateMeasure {
        return settingsRepository.getDateMeasure()
    }

    fun getDistanceMeasure(): DistanceMeasure {
        return settingsRepository.getDistanceMeasure()
    }

    fun getTimeMeasure(): TimeMeasure {
        return settingsRepository.getTimeMeasure()
    }

    fun getMapStyle(): MapStyle {
        return settingsRepository.getMapStyle()
    }

    fun setDateMeasure(dateMeasure: DateMeasure) {
        settingsRepository.setDateMeasure(dateMeasure)
    }

    fun setDistanceMeasure(distanceMeasure: DistanceMeasure) {
        settingsRepository.setDistanceMeasure(distanceMeasure)
    }

    fun setTimeMeasure(timeMeasure: TimeMeasure) {
        settingsRepository.setTimeMeasure(timeMeasure)
    }

    fun setMapStyle(value: MapStyle) {
        settingsRepository.setMapStyle(value)
    }
}