package com.telematics.features.feed.ui.trip_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.telematics.core.common.extension.color
import com.telematics.core.common.extension.drawable
import com.telematics.core.common.extension.format
import com.telematics.core.model.measures.DistanceMeasure
import com.telematics.core.model.tracking.TripDetailsData
import com.telematics.features.feed.R
import com.telematics.features.feed.databinding.FragmentDialogTripDetailsBinding
import com.telematics.features.feed.ui.feed.FeedViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripDetailDialogFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentDialogTripDetailsBinding
    private val viewModel: FeedViewModel by viewModels({ requireParentFragment() })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        isCancelable = true

        binding = FragmentDialogTripDetailsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.popupOk.setOnClickListener(this)
        bindTrip()
    }

    private fun bindTrip(tripDetailsData: TripDetailsData? = viewModel.tripDetails) =
        with(binding) {

            if (tripDetailsData == null) {
                dismiss()
                return
            }

            tripBottomSheetOverallScore.text =
                tripDetailsData.rating.toString()
            tripBottomSheetTime.text = formatTime(tripDetailsData.time)

            viewModel.formatter.getDistanceByKm(tripDetailsData.distance).apply {
                tripBottomSheetMileage.text =
                    this.format()
            }

            viewModel.formatter.getDistanceMeasureValue().apply {
                val distValue = when (this) {
                    DistanceMeasure.KM -> R.string.dashboard_new_km
                    DistanceMeasure.MI -> R.string.dashboard_new_mi
                }
                distanceMeasureText.text = getString(distValue)
            }

            tripBottomSheetOverallScore.setTextColor(
                when (tripDetailsData.rating) {
                    in 0..40 -> requireContext().resources.color(R.color.colorRedText)
                    in 41..60 -> requireContext().resources.color(R.color.colorOrangeText)
                    in 61..80 -> requireContext().resources.color(R.color.colorYellowText)
                    in 80..100 -> requireContext().resources.color(R.color.colorGreenText)
                    else -> requireContext().resources.color(R.color.colorGreenText)
                }
            )

            tripBottomSheetCityStart.text =
                tripDetailsData.addressStartParts?.city
            tripBottomSheetCityFinish.text =
                tripDetailsData.addressFinishParts?.city
            tripBottomSheetAddressStart.text =
                if (tripDetailsData.addressStartParts?.distinct.isNullOrEmpty()) tripDetailsData.addressStartParts?.city else tripDetailsData.addressStartParts?.distinct
            tripBottomSheetAddressFinish.text =
                if (tripDetailsData.addressFinishParts?.distinct.isNullOrEmpty()) tripDetailsData.addressFinishParts?.city else tripDetailsData.addressFinishParts?.distinct  // street.plus(" ").plus(tripDetailsData.addressFinishParts?.house)
            tripBottomSheetTimeStart.text = tripDetailsData.startDate
            tripBottomSheetTimeFinish.text = tripDetailsData.endDate

            setScoringValueWithColors(
                tripBottomSheetAccValue,
                tripBottomSheetAccDot,
                tripDetailsData.ratingAcceleration
            )
            setScoringValueWithColors(
                tripBottomSheetBrakingValue,
                tripBottomSheetBrakingDot,
                tripDetailsData.ratingBraking
            )
            setScoringValueWithColors(
                tripBottomSheetPhoneValue,
                tripBottomSheetPhoneDot,
                tripDetailsData.ratingPhoneUsage
            )
            setScoringValueWithColors(
                tripBottomSheetSpeedingValue,
                tripBottomSheetSpeedingDot,
                tripDetailsData.ratingSpeeding
            )
            setScoringValueWithColors(
                tripBottomSheetCorneringValue,
                tripBottomSheetCorneringDot,
                tripDetailsData.ratingCornering
            )
        }

    private fun setScoringValueWithColors(textView: TextView, imageView: ImageView, value: Int) {
        textView.text = value.toString()
        val color = when (value) {
            in 0..40 -> requireContext().resources.color(R.color.colorRedText)
            in 41..60 -> requireContext().resources.color(R.color.colorOrangeText)
            in 61..80 -> requireContext().resources.color(R.color.colorYellowText)
            in 80..100 -> requireContext().resources.color(R.color.colorGreenText)
            else -> requireContext().resources.color(R.color.colorGreenText)
        }
        val drawable = when (value) {
            in 0..40 -> requireContext().resources.drawable(R.drawable.ic_dot_red, requireContext())
            in 41..60 -> requireContext().resources.drawable(
                R.drawable.ic_dot_orange,
                requireContext()
            )

            in 61..80 -> requireContext().resources.drawable(
                R.drawable.ic_dot_yellow,
                requireContext()
            )

            in 80..100 -> requireContext().resources.drawable(
                R.drawable.ic_dot_green,
                requireContext()
            )

            else -> requireContext().resources.drawable(R.drawable.ic_dot_green, requireContext())
        }
        textView.setTextColor(color)
        imageView.setImageDrawable(drawable)
    }

    private fun formatTime(value: Int): String {
        var min = (value / 60).toLong()
        val h = min / 60
        min %= 60
        return requireContext().getString(R.string.common_time_in_hm_format, h, min)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.popupOk.id -> dismiss()
        }
    }

}