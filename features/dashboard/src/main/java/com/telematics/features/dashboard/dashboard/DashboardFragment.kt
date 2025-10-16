package com.telematics.features.dashboard.dashboard

import android.annotation.SuppressLint
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.telematics.core.common.dialog.SuccessDialogFragment
import com.telematics.core.common.extension.dpToPx
import com.telematics.core.common.extension.showMessage
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.core.common.fragment.muxplayer.MuxVideoPlayerFragment
import com.telematics.core.common.navigation.AppNavigation
import com.telematics.core.common.navigation.AppNavigationViewModel
import com.telematics.core.data.formatter.MeasuresFormatter
import com.telematics.core.model.Trip
import com.telematics.core.model.TripRecordMode
import com.telematics.core.model.statistics.DailyScore
import com.telematics.core.model.statistics.EcoScore
import com.telematics.core.model.statistics.Score
import com.telematics.core.model.statistics.ScoreType
import com.telematics.core.model.video.VideoData
import com.telematics.features.dashboard.BuildConfig
import com.telematics.features.dashboard.R
import com.telematics.features.dashboard.databinding.FragmentDashboardBinding
import com.telematics.features.dashboard.dialog.ReAuthDialogFragment
import com.telematics.features.dashboard.dialog.ScoreExplanationDialogFragment
import com.telematics.features.dashboard.dialog.TripRecordModeDialogFragment
import com.telematics.features.dashboard.dialog.UpdateDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class DashboardFragment : BaseFragment(), View.OnClickListener {

    companion object {

        private const val NEED_ATTENTION_REQUEST_KEY = "need_attention_request_key"
        private const val DIALOG_RESULT_KEY = "dialog_result_key"
        private const val CHOOSER_REQUEST_KEY = "trip_record_mode_request_key"
        private const val RE_AUTH_REQUEST_KEY = "re_auth_request_key"
        private const val UPDATE_REQUEST_KEY = "update_request_key"
        private const val SUCCESS_REQUEST_KEY = "success_request_key"
    }

    private val distanceLimit = BuildConfig.DASHBOARD_DISTANCE_LIMIT

    @Inject
    internal lateinit var measuresFormatter: MeasuresFormatter

    private val appNavigationViewModel: AppNavigationViewModel by activityViewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()

    private lateinit var binding: FragmentDashboardBinding

    private var isDialogShowed = false

    private val scoringAdapter: DashboardTypePagerAdapter by lazy {
        DashboardTypePagerAdapter(mutableListOf())
    }

    private var prevPageSelected: Int = -1
    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            val item = scoringAdapter.getItem(position)

            setStatisticsName(item.type)
            prevPageSelected = position
        }
    }

    private var simpleMode = false
    private var permissionsGranted = false

    private fun setStatisticsName(scoreType: ScoreType) {
        binding.statisticName.text = getString(
            when (scoreType) {
                ScoreType.OVERALL -> R.string.dashboard_new_overall_score
                ScoreType.ACCELERATION -> R.string.dashboard_new_acceleration_score
                ScoreType.BREAKING -> R.string.dashboard_new_braking_score
                ScoreType.PHONE_USAGE -> R.string.dashboard_new_phone_dist_score
                ScoreType.SPEEDING -> R.string.dashboard_new_speeding_score
                ScoreType.CORNERING -> R.string.dashboard_new_cornering_score
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        childFragmentManager.setFragmentResultListener(
            NEED_ATTENTION_REQUEST_KEY,
            this
        ) { _, bundle ->
            if (bundle.getBoolean(DIALOG_RESULT_KEY, false)) {
                appNavigationViewModel.navigateTo(AppNavigation.AccountScreen)
            }
        }

        childFragmentManager.setFragmentResultListener(
            CHOOSER_REQUEST_KEY,
            this
        ) { _, bundle ->
            bundle.getString(DIALOG_RESULT_KEY, "")?.apply {
                try {
                    dashboardViewModel.setTripRecordMode(TripRecordMode.valueOf(this), false)
                } catch (_: Exception) {

                }
            }
        }

        childFragmentManager.setFragmentResultListener(
            RE_AUTH_REQUEST_KEY,
            this
        ) { _, bundle ->
            val result =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    bundle.getSerializable(
                        DIALOG_RESULT_KEY,
                        ReAuthDialogFragment.ReAuthResult::class.java
                    )
                } else {
                    @Suppress("DEPRECATION")
                    bundle.getSerializable(DIALOG_RESULT_KEY) as? ReAuthDialogFragment.ReAuthResult
                }
            when (result) {
                ReAuthDialogFragment.ReAuthResult.LoggedIn -> {
                    dashboardViewModel.refreshDashboardData()
                }

                ReAuthDialogFragment.ReAuthResult.LogOut -> {
                    dashboardViewModel.logout()
                }

                null -> {}
            }
        }

        childFragmentManager.setFragmentResultListener(
            UPDATE_REQUEST_KEY,
            this
        ) { _, bundle ->
            val result = bundle.getBoolean(DIALOG_RESULT_KEY, false)
            if (result) {
                openAppInPlayStore()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        binding.drivingScoresPager.registerOnPageChangeCallback(onPageChangeCallback)
    }

    override fun onStop() {
        super.onStop()
        binding.drivingScoresPager.unregisterOnPageChangeCallback(onPageChangeCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackPressedExitCallback()

        arguments?.getInt("position")

        binding.drivingScoresPager.offscreenPageLimit = 6
        binding.drivingScoresPager.adapter = scoringAdapter
        binding.rateDotIndicator.attachTo(binding.drivingScoresPager)

        setListener()
        initView()

        collectUiState()

        dashboardViewModel.apply {
            checkAppFlags()
            refreshDashboardData()
        }

        collectLogoutState()
    }

    private fun initView() = with(binding) {
        statisticsTitle.isVisible = false
        statisticsCard.isVisible = false

        rankLayout.isVisible = false
        coinsLayout.isVisible = false

        videoTitle.isVisible = false
        videoPreviewImage.isVisible = false

        scoreTrendTitle.isVisible = false
        scoreTrend.isVisible = false

        annualDrivingTitle.isVisible = false
        annualDriving.isVisible = false

        ecoScoringTitle.isVisible = false
        ecoScoring.isVisible = false

        scoredTripTitle.isVisible = false
        scoredTrip.isVisible = false

        strakesTitle.isVisible = false
        strakesMore.isVisible = false
        strakes.isVisible = false

        leadersTitle.isVisible = false
        leaders.isVisible = false

        permissionsCard.isVisible = false
    }

    @SuppressLint("SetTextI18n")
    private fun collectUiState() {
        if (dashboardViewModel.dashboardConfig.isTripRecordModeEnabled()) {
            viewLifecycleOwner.lifecycleScope.launch {
                dashboardViewModel.tripRecordMode
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .distinctUntilChanged()
                    .collect { mode ->
                        mode?.apply {
                            with(binding) {
                                tripRecordModeTitle.isVisible = true
                                tripRecordMode.apply {
                                    isVisible = true
                                    setTripRecordMode(first, second)
                                    modeDropdown.setOnClickListener(this@DashboardFragment)
                                    onButton.setOnClickListener(this@DashboardFragment)
                                    offButton.setOnClickListener(this@DashboardFragment)
                                }

                            }
                        }
                    }
            }

            viewLifecycleOwner.lifecycleScope.launch {
                dashboardViewModel.tripRecordModeUpdated
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .collect {
                        if (it) {
                            SuccessDialogFragment.getNewInstance(
                                description = getString(R.string.dashboard_trip_record_mode_success),
                                button = getString(R.string.dialog_button_ok),
                                requestKey = SUCCESS_REQUEST_KEY,
                                resultKey = DIALOG_RESULT_KEY
                            ).show(
                                childFragmentManager,
                                DashboardFragment::class.java.name
                            )
                        } else {
                            showMessage(R.string.dashboard_trip_record_mode_failure)
                        }
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            dashboardViewModel.getDashboardDataFlow()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { data ->
                    data?.apply {

                        when {
                            mileageKm >= distanceLimit -> {
                                setDrivingDataEnabled(true)

                                if (!simpleMode && dashboardViewModel.dashboardConfig.isStatisticsEnabled()) {
                                    fillData(scores)
                                }

                                if (dashboardViewModel.dashboardConfig.isAnnualDrivingSummaryEnabled()) {
                                    showAnnualDrivingSummary(
                                        tripsCount = tripsCount,
                                        mileage = mileageKm,
                                        drivingTime = drivingTime
                                    )
                                }
                                if (!simpleMode && dashboardViewModel.dashboardConfig.isScoreTrendEnabled()) {
                                    showScoreTrend(dailyScores)
                                }

                                if (!simpleMode && dashboardViewModel.dashboardConfig.isEcoScoringEnabled()) {
                                    showMainEcoScoring(ecoScore)
                                }

                                if (!simpleMode && dashboardViewModel.dashboardConfig.isLastScoredTripEnabled()) {
                                    lastTrip?.also { tripData ->
                                        showLastTrip(tripData)
                                    }
                                }

                                if (!simpleMode && dashboardViewModel.dashboardConfig.isDriveCoinsEnabled()) {
                                    driveCoins?.apply {
                                        showDriveCoins(this)
                                    }
                                }

                                if (!simpleMode && dashboardViewModel.dashboardConfig.isDrivingSrakesEnabled()) {
                                    drivingStrakes?.apply {
                                        showStrakes(this)
                                    }
                                }

                                if (!simpleMode && dashboardViewModel.dashboardConfig.isVideoEnabled()) {
                                    videoData?.apply {
                                        showVideo(this)
                                    }
                                }

                                if (!simpleMode && dashboardViewModel.dashboardConfig.isLeaderboardEnabled()) {
                                    place?.apply {
                                        totalDrivers?.apply {
                                            showLeaders(place, totalDrivers)
                                        }
                                    }
                                }

                                if (!simpleMode && dashboardViewModel.dashboardConfig.isRankEnabled()) {
                                    place?.apply {
                                        showLeaderChips(this)
                                    }

                                }

                                with(binding) {
                                    if (!simpleMode && coinsLayout.isVisible && rankLayout.isVisible) {
                                        statisticsCard.doOnPreDraw {
                                            val chipWidth = maxOf(coinsLayout.width, rankLayout.width)

                                            coinsLayout.layoutParams =
                                                coinsLayout.layoutParams.apply { width = chipWidth }
                                            rankLayout.layoutParams =
                                                rankLayout.layoutParams.apply { width = chipWidth }
                                        }
                                    }
                                }
                            }

                            else -> {
                                setDrivingDataEnabled(false)

                                showEmptyDashboard()
                            }
                        }
                    }

                    showProgressBar(data == null)
                }
        }

        if (dashboardViewModel.dashboardConfig.isVideoEnabled()) {
            viewLifecycleOwner.lifecycleScope.launch {
                dashboardViewModel.videoLink
                    .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                    .distinctUntilChanged()
                    .collectLatest {
                        it?.apply {
                            openMuxPlayer(this)
                        }
                    }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            dashboardViewModel.isUnauthenticated
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collectLatest {
                    it.apply {
                        consumeEvent {
                            if (!isDialogShowed) {
                                isDialogShowed = true
                                ReAuthDialogFragment.getNewInstance(
                                    requestKey = RE_AUTH_REQUEST_KEY,
                                    resultKey = DIALOG_RESULT_KEY,
                                ).show(childFragmentManager, DashboardFragment::class.java.name)
                            }
                        }
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            dashboardViewModel.isUnsupportedVersion
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collectLatest {
                    it.apply {
                        if (!isDialogShowed) {
                            isDialogShowed = true
                            UpdateDialogFragment.getNewInstance(
                                requestKey = UPDATE_REQUEST_KEY,
                                resultKey = DIALOG_RESULT_KEY,
                            ).show(childFragmentManager, DashboardFragment::class.java.name)
                        }
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            dashboardViewModel.isPermissionsGranted()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collectLatest {
                    permissionsGranted = it
                    showPermissionsCard()
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            dashboardViewModel.simpleModeFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collectLatest {
                    simpleMode = it
                    showPermissionsCard()
                    if (simpleMode) {
                        binding.apply {
                            statisticsTitle.isVisible = false
                            statisticsCard.isVisible = false
                            scoreTrendTitle.isVisible = false
                            scoreTrend.isVisible = false
                            ecoScoringTitle.isVisible = false
                            ecoScoring.isVisible = false
                            scoredTripTitle.isVisible = false
                            scoredTrip.isVisible = false
                            strakesTitle.isVisible = false
                            strakesMore.isVisible = false
                            strakes.isVisible = false
                            leadersTitle.isVisible = false
                            leadersMore.isVisible = false
                            leaders.isVisible = false
                        }
                    }
                }
        }
    }

    private fun setListener() = with(binding) {
        leadersMore.setOnClickListener(this@DashboardFragment)
        rankLayout.setOnClickListener(this@DashboardFragment)
        coinsLayout.setOnClickListener(this@DashboardFragment)

        videoPreviewImage.setOnClickListener(this@DashboardFragment)
    }

    private fun showProgressBar(visible: Boolean) = with(binding) {
        contentContainer.isVisible = !visible
        progressBar.isVisible = visible
    }

    private fun openMuxPlayer(url: String) {
        url.toUri().apply {
            val bundle = MuxVideoPlayerFragment.createBundle(
                domain = host?.substringAfter("stream."),
                token = query?.substringAfter("token="),
                playbackId = path?.substringAfter("/")?.substringBefore(".m3u8") ?: "",
                deviceToken = dashboardViewModel.deviceToken
            )
            findNavController().navigate(
                R.id.action_dashboardFragment_to_muxVideoPlayerFragment,
                bundle
            )
        }
    }

    private fun openAppInPlayStore() {
        val context = requireContext()
        val packageName = context.packageName
        val intent = android.content.Intent(
            android.content.Intent.ACTION_VIEW,
            "market://details?id=$packageName".toUri()
        )
        intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NO_HISTORY or android.content.Intent.FLAG_ACTIVITY_NEW_DOCUMENT or android.content.Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
        try {
            startActivity(intent)
        } catch (_: android.content.ActivityNotFoundException) {
            val webIntent = android.content.Intent(
                android.content.Intent.ACTION_VIEW,
                "https://play.google.com/store/apps/details?id=$packageName".toUri()
            )
            startActivity(webIntent)
        }
        requireActivity().finishAffinity()
    }


    private fun setDrivingDataEnabled(visible: Boolean) = with(binding) {
        if (visible) {
            if (!simpleMode && dashboardViewModel.dashboardConfig.isStatisticsEnabled()) {
                statisticsLeftButton.apply {
                    setOnClickListener(this@DashboardFragment)
                    setBackgroundResource(R.drawable.oval_button_white_ripple_effect)
                }
                statisticsRightButton.apply {
                    setOnClickListener(this@DashboardFragment)
                    setBackgroundResource(R.drawable.oval_button_white_ripple_effect)
                }
                statisticMark.apply {
                    setOnClickListener(this@DashboardFragment)
                    isVisible = true
                }
                rateDotIndicator.apply {
                    setDotIndicatorColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.design_blue
                        )
                    )
                    setStrokeDotsIndicatorColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.design_blue
                        )
                    )
                }

                coinsImage.clearColorFilter()

                val textColor =
                    ContextCompat.getColor(requireContext(), R.color.design_blue)
                rankValue.setTextColor(textColor)

                drivingScoresPager.isUserInputEnabled = true
                statisticsTitle.alpha = 1.0f
                statisticsCard.alpha = 1.0f
            }

            binding.attentionCard.isVisible = false
        } else {
            if (!simpleMode && dashboardViewModel.dashboardConfig.isStatisticsEnabled()) {
                statisticsTitle.isVisible = true
                statisticsCard.isVisible = true

                statisticsLeftButton.apply {
                    setOnClickListener(null)
                    setBackgroundResource(R.drawable.oval_button_white)
                }
                statisticsRightButton.apply {
                    setOnClickListener(null)
                    setBackgroundResource(R.drawable.oval_button_white)
                }
                statisticMark.apply {
                    isVisible = false
                    setOnClickListener(null)
                }
                rateDotIndicator.apply {
                    setDotIndicatorColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.design_black
                        )
                    )
                    setStrokeDotsIndicatorColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.design_black
                        )
                    )
                }

                drivingScoresPager.isUserInputEnabled = false

                statisticsTitle.alpha = 0.3f
                statisticsCard.alpha = 0.3f
            }

            if (!simpleMode && dashboardViewModel.dashboardConfig.isDriveCoinsEnabled()) {
                coinsLayout.isVisible = true
                coinsValue.text = "-"
                val tintColor =
                    ContextCompat.getColor(requireContext(), R.color.design_black)
                coinsImage.setColorFilter(tintColor, PorterDuff.Mode.SRC_IN)
            }

            if (!simpleMode && dashboardViewModel.dashboardConfig.isRankEnabled()) {
                rankLayout.isVisible = true
                rankValue.text = "-"
                val textColor =
                    ContextCompat.getColor(requireContext(), R.color.design_black)
                rankValue.setTextColor(textColor)
            }
            binding.attentionCard.isVisible = true
        }
    }

    private fun showPermissionsCard() {
        binding.permissionsCard.isVisible = simpleMode && permissionsGranted
    }

    private fun showEmptyDashboard() {
        scoringAdapter.updateData(Score.empty())
        setStatisticsName(ScoreType.OVERALL)
    }

    private fun fillData(scores: List<Score>) = with(binding) {
        statisticsTitle.isVisible = true
        statisticsCard.isVisible = true

        scoringAdapter.updateData(scores)
        if (scores.isNotEmpty()) {
            setStatisticsName(scores[0].type)
        } else {
            setStatisticsName(ScoreType.OVERALL)
        }
    }

    private fun showLastTrip(tripData: Trip) = with(binding) {
        scoredTripTitle.isVisible = true
        scoredTrip.isVisible = true

        scoredTrip.setTripData(
            trip = tripData,
            formatter = measuresFormatter
        )
    }

    private fun showMainEcoScoring(data: EcoScore) = with(binding) {
        ecoScoringTitle.isVisible = true
        ecoScoring.isVisible = true

        ecoScoring.setProgress(data)
    }

    private fun showScoreTrend(data: List<DailyScore>) = with(binding) {
        scoreTrendTitle.isVisible = true
        scoreTrend.isVisible = true

        scoreTrend.setData(data)
    }

    private fun showAnnualDrivingSummary(tripsCount: Int, mileage: Double, drivingTime: Double) =
        with(binding) {
            annualDrivingTitle.isVisible = true
            annualDriving.isVisible = true

            annualDriving.setAnnualDrivingData(
                tripsCount = tripsCount,
                mileage = mileage,
                drivingTime = drivingTime,
                formatter = measuresFormatter
            )
        }

    private fun showLeaders(position: Int?, totalDrivers: Int?) = with(binding) {
        leaders.isVisible = true
        leadersTitle.isVisible = true
        leadersMore.isVisible = true

        val place = position?.let { if (it <= 0) 1 else it } ?: 1
        val total = totalDrivers?.let { if (it <= 0) 1 else it } ?: 1
        leaders.setLeadersData(
            place,
            getPercentage(place, total)
        )
    }

    @Suppress("UNUSED_PARAMETER")
    @SuppressLint("SetTextI18n")
    private fun showStrakes(drivingStrakes: Any) = with(binding) {
        strakesTitle.isVisible = true
        strakesMore.isVisible = true
        strakes.isVisible = true
        // TODO Set Strakes values
    }

    @SuppressLint("SetTextI18n")
    private fun showVideo(videoData: VideoData) = with(binding) {
        videoTitle.isVisible = true
        videoPreviewImage.isVisible = true
        Log.d("video", this.toString())

        with(videoData) {
            videoPreviewImage.tag = videoID

            Glide.with(requireContext()).load(imageUrl).centerCrop()
                .transform(
                    RoundedCorners(8.dpToPx)
                ).into(videoPreviewImage)

            videoTitle.text = "${title}${body}"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showDriveCoins(coins: Int) = with(binding) {
        coinsLayout.isVisible = true
        coinsValue.text = coins.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun showLeaderChips(position: Int?) = with(binding) {
        val place = position?.let { if (it <= 0) 1 else it } ?: 1
        rankLayout.isVisible = true
        rankValue.text = "$place"
    }

    private fun getPercentage(position: Int, totalDrivers: Int): Int {
        return try {
            100 - ((position - 1) * 100 / totalDrivers)
        } catch (_: Exception) {
            100
        }
    }

    override fun onClick(v: View?) = with(binding) {
        when (v?.id) {
            tripRecordMode.modeDropdown.id -> {
                TripRecordModeDialogFragment.getNewInstance(
                    currentMode = dashboardViewModel.tripRecordMode.value?.first,
                    requestKey = CHOOSER_REQUEST_KEY,
                    resultKey = DIALOG_RESULT_KEY,
                ).show(childFragmentManager, DashboardFragment::class.java.name)
            }

            tripRecordMode.offButton.id,
            tripRecordMode.onButton.id -> {
                dashboardViewModel.tripRecordMode.value?.apply {
                    dashboardViewModel.switchTripRecordActiveMode()
                }
            }

            statisticMark.id -> {
                ScoreExplanationDialogFragment().show(
                    childFragmentManager,
                    DashboardFragment::class.java.name
                )
            }

            videoPreviewImage.id -> {
                if (videoPreviewImage.tag is String) {
                    dashboardViewModel.getVideoUrl(videoPreviewImage.tag as String)
                }
            }

            statisticsLeftButton.id -> {
                val position =
                    if (binding.drivingScoresPager.currentItem == 0) {
                        scoringAdapter.itemCount - 1
                    } else {
                        binding.drivingScoresPager.currentItem - 1
                    }
                binding.drivingScoresPager.setCurrentItem(position, true)
            }

            statisticsRightButton.id -> {
                val position =
                    if (binding.drivingScoresPager.currentItem == scoringAdapter.itemCount - 1) {
                        0
                    } else {
                        binding.drivingScoresPager.currentItem + 1
                    }
                binding.drivingScoresPager.setCurrentItem(position, true)
            }

            rankLayout.id,
            leadersMore.id -> {
                appNavigationViewModel.navigateTo(AppNavigation.LeaderboardScreen)
            }

            coinsLayout.id -> {
                appNavigationViewModel.navigateTo(AppNavigation.RewardScreen)
            }
        }
        return@with
    }


    private fun collectLogoutState() {
        viewLifecycleOwner.lifecycleScope.launch {
            dashboardViewModel.logout
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { result ->
                    if (result) appNavigationViewModel.navigateTo(AppNavigation.SplashScreen)
                }
        }
    }
}