package com.telematics.features.feed.ui.trip_detail

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.telematics.core.common.extension.color
import com.telematics.core.common.extension.dpToPx
import com.telematics.core.common.extension.drawable
import com.telematics.core.common.extension.format
import com.telematics.core.common.extension.showMessage
import com.telematics.core.common.extension.systemBarsInsets
import com.telematics.core.common.extension.toMiles
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.core.model.measures.DistanceMeasure
import com.telematics.core.model.measures.MapStyle
import com.telematics.core.model.tracking.TripData
import com.telematics.core.model.tracking.TripDetailsData
import com.telematics.core.model.tracking.TripPointData
import com.telematics.features.feed.R
import com.telematics.features.feed.databinding.FragmentTripDetailsBinding
import com.telematics.features.feed.model.AlertType
import com.telematics.features.feed.model.ChangeDriverTypeDialog
import com.telematics.features.feed.model.SpeedType
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TripDetailFragment : BaseFragment() {

    companion object {
        const val TRIP_DETAILS_TRIP_DATA_KEY = "TRIP_DETAILS_TRIP_DATA_KEY"
        const val TRIP_DETAILS_TRIP_POS_KEY = "TRIP_DETAILS_TRIP_POS_KEY"

        private const val TAG = "TripDetailFragment"
    }

    private val tripDetailViewModel: TripDetailViewModel by viewModels()

    private var currentTripPosition = -1
    private var isNeedUpdateFeedList = false
    private var markersList = mutableListOf<Pair<Marker, TripPointData>>()
    private var currentTripDetailsData: TripDetailsData? = null
    private var needMapAnimation = false

    //UI
    private lateinit var binding: FragmentTripDetailsBinding
    private var mMap: GoogleMap? = null
    private lateinit var claimDialog: AlertDialog
    private lateinit var changeDriverTypeDialog: ChangeDriverTypeDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTripDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackPressedCallback()

        val tripPos = arguments?.getInt(TRIP_DETAILS_TRIP_POS_KEY)!!
        currentTripPosition = tripPos

        setInset()
        initViews()
        setListeners()
    }

    /** init UIs */
    private fun initViews() {

        initMap()
        initBottomSheet()
        initClaimDialog()
        initChangeDriverTypeDialog()
    }

    private fun initMap() {

        (childFragmentManager.findFragmentById(R.id.tripDetailsMap) as SupportMapFragment).apply {
            getMapAsync { googleMap ->
                mMap = googleMap
                googleMap.setMapStyle(
                    getMapStyleOptions(tripDetailViewModel.getMapStyle())
                )
                getTripDetailsByPos(currentTripPosition)
            }
        }
    }

    private fun getMapStyleOptions(style: MapStyle): MapStyleOptions? =
        if (style == MapStyle.DARK) MapStyleOptions.loadRawResourceStyle(
            requireContext(),
            R.raw.mapstyle_night
        )
        else null

    @Suppress("DEPRECATION")
    private fun initBottomSheet() {

        val tripBottomSheet = binding.tripDetailsBottomSheet.tripBottomSheet
        val tripBottomSheetSwipe = binding.tripDetailsBottomSheet.tripBottomSheetSwipe
        val tripBottomSheetButtons = binding.tripDetailsBottomSheet.tripBottomSheetButtons
        val nextArrow = binding.nextArrow
        val prevArrow = binding.prevArrow
        val tripBottomSheetTypeLayout = binding.tripDetailsBottomSheet.tripBottomSheetTypeLayout

        val bottomSheetBehavior = BottomSheetBehavior.from(tripBottomSheet)
        bottomSheetBehavior.setBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                var slideOffsetForAnimation = slideOffset
                if (slideOffsetForAnimation < 0) slideOffsetForAnimation = 0f
                tripBottomSheetSwipe.animate()
                    .alpha(1f - slideOffsetForAnimation)
                    .setDuration(0)
                    .withEndAction { }
                    .start()
            }

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        tripBottomSheetTypeLayout.isVisible = false
                        nextArrow.isVisible = true
                        prevArrow.isVisible = true
                        tripBottomSheetButtons.animate()
                            .alpha(0f)
                            .setDuration(300)
                            .withEndAction {
                                tripBottomSheetSwipe.isVisible = true
                                tripBottomSheetButtons.isVisible = true
                            }
                            .start()
                    }

                    BottomSheetBehavior.STATE_EXPANDED -> {
                        tripBottomSheetTypeLayout.isVisible = true
                        nextArrow.isVisible = false
                        prevArrow.isVisible = false
                        tripBottomSheetSwipe.isVisible = false
                        tripBottomSheetButtons.isVisible = true
                        tripBottomSheetButtons.animate()
                            .alpha(1f)
                            .setDuration(300)
                            .start()
                    }

                    else -> {

                    }
                }
            }
        })

        tripBottomSheetSwipe.setOnClickListener {
            when (bottomSheetBehavior.state) {
                BottomSheetBehavior.STATE_COLLAPSED -> bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_EXPANDED

                BottomSheetBehavior.STATE_EXPANDED -> bottomSheetBehavior.state =
                    BottomSheetBehavior.STATE_COLLAPSED

                else -> {

                }
            }
        }
    }

    private fun initClaimDialog() {
        claimDialog = AlertDialog.Builder(context)
            .setView(R.layout.claim_dialog_view)
            .setCancelable(true)
            .create()
        claimDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    private fun initChangeDriverTypeDialog() {

        changeDriverTypeDialog = ChangeDriverTypeDialog(requireContext())
    }

    private fun setListeners() {

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.nextArrow.setOnClickListener {
            next()
        }
        binding.prevArrow.setOnClickListener {
            prev()
        }
    }

    /** go to next trip details */
    private fun next() {

        currentTripPosition++
        getTripDetailsByPos(currentTripPosition)
    }

    /** go to previous trip details */
    private fun prev() {

        currentTripPosition--
        getTripDetailsByPos(currentTripPosition)
    }

    /** observe trip details */
    private fun getTripDetailsByPos(position: Int) {

        showMapFragment(false)
        showProgress(true)
        setTranslucentForBottomSheet(true)
        showPreviousArrow(position > 0)

        tripDetailViewModel.getTripDetailsByPos(position)
            .observe(viewLifecycleOwner) { result ->
                result.onSuccess { tripDetails ->
                    tripDetails?.let {
                        currentTripDetailsData = tripDetails
                        bindTrip(tripDetails)
                    } ?: run {
                        currentTripPosition = 0
                        getTripDetailsByPos(currentTripPosition)
                        showError("getTripDetailsByPos tripId == null")
                    }
                }
                result.onFailure {
                    showError(it.message ?: "getTripDetailsByPos onFailure")
                }
            }
    }

    private fun bindTrip(tripDetailsData: TripDetailsData) = with(binding.tripDetailsBottomSheet) {

        renderTrip(tripDetailsData)
        bindTripType(tripDetailsData)

        tripBottomSheetOverallScore.text =
            tripDetailsData.rating.toString()
        tripBottomSheetTime.text = formatTime(tripDetailsData.time)

        tripDetailViewModel.formatter.getDistanceByKm(tripDetailsData.distance).apply {
            tripBottomSheetMileage.text =
                this.format()
        }

        tripDetailViewModel.formatter.getDistanceMeasureValue().apply {
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

        showMapFragment(true)
        showProgress(false)
        setTranslucentForBottomSheet(false)
    }

    private fun bindTripType(tripDetailsData: TripDetailsData) =
        with(binding.tripDetailsBottomSheet) {

            val tripType = tripDetailsData.type

            tripBottomSheetTypeLayout.isEnabled = true
            tripBottomSheetTypeLayout.setOnClickListener(null)

            tripBottomSheetTypeImg.apply {
                val padding = 8.dpToPx
                updatePadding(padding, padding, padding, padding)
            }
            tripBottomSheetTypeName.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.design_black
                )
            )

            val tripTypeTextRes: Int = (tripType ?: TripData.TripType.OTHER).nameId
            val tripTypeImgRes: Int = (tripType ?: TripData.TripType.OTHER).imageId

            tripBottomSheetTypeName.setText(tripTypeTextRes)
            tripBottomSheetTypeImg.setImageResource(tripTypeImgRes)

            tripBottomSheetTypeLayout.setOnClickListener {
                changeDriverTypeDialog.showDialog(tripType) { type ->
                    changeTripTypeTo(tripDetailsData, type)
                }
            }

            tripBottomSheetDelete.setOnClickListener {
                showAnswerDialog(
                    R.string.dialog_events_delete_trip,
                    onPositive = {
                        showProgress(true)
                        tripDetailViewModel.setDeleteStatus(tripDetailsData.id!!)
                            .observe(viewLifecycleOwner) { result ->
                                result.onSuccess {
                                    finish()
                                }
                                result.onFailure {
                                    showMessage(R.string.server_error_error)
                                }
                                showProgress(false)
                            }
                    }
                )
            }

            tripBottomSheetHide.setOnClickListener {
                showAnswerDialog(
                    R.string.dialog_events_hide_trip,
                    onPositive = {
                        showProgress(true)
                        tripDetailViewModel.hideTrip(tripDetailsData.id!!)
                            .observe(viewLifecycleOwner) { result ->
                                result.onSuccess {
                                    finish()
                                }
                                result.onFailure {
                                    showMessage(R.string.server_error_error)
                                }
                                showProgress(false)
                            }
                    }
                )
            }

            fun updateState() {
                when (tripDetailsData.tag.type) {
                    TripData.TagType.NONE -> {
                        tripNoneBtn.isChecked = true
                        tripBusinessBtn.isChecked = false
                        tripPersonalBtn.isChecked = false
                    }

                    TripData.TagType.PERSONAL -> {
                        tripNoneBtn.isChecked = false
                        tripBusinessBtn.isChecked = false
                        tripPersonalBtn.isChecked = true
                    }

                    TripData.TagType.BUSINESS -> {
                        tripNoneBtn.isChecked = false
                        tripBusinessBtn.isChecked = true
                        tripPersonalBtn.isChecked = false
                    }
                }
            }

            tripNoneBtn.setOnClickListener {
                if (tripNoneBtn.isChecked && tripDetailsData.tag.type != TripData.TagType.NONE) {

                    updateState()
                }

                changeTripTagTo(tripDetailsData, TripData.TagType.NONE)
            }

            tripBusinessBtn.setOnClickListener {
                if (tripBusinessBtn.isChecked && tripDetailsData.tag.type != TripData.TagType.BUSINESS) {

                    updateState()
                }

                changeTripTagTo(tripDetailsData, TripData.TagType.BUSINESS)
            }

            tripPersonalBtn.setOnClickListener {

                if (tripPersonalBtn.isChecked && tripDetailsData.tag.type != TripData.TagType.PERSONAL) {

                    updateState()
                }

                changeTripTagTo(tripDetailsData, TripData.TagType.PERSONAL)
            }

            updateState()
        }

    private fun changeTripTagTo(tripDetailsData: TripDetailsData, toTagType: TripData.TagType) {

        if (tripDetailsData.id == null) {
            Log.d(TAG, "TripDataFragment:changeTripTypeTo: tripDetailsData.id == null")
            bindTripType(tripDetailsData)
            return
        }

        val tripId = tripDetailsData.id!!

        showProgress(true)
        tripDetailViewModel.changeTripTagTo(tripId, toTagType)
            .observe(viewLifecycleOwner) { result ->
                result.onSuccess {
                    isNeedUpdateFeedList = true
                    tripDetailsData.tag.type = toTagType
                    bindTripType(tripDetailsData)
                }
                result.onFailure {
                    bindTripType(tripDetailsData)
                }
                showProgress(false)
            }
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

    /** render trip on map */
    private fun renderTrip(tripDetailsData: TripDetailsData) {

        mMap?.clear()
        mMap?.uiSettings?.apply {
            isMapToolbarEnabled = false
            isZoomControlsEnabled = false
        }

        tripDetailsData.points?.also { tripPoints ->
            if (tripPoints.isNotEmpty()) {
                centerMapByRoute(tripPoints)
                renderRoute(tripPoints)
                renderPhoneUsage(tripPoints)
                renderMarkers(tripPoints)
            }
        }

        showProgress(false)
    }

    private fun renderPhoneUsage(tripPoints: List<TripPointData>) {

        var segmentPolyline = PolylineOptions()
        for (i in tripPoints.indices) {
            val currentPoint = tripPoints[i]
            if (currentPoint.usePhone) {
                segmentPolyline.add(LatLng(currentPoint.latitude, currentPoint.longitude))
                segmentPolyline.color(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.colorPhoneUsage
                    )
                )
                segmentPolyline.width(18f)
                segmentPolyline.jointType(JointType.ROUND)
                segmentPolyline.startCap(RoundCap())
                segmentPolyline.endCap(RoundCap())
                segmentPolyline.zIndex(1f)
            } else {
                mMap?.addPolyline(segmentPolyline)
                segmentPolyline = PolylineOptions()
            }
        }

        mMap?.addPolyline(segmentPolyline)
    }

    private fun renderRoute(tripPoints: List<TripPointData>) {

        var currentSpeedType = tripPoints.first().speedType
        var segmentPolyline = PolylineOptions()
        for (i in tripPoints.indices) {
            val currentPoint = tripPoints[i]
            if (currentSpeedType.equals(currentPoint.speedType, true)) {
                segmentPolyline.add(LatLng(currentPoint.latitude, currentPoint.longitude))
                segmentPolyline.color(
                    ContextCompat.getColor(
                        requireContext(),
                        SpeedType.getColor(currentPoint.speedType)
                    )
                )
                segmentPolyline.width(8f)
                segmentPolyline.jointType(JointType.ROUND)
                segmentPolyline.startCap(RoundCap())
                segmentPolyline.endCap(RoundCap())
                segmentPolyline.zIndex(2f)
            } else {
                mMap?.addPolyline(segmentPolyline)
                segmentPolyline = PolylineOptions()
                segmentPolyline.add(LatLng(tripPoints[i - 1].latitude, tripPoints[i - 1].longitude))
                segmentPolyline.add(LatLng(currentPoint.latitude, currentPoint.longitude))
            }
            currentSpeedType = currentPoint.speedType
        }

        mMap?.addPolyline(segmentPolyline)
    }

    private fun renderMarkers(tripPoints: List<TripPointData>) {

        fun createInfoView(marker: Marker): View {

            val v: View =
                LayoutInflater.from(requireContext()).inflate(R.layout.info_bubble_layout, null)
            //val latLng: LatLng = marker.position

            val event = markersList.find { it.first == marker }?.second!!
            val alertType = AlertType.from(event.alertType)

            val typeText = resources.getString(alertType.getStringRes())

            v.findViewById<View>(R.id.info_not_event_button).setOnClickListener {
                showEventDialog(event, alertType)
            }
            if (event.edited) v.findViewById<View>(R.id.info_not_event_button).visibility =
                View.GONE
            v.findViewById<ImageView>(R.id.info_image).setImageResource(alertType.drawableResId)
            v.findViewById<TextView>(R.id.info_type_text).text = typeText
            v.findViewById<TextView>(R.id.info_date_text).text = event.date
            v.findViewById<TextView>(R.id.info_max_force).text =
                getString(R.string.trip_details_max_force, event.alertValue.format())
            when (tripDetailViewModel.formatter.getDistanceMeasureValue()) {
                DistanceMeasure.KM -> {
                    v.findViewById<TextView>(R.id.info_speed_text).text =
                        getString(R.string.trip_details_km_h, event.speed.format())
                }

                DistanceMeasure.MI -> {
                    v.findViewById<TextView>(R.id.info_speed_text).text =
                        getString(R.string.trip_details_mi_h, event.speed.toMiles().format())
                }
            }

            return v
        }

        val infoViewAdapter = object : GoogleMap.InfoWindowAdapter {
            override fun getInfoContents(p0: Marker): View? {
                return null
            }

            override fun getInfoWindow(p0: Marker): View {
                return createInfoView(p0)
            }
        }

        mMap?.setInfoWindowAdapter(infoViewAdapter)

        markersList = mutableListOf()

        val startPoint = LatLng(tripPoints[0].latitude, tripPoints[0].longitude)
        val endPoint = LatLng(tripPoints.last().latitude, tripPoints.last().longitude)

        val startMarker = MarkerOptions()
            .position(startPoint)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_a_trip))
            .anchor(0.5f, 0.5f)

        val endMarker = MarkerOptions()
            .position(endPoint)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_dot_b_trip))
            .anchor(0.5f, 0.5f)

        mMap?.addMarker(startMarker)
        mMap?.addMarker(endMarker)

        for (i in tripPoints.indices) {
            val currentPoint = tripPoints[i]
            if (AlertType.from(currentPoint.alertType) != AlertType.UNKNOWN) {
                val image = BitmapDescriptorFactory
                    .fromBitmap(
                        AppCompatResources.getDrawable(
                            requireContext(),
                            AlertType.getDrawableRes(currentPoint.alertType)
                        )!!.toBitmap()
                    )
                val eventMarker = MarkerOptions()
                    .position(LatLng(currentPoint.latitude, currentPoint.longitude))
                    .title("")
                    .snippet("")
                    .icon(image)
                    .anchor(0.5f, 0.5f)
                    .draggable(false)
                mMap?.addMarker(eventMarker).apply {
                    this ?: return@apply
                    markersList.add(Pair(this, currentPoint))
                }
            }
        }

        mMap?.setOnMarkerClickListener { marker ->
            markersList.find { it.first == marker }.apply {
                if (this != null) {
                    marker.showInfoWindow()
                }
            }
            true
        }

        mMap?.setOnInfoWindowClickListener { marker ->
            markersList.find { it.first == marker }.apply {
                if (this != null) {
                    showEventDialog(this.second, AlertType.from(this.second.alertType))
                }
            }
        }
    }

    private fun centerMapByRoute(tripPoints: List<TripPointData>) {

        val b = LatLngBounds.Builder()
        tripPoints.forEach { point ->
            b.include(LatLng(point.latitude, point.longitude))
        }
        val cu = CameraUpdateFactory.newLatLngBounds(b.build(), 200)
        mMap?.setPadding(55, 100, 55, 270)
        if (needMapAnimation)
            mMap?.animateCamera(cu)
        else
            mMap?.moveCamera(cu)

        needMapAnimation = true
    }

    /** additional UIs*/
    private fun showConfirmDialog(callback: () -> Unit) {
        showAnswerDialog(
            R.string.are_you_sure,
            onPositive = callback
        )
    }

    private fun showEventDialog(event: TripPointData, alertType: AlertType) {
        claimDialog.show()
        claimDialog.findViewById<ImageView>(R.id.noEventImage)
            .setImageResource(alertType.wrongDialogResId)
        claimDialog.findViewById<TextView>(R.id.noEventText).text =
            resources.getString(alertType.getStringRes())

        val remainingItems =
            AlertType.entries.filter { it != AlertType.UNKNOWN && it != alertType }

        claimDialog.findViewById<ImageView>(R.id.firstImage)
            .setImageResource(remainingItems[0].editedDialogResId)
        claimDialog.findViewById<TextView>(R.id.firstText).text =
            resources.getString(remainingItems[0].getStringRes())

        claimDialog.findViewById<ImageView>(R.id.secondImage)
            .setImageResource(remainingItems[1].editedDialogResId)
        claimDialog.findViewById<TextView>(R.id.secondText).text =
            resources.getString(remainingItems[1].getStringRes())

        claimDialog.findViewById<ImageView>(R.id.thirdImage)
            .setImageResource(remainingItems[2].editedDialogResId)
        claimDialog.findViewById<TextView>(R.id.thirdText).text =
            resources.getString(remainingItems[2].getStringRes())

        claimDialog.findViewById<View>(R.id.noEventLayout).setOnClickListener(null)
        claimDialog.findViewById<View>(R.id.noEventLayout).setOnClickListener {
            showConfirmDialog {
                claimDialog.dismiss()
            }
        }

        claimDialog.findViewById<View>(R.id.firstLayout).setOnClickListener(null)
        claimDialog.findViewById<View>(R.id.firstLayout).setOnClickListener {
            showConfirmDialog {
                changeTripEvent(event, remainingItems[0])
            }

        }

        claimDialog.findViewById<View>(R.id.secondLayout).setOnClickListener(null)
        claimDialog.findViewById<View>(R.id.secondLayout).setOnClickListener {
            showConfirmDialog {
                changeTripEvent(event, remainingItems[1])
            }
        }

        claimDialog.findViewById<View>(R.id.thirdLayout).setOnClickListener(null)
        claimDialog.findViewById<View>(R.id.thirdLayout).setOnClickListener {
            showConfirmDialog {
                changeTripEvent(event, remainingItems[2])
            }
        }

        claimDialog.findViewById<View>(R.id.cancelButton).setOnClickListener {
            claimDialog.dismiss()
        }
    }

    private fun showError(msg: String) {

        Log.d(TAG, "showError: $msg")
        showMessage(msg)
    }

    private fun showProgress(show: Boolean) {

        binding.tripDetailsLoadingView.setOnClickListener { }
        binding.tripDetailsLoadingView.isVisible = show
    }

    private fun showPreviousArrow(show: Boolean) {

        binding.prevArrow.isVisible = show
    }

    private fun showMapFragment(show: Boolean) {

        if (show) {
            binding.tripDetails.tripDetailsMapParent.isVisible = true
            binding.tripDetails.tripDetailsMapParent.alpha = 0f
            binding.tripDetails.tripDetailsMapParent.animate()
                .setDuration(300)
                .setStartDelay(100)
                .alpha(1f)
                .start()
        } else {
            binding.tripDetails.tripDetailsMapParent.isVisible = false
        }
    }

    private fun setTranslucentForBottomSheet(show: Boolean) {
        binding.tripDetailsBottomSheet.tripBottomSheet.alpha = if (show) 0.5f else 1f
    }

    @Suppress("DEPRECATION")
    private fun showSuccessToast(success: Boolean) {

        val s =
            if (success) resources.getString(R.string.trip_details_thanks) else resources.getString(
                R.string.trip_details_error
            )
        val t = Toast.makeText(context, s, Toast.LENGTH_SHORT)
        t.setGravity(Gravity.CENTER, 0, 0)
        val view = LayoutInflater.from(context).inflate(R.layout.wrong_event_toast, null)
        val toastText = view.findViewById<TextView>(R.id.toast_text)
        val toastImage = view.findViewById<ImageView>(R.id.toast_image)
        toastText.text = s
        if (success) toastImage.setImageResource(R.drawable.ic_toast_success)
        else toastImage.setImageResource(R.drawable.ic_toast_error)
        t.view = view
        t.show()
    }

    /** handle trip */
    private fun changeTripTypeTo(tripDetailsData: TripDetailsData, toType: TripData.TripType) {

        if (tripDetailsData.id == null) {
            Log.d(TAG, "TripDataFragment:changeTripTypeTo: tripDetailsData.id == null")
            return
        }

        val tripId = tripDetailsData.id!!

        showProgress(true)
        tripDetailViewModel.changeTripTypeTo(tripId, toType)
            .observe(viewLifecycleOwner) { result ->
                result.onSuccess {
                    if (it) {
                        isNeedUpdateFeedList = true
                        tripDetailsData.type = toType
                    }
                    bindTripType(tripDetailsData)
                }
                result.onFailure {
                    showMessage(R.string.server_error_error)
                    bindTripType(tripDetailsData)
                }
                showProgress(false)
            }
    }

    private fun changeTripEvent(event: TripPointData, alertType: AlertType) {

        showProgress(true)
        claimDialog.dismiss()
        tripDetailViewModel.changeTripEvent(event, alertType)
            .observe(viewLifecycleOwner) { result ->
                result.onSuccess {
                    event.edited = true
                    renderTrip(currentTripDetailsData!!)
                    removeOverlay()
                    showSuccessToast(true)
                }
                result.onFailure {
                    showSuccessToast(false)
                }
                showProgress(false)
            }
    }

    private fun removeOverlay() {
        markersList.forEach {
            it.first.hideInfoWindow()
        }
    }

    private fun formatTime(value: Int): String {
        var min = (value / 60).toLong()
        val h = min / 60
        min %= 60
        return requireContext().getString(R.string.common_time_in_hm_format, h, min)
    }

    private fun finish() {
        onBackPressed()
    }

    private fun setInset() {
        binding.toolbar.setOnApplyWindowInsetsListener { view, insets ->
            val systemBarsInsets = systemBarsInsets(insets)
            view.updatePadding(
                top = systemBarsInsets.top
            )

            insets
        }
    }
}