package com.telematics.features.splash

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.telematics.features.splash.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashFragment : Fragment() {

    private val viewModel: SplashViewModel by viewModels()

    private lateinit var binding: FragmentSplashBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectUiState()
        viewModel.checkState()
    }

    private fun collectUiState() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { uiState ->

                    with(uiState) {
                        viewModel.onNavigationStateHandled()
                        when (navigationState) {
                            SplashViewModel.NavigationState.MainScreen -> openMainScreen()
                            SplashViewModel.NavigationState.OnboardingScreen -> openOnboardingScreen()
                            SplashViewModel.NavigationState.LoginScreen -> openLoginScreen()
                            else -> {}
                        }
                    }
                }
        }
    }

    private fun openOnboardingScreen() {
        val uri = getString(R.string.deep_link_onboarding_fragment).toUri()
        openScreen(uri)
    }

    private fun openLoginScreen() {
        val uri = getString(R.string.deep_link_login_fragment).toUri()
        openScreen(uri)
    }

    private fun openMainScreen() {
        val uri = getString(R.string.deep_link_main_fragment).toUri()
        openScreen(uri)
    }

    private fun openScreen(uri: Uri) {
        val navOptions = NavOptions.Builder()
            .setPopUpTo(
                R.id.splash_fragment,
                true
            )
            .setLaunchSingleTop(true)
            .build()
        findNavController().navigate(uri, navOptions)
    }
}