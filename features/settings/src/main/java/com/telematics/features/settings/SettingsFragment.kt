package com.telematics.features.settings

import android.app.AlertDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.telematics.core.common.extension.showMessage
import com.telematics.core.common.extension.systemBarsAndDisplayCutout
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.core.common.navigation.AppNavigation
import com.telematics.core.common.navigation.AppNavigationViewModel
import com.telematics.core.common.util.TryOpenLink
import com.telematics.features.settings.databinding.FragmentSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SettingsFragment : BaseFragment(), View.OnClickListener {

    private lateinit var binding: FragmentSettingsBinding

    private val appNavigationViewModel: AppNavigationViewModel by activityViewModels()
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            applyInsets(insets)
        }

        setListeners()
        collectLogoutState()

        if (BuildConfig.DEBUG) {
            binding.version.apply {
                isVisible = true
                text = BuildConfig.VERSION
            }
        }
    }

    private fun setListeners() {

        binding.toolbar.setNavigationOnClickListener {
            openMainFragment()
        }

        binding.settingsOBD.setOnClickListener(this)
        binding.settingsMeasures.setOnClickListener(this)
        binding.settingsCompanyID.setOnClickListener(this)
        binding.settingsDriverLogbook.setOnClickListener(this)
        binding.settingsLogout.setOnClickListener(this)
        binding.settingsPrivacy.setOnClickListener(this)
        binding.settingsTerms.setOnClickListener(this)
        binding.settingsRate.setOnClickListener(this)
    }

    private fun collectLogoutState() {
        viewLifecycleOwner.lifecycleScope.launch {
            settingsViewModel.logout
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { result ->
                    if (result) openSplashFragment()
                }
        }
    }

    private fun logout() {

        AlertDialog.Builder(context)
            .setMessage(R.string.nav_logout_question)
            .setCancelable(true)
            .setPositiveButton(R.string.nav_logout_yes) { d, _ ->
                settingsViewModel.logout()
                d.dismiss()
            }
            .setNegativeButton(R.string.nav_logout_no) { d, _ ->
                d.dismiss()
            }
            .show()
    }

    private fun openPrivacy() {

        val link = BuildConfig.PRIVACY_POLICY
        if (!TryOpenLink(requireContext()).open(link)) {
            showMessage(R.string.link_open_error)
        }
    }

    private fun openTerms() {

        val link = BuildConfig.TERMS_OF_USE
        if (!TryOpenLink(requireContext()).open(link)) {
            showMessage(R.string.link_open_error)
        }
    }

    private fun rateAppInMarket() {
        val uri = ("market://details?id=" + context?.packageName).toUri()

        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(
            Intent.FLAG_ACTIVITY_NO_HISTORY or
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        )
        try {
            startActivity(goToMarket)
        } catch (_: ActivityNotFoundException) {
            TryOpenLink(requireContext()).open("http://play.google.com/store/apps/details?id=" + context?.packageName)
        }

    }

    private fun openSplashFragment() {
        val uri = getString(R.string.deep_link_splash_fragment).toUri()

        val navOptions = NavOptions.Builder()
            .setPopUpTo(
                findNavController().graph.id,
                true
            )
            .setLaunchSingleTop(true)
            .build()
        findNavController().navigate(uri, navOptions)
    }

    private fun openMainFragment() {

        onBackPressed()
    }

    private fun openOBDFragment() {

        findNavController().navigate(R.id.action_settings_fragment_to_obd_nav_graph)
    }

    private fun openCompanyIdFragment() {

        findNavController().navigate(R.id.action_settings_fragment_to_companyIdFragment)
    }

    private fun openDriverLogbookFragment() {

        findNavController().navigate(R.id.action_settings_fragment_to_driverLogbookFragment)
    }

    private fun openMeasures() {

        findNavController().navigate(R.id.action_settings_fragment_to_measuresFragment)
    }

    private fun applyInsets(windowInsets: WindowInsetsCompat): WindowInsetsCompat {
        val insetTypeMask = systemBarsAndDisplayCutout()

        val insets = windowInsets.getInsets(insetTypeMask)

        binding.root.updatePadding(top = insets.top)

        return WindowInsetsCompat.Builder()
            .setInsets(insetTypeMask, insets)
            .build()
    }

    override fun onClick(v: View?) = with(binding) {
        when (v?.id) {
            settingsOBD.id -> {
                openOBDFragment()
            }

            settingsMeasures.id -> {
                openMeasures()
            }

            settingsCompanyID.id -> {
                openCompanyIdFragment()
            }

            settingsDriverLogbook.id -> {
                openDriverLogbookFragment()
            }

            settingsLogout.id -> {
                logout()
            }

            settingsPrivacy.id -> {
                openPrivacy()
            }

            settingsTerms.id -> {
                openTerms()
            }

            settingsRate.id -> {
                rateAppInMarket()
            }
        }
    }
}