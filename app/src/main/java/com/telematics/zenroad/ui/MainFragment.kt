package com.telematics.zenroad.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.telematics.core.common.Constants.BOOLEAN_RESULT_KEY
import com.telematics.core.common.Constants.BOTTOM_BAR_VISIBILITY_KEY
import com.telematics.core.common.Constants.INT_RESULT_KEY
import com.telematics.core.common.Constants.TOP_BAR_VISIBILITY_KEY
import com.telematics.core.common.extension.getNotGrantedNotificationPermissions
import com.telematics.core.common.extension.loadCircularImage
import com.telematics.core.common.extension.observeOnce
import com.telematics.core.common.extension.systemBarsInsets
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.core.common.navigation.AppNavigation
import com.telematics.core.common.navigation.AppNavigationViewModel
import com.telematics.core.model.NavigationTarget
import com.telematics.core.model.UserProfile
import com.telematics.features.account.AccountFeatureHost
import com.telematics.features.dashboard.DashboardFeatureHost
import com.telematics.features.feed.FeedFeatureHost
import com.telematics.features.leaderboard.LeaderboardFeatureHost
import com.telematics.features.reward.RewardFeatureHost
import com.telematics.zenroad.R
import com.telematics.zenroad.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainFragment : BaseFragment(), View.OnClickListener {

    private val appNavigationViewModel: AppNavigationViewModel by activityViewModels()
    private val mainFragmentViewModel: MainFragmentViewModel by viewModels()

    private lateinit var binding: FragmentMainBinding

    private var simpleMode = false

    private val requestMultiplePermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->

        results.forEach {
            when (it.key) {
                android.Manifest.permission.POST_NOTIFICATIONS -> {
                    mainFragmentViewModel.onNotificationPermissionRequested()
                    initTrackingApi()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val argumentName = getString(R.string.deep_link_main_fragment)
            .substringAfter("?")
            .substringBefore("=")

        @Suppress("DEPRECATION")
        val navTo = arguments?.getSerializable(argumentName) as? NavigationTarget

        setInset()
        collectAppNavigation()
        initTrackingApi()
        initBottomMenu(navTo)

        initBars()
        setListener()
    }

    private fun setListener() = with(binding) {
        toolbar.chat.setOnClickListener(this@MainFragment)
        toolbar.toolbarSettings.setOnClickListener(this@MainFragment)
        toolbar.editAvatar.setOnClickListener(this@MainFragment)
    }

    private fun collectAppNavigation() {

        viewLifecycleOwner.lifecycleScope.launch {
            appNavigationViewModel.appNavigation
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect { screen ->

                    with(binding.mainBottomNav) {
                        when (screen) {
                            AppNavigation.TripsScreen -> {
                                selectedItemId = R.id.nav_feed
                            }

                            AppNavigation.LeaderboardScreen -> {
                                selectedItemId = R.id.nav_leaderboard
                            }

                            AppNavigation.DashboardScreen -> {
                                selectedItemId = R.id.nav_dashboard
                            }

                            AppNavigation.RewardScreen -> {
                                selectedItemId = R.id.nav_reward
                            }

                            AppNavigation.RewardStreaksScreen -> {
                                selectedItemId = R.id.nav_reward
                                navToReward(true)
                            }

                            AppNavigation.AccountScreen -> {
                                mainFragmentViewModel.saveCurrentBottomMenuState(R.id.nav_profile)
                                navToAccount()
                            }

                            AppNavigation.SettingsScreen -> {
                                navToSettings()
                            }

                            AppNavigation.SplashScreen -> {
                                val uri = getString(R.string.deep_link_splash_fragment).toUri()
                                val navOptions = androidx.navigation.NavOptions.Builder()
                                    .setPopUpTo(findNavController().graph.id, true)
                                    .setLaunchSingleTop(true)
                                    .build()
                                findNavController().navigate(uri, navOptions)
                            }

                            AppNavigation.Idle -> {

                            }
                        }
                        appNavigationViewModel.navigateTo(AppNavigation.Idle)
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            mainFragmentViewModel.getUserProfileFlow()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { userProfile ->
                    userProfile?.apply {
                        bindUser(this)
                    }
                }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            mainFragmentViewModel.getSimpleModeFlow()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { mode ->
                    simpleMode = mode
                    Log.d("SimpleMode", "$mode")
                    binding.mainBottomNav.isVisible = !simpleMode

                    binding.toolbar.toolbarEmail.apply {
                        isVisible = simpleMode && !text.isNullOrBlank()
                    }

                    binding.toolbar.arrow.isVisible = simpleMode

                    if (mode) {
                        binding.toolbar.userProfile.setOnClickListener {
                            binding.mainBottomNav.selectedItemId = R.id.nav_profile
                        }
                    }
                }
        }
    }

    private fun bindUser(user: UserProfile) = with(user) {

        val userName = fullName

        binding.toolbar.toolbarUserName.apply {
            text = userName
            isVisible = userName.isNotBlank()
        }


        binding.toolbar.userName.text = userName
        binding.toolbar.userName.isVisible = userName.isNotBlank()

        binding.toolbar.toolbarEmail.apply {
            text = email
            isVisible = simpleMode && !email.isNullOrBlank()
        }

        binding.toolbar.arrow.isVisible = simpleMode

        binding.toolbar.email.text = email
        binding.toolbar.email.isVisible = !email.isNullOrBlank()

        if (profileImage != null) {
            binding.toolbar.toolbarAvatar.loadCircularImage(
                model = profileImage,
                errorImageId = com.telematics.features.dashboard.R.drawable.ic_user_no_avatar
            )
            binding.toolbar.expandedAvatar.loadCircularImage(
                model = profileImage,
                errorImageId = com.telematics.features.account.R.drawable.ic_user_no_avatar
            )
        } else {
            binding.toolbar.toolbarAvatar.setImageResource(com.telematics.features.account.R.drawable.ic_user_no_avatar)
            binding.toolbar.expandedAvatar.setImageResource(com.telematics.features.account.R.drawable.ic_user_no_avatar)
        }
    }

    private fun initBars() {
        childFragmentManager.setFragmentResultListener(
            BOTTOM_BAR_VISIBILITY_KEY,
            viewLifecycleOwner
        ) { _, bundle ->

            val result = bundle.getBoolean(BOOLEAN_RESULT_KEY, true)

            binding.mainBottomNav.isVisible = result && !simpleMode
        }

        childFragmentManager.setFragmentResultListener(
            TOP_BAR_VISIBILITY_KEY,
            viewLifecycleOwner
        ) { _, bundle ->

            val result = bundle.getInt(INT_RESULT_KEY, 0)

            showToolbar(enabled = result != 0, expanded = result == 2)
        }
    }

    private fun initBottomMenu(navTo: NavigationTarget?) {

        val bottomNavigationView = binding.mainBottomNav
        bottomNavigationView.itemTextAppearanceActive = R.style.bottom_selected_text
        bottomNavigationView.itemTextAppearanceInactive = R.style.bottom_normal_text

        var previousItemId = 0

        bottomNavigationView.setOnItemSelectedListener {

            mainFragmentViewModel.saveCurrentBottomMenuState(it.itemId)

            if (previousItemId == it.itemId) {
                return@setOnItemSelectedListener true
            }
            previousItemId = it.itemId

            when (it.itemId) {
                R.id.nav_feed -> {
                    navToFeed()
                }

                R.id.nav_leaderboard -> {
                    navToLeaderboard()
                }

                R.id.nav_dashboard -> {
                    navToDashboard()
                }

                R.id.nav_reward -> {
                    navToReward()
                }

                R.id.nav_profile -> {
                    navToAccount()
                }

                else -> {
                    navToDashboard()
                }
            }
            return@setOnItemSelectedListener true
        }

        //first state
        navTo?.let { target ->
            when (target) {
                NavigationTarget.ACCOUNT ->
                    bottomNavigationView.selectedItemId = R.id.nav_profile

                else -> {
                    observeSavedBottomMenuState { state ->
                        if (state == 0) {
                            bottomNavigationView.selectedItemId = R.id.nav_dashboard
                        } else {
                            bottomNavigationView.selectedItemId = state
                        }
                    }
                }
            }
        }
    }

    private fun observeSavedBottomMenuState(action: (bottomMenuState: Int) -> Unit) {

        if (mainFragmentViewModel.getSaveStateBundle.value == null) {
            action(0)
            return
        }

        mainFragmentViewModel.getSaveStateBundle.observe(viewLifecycleOwner) { bundle ->
            val bottomMenuState = mainFragmentViewModel.bundleToListSize(bundle)
            action(bottomMenuState)
            mainFragmentViewModel.getSaveStateBundle.removeObservers(viewLifecycleOwner)
        }
    }

    private fun initTrackingApi() {

        mainFragmentViewModel.checkPermissions().observeOnce(viewLifecycleOwner) { result ->
            result.onSuccess { allPermissionsGranted ->
                if (!allPermissionsGranted) {
                    startWizard()
                } else {
                    mainFragmentViewModel.enableTracking()
                }
            }
            result.onFailure {
                startWizard()
            }
        }

        mainFragmentViewModel.setDeviceTokenForTrackingApi()
        setIntentForNotification()
        checkNotificationPermissions()
    }

    // if your app needs to request notification permission (Android 13+),
    // set REQUEST_NOTIFICATION_PERMISSION = true in AppConfig
    private fun checkNotificationPermissions() {
        mainFragmentViewModel.checkNotificationPermissions()
            .observeOnce(viewLifecycleOwner) { result ->
                if (result) {
                    requestMultiplePermissions.launch(
                        getNotGrantedNotificationPermissions(
                            requireContext()
                        ).toTypedArray()
                    )
                }
            }
    }

    private fun startWizard() {

        mainFragmentViewModel.startWizard(requireActivity())
    }

    private fun setIntentForNotification() {

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        mainFragmentViewModel.setIntentForNotification(intent)
    }

    private fun navToFeed() {
        openFragment(FeedFeatureHost())
    }

    private fun navToLeaderboard() {
        openFragment(LeaderboardFeatureHost())
    }

    private fun navToDashboard() {
        mainFragmentViewModel.updateIntercomUser()
        openFragment(DashboardFeatureHost())
    }

    private fun navToAccount() {
        openFragment(AccountFeatureHost())
    }

    private fun navToReward(toStreaks: Boolean = false) {
        openFragment(
            RewardFeatureHost.createFragment(toStreaks)
        )
    }

    private fun showToolbar(enabled: Boolean = true, expanded: Boolean = false) = with(binding) {
        if (enabled) {
            toolbar.userProfile.isInvisible = expanded
            toolbar.expandedToolbarLayout.isVisible = expanded
        }
        toolbar.root.isVisible = enabled
    }

    private fun navToSettings() {
        findNavController().navigate(R.id.action_mainFragment_to_settings_nav_graph)
    }

    private fun openFragment(fragment: Fragment) {

        val container = R.id.main_fragment_container
        val manager: FragmentManager = childFragmentManager
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.replace(container, fragment)
        transaction.commit()
    }

    private fun setInset() {
        binding.toolbar.root.setOnApplyWindowInsetsListener { view, insets ->
            val systemBarsInsets = systemBarsInsets(insets)
            view.updatePadding(
                top = systemBarsInsets.top
            )

            insets
        }
    }

    override fun onClick(v: View?) = with(binding) {
        when (v?.id) {
            toolbar.chat.id -> {
                mainFragmentViewModel.showChat()
            }

            toolbar.toolbarSettings.id -> {
                navToSettings()
            }

            toolbar.editAvatar.id -> {
                appNavigationViewModel.editAvatar()
            }
        }
        return@with
    }
}