package com.telematics.features.reward.drivecoins.tab_traveling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.telematics.core.common.extension.getSerializableCompat
import com.telematics.core.model.reward.DriveCoinsDetailedData
import com.telematics.features.reward.R
import com.telematics.features.reward.databinding.FragmentTravellingBinding
import com.telematics.features.reward.drivecoins.DriveCoinsViewPagerAdapter.Companion.DRIVE_COINS_DETAILED_KEY
import com.telematics.features.reward.drivecoins.DriveCoinsViewPagerAdapter.Companion.DRIVE_COINS_IN_MILES_KEY
import kotlin.math.roundToInt

class TravellingFragment : Fragment() {

    private lateinit var binding: FragmentTravellingBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTravellingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val data = arguments?.getSerializableCompat(
            DRIVE_COINS_DETAILED_KEY,
            DriveCoinsDetailedData::class.java
        ) ?: DriveCoinsDetailedData()

        val inMiles = arguments?.getBoolean(DRIVE_COINS_IN_MILES_KEY) ?: false

        view.alpha = 0f
        view.animate().setDuration(300).alpha(1f).start()

        with(binding) {
            distanceScore.apply {
                text = data.travelingMileage.toString()
                setTextColor(getTextColor(data.travelingMileage))
            }
            durationScore.apply {
                text = data.travelingTimeDriven.toString()
                setTextColor(getTextColor(data.travelingTimeDriven))
            }

            accelerationsScore.apply {
                text = data.travelingAccelerations.toString()
                setTextColor(getTextColor(data.travelingAccelerations))
            }
            brakingsScore.apply {
                text = data.travelingBrakings.toString()
                setTextColor(getTextColor(data.travelingBrakings))
            }
            corneringsScore.apply {
                text = data.travelingCornerings.toString()
                setTextColor(getTextColor(data.travelingCornerings))
            }
            phoneUsageScore.apply {
                text = data.travelingPhoneUsage.toString()
                setTextColor(getTextColor(data.travelingPhoneUsage))
            }
            speedingScore.apply {
                text = data.travelingSpeeding.toString()
                setTextColor(getTextColor(data.travelingSpeeding))
            }

            accelerationsSum.apply {
                text = data.travelingAccelerationCount.toString()
            }
            brakingsSum.apply {
                text = data.travelingBrakingCount.toString()
            }
            corneringsSum.apply {
                text = data.travelingCorneringCount.toString()
            }
            phoneUsageSum.apply {
                text = outHoursOrMinutes(data.travelingDrivingTime)
            }
            speedingSum.apply {
                text = getDistanceValue(data.travelingTotalSpeedingKm, inMiles)
            }

            distanceSum.text =
                getDistanceValue(data.travelingMileageData, inMiles)
            durationSum.text =
                outHoursOrMinutes(data.travelingTimeDrivenData)
        }
    }

    private fun getTextColor(p: Int): Int {
        val rc = when {
            p == 0 -> R.color.colorPrimaryText
            p > 0 -> R.color.colorGreenText
            else -> R.color.colorRedText
        }
        return ContextCompat.getColor(requireContext(), rc)
    }

    private fun outHoursOrMinutes(p: Int): String {

        return if (p >= 60) {
            (p.toFloat() / 60f).roundToInt().toString() + " h"
        } else "$p m"
    }

    private fun getDistanceValue(p: Int, inMiles: Boolean): String {

        val distValue = if (inMiles) R.string.dashboard_new_mi else R.string.dashboard_new_km
        val kmStr = getString(distValue)
        return "$p $kmStr"
    }
}