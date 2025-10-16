package com.telematics.features.dashboard.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import com.telematics.core.common.extension.dpToPx
import com.telematics.core.common.extension.format
import com.telematics.core.data.formatter.MeasuresFormatter
import com.telematics.core.model.Trip
import com.telematics.core.model.measures.DistanceMeasure
import com.telematics.features.dashboard.R
import com.telematics.features.dashboard.databinding.LayoutScoredTripBinding


class ScoredTripLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding =
        LayoutScoredTripBinding.inflate(LayoutInflater.from(context), this)

    init {
        init()
    }

    private fun init() {

        background = AppCompatResources.getDrawable(context, R.drawable.bg_card)

        updatePadding(
            16.dpToPx,
            16.dpToPx,
            16.dpToPx,
            16.dpToPx,
        )
    }

    @SuppressLint("SetTextI18n")
    fun setTripData(trip: Trip, formatter: MeasuresFormatter) {
        with(binding) {

            formatter.getDistanceByKm(trip.dist.toDouble()).apply {
                distanceValue.text = this.format()
            }

            formatter.getDistanceMeasureValue().apply {
                val distValue = when (formatter.getDistanceMeasureValue()) {
                    DistanceMeasure.KM -> R.string.dashboard_new_km
                    DistanceMeasure.MI -> R.string.dashboard_new_mi
                }
                distanceName.text = context.getString(distValue)
            }
            val startDate = formatter.parseFullNewDate(trip.timeStart)
            val endDate = formatter.parseFullNewDate(trip.timeEnd)
            startDateValue.text = formatter.getDateWithTime(startDate)
            endDateValue.text = formatter.getDateWithTime(endDate)
            startCity.text =
                "${trip.cityStart}, ${trip.streetStart}" // "125, 5th Really long name Avenue, Pittsburgh, PA"
            endCity.text =
                "${trip.cityEnd}, ${trip.streetEnd}"  // "47 Cherry Hill Highway, New York, NY"

            val tripType = trip.type

            tripTypeName.text = context.getString(tripType.nameId)
            tripTypeImg.setImageResource(tripType.imageId)
        }
    }
}
