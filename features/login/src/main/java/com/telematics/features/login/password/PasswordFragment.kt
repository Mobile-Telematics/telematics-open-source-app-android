package com.telematics.features.login.password

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.telematics.core.common.NetworkException
import com.telematics.core.common.extension.hideKeyboard
import com.telematics.core.common.extension.showConfirmationDialog
import com.telematics.core.common.extension.showMessage
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.features.login.BuildConfig
import com.telematics.features.login.R
import com.telematics.features.login.databinding.FragmentPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PasswordFragment : BaseFragment() {

    private val passwordViewModel: PasswordViewModel by viewModels()

    private lateinit var binding: FragmentPasswordBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackPressedCallback()
        setListeners()
        initScreen()
        collectUiState()
    }

    private fun setListeners() {

        binding.loginSend.setOnClickListener {
            login()
        }

        binding.loginNewPassword.setOnClickListener {
            hideKeyboard()
            passwordViewModel.resetPassword(arguments?.getString(BUNDLE_LOGIN_KEY) ?: "")
        }

        binding.loginNewEmail.setOnClickListener {
            onBackPressed()
        }

        spanTextButton(binding.loginNewPassword)
        spanTextButton(binding.loginNewEmail)

        val rawStringPolicy =
            "<a href=\"${BuildConfig.PRIVACY_POLICY}\">${
                getString(
                    R.string.login_screen_policy
                )
            }</a>"
        binding.loginPolicy.text =
            HtmlCompat.fromHtml(rawStringPolicy, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.loginPolicy.movementMethod = LinkMovementMethod.getInstance()

        val rawStringTerms =
            "<a href=\"${BuildConfig.TERMS_OF_USE}\">${
                getString(
                    R.string.login_screen_terms
                )
            }</a>"
        binding.loginTerms.text =
            HtmlCompat.fromHtml(rawStringTerms, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.loginTerms.movementMethod = LinkMovementMethod.getInstance()

        binding.loginInputPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                login()
            }
            return@setOnEditorActionListener true
        }
    }

    private fun spanTextButton(textView: TextView) {
        val text = textView.text

        val spannableString = SpannableString(text)

        spannableString.setSpan(
            UnderlineSpan(),
            0,
            text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        textView.text = spannableString
    }

    private fun initScreen() {
        animateViews()
    }

    private fun collectUiState() {

        viewLifecycleOwner.lifecycleScope.launch {
            passwordViewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { uiState ->

                    with(uiState) {
                        showProgressBar(isLoading)

                        error?.let { throwable ->
                            handleFailure(throwable)
                            passwordViewModel.onErrorHandled()
                        }

                        if (this.isLoggedIn) {
                            passwordViewModel.onUserStateHandled()
                            startMainScreen()

                        }

                        if (isLinkSent) {
                            passwordViewModel.onLinkSentHandled()
                            showConfirmationDialog(
                                getString(R.string.dialog_reset_password),
                                {},
                                getString(R.string.dialog_confirm),
                                false
                            )
                        }
                    }
                }
        }
    }

    private fun showProgressBar(visible: Boolean) {
        binding.progressBar.isVisible = visible
    }

    private fun animateViews() {

        fun animateView(view: View, index: Int = 1) {

            val duration = 500L
            val durationK = 100L
            val d = duration + durationK * index

            view.alpha = 0f
            view.animate().alpha(1f).setDuration(d)
                .start()
        }

        animateView(binding.loginTitle, 1)
        animateView(binding.loginInputPasswordTill, 1)
        animateView(binding.loginSend, 2)
        animateView(binding.loginNewPassword, 3)
        animateView(binding.loginNewEmail, 3)
        animateView(binding.loginPolicyLayout, 4)
    }


    private fun validFields(): Boolean {

        val passwordField = binding.loginInputPassword.text.toString().trim()

        if (passwordField.isBlank()) {
            showLoginFailedMessage(R.string.auth_error_empty_password)
            return false
        }

        if (passwordField.length < PASSWORD_MIN_LENGTH) {
            showLoginFailedMessage(
                getString(
                    R.string.auth_error_short_password,
                    PASSWORD_MIN_LENGTH
                )
            )
            return false
        }

        return true
    }

    private fun showLoginFailedMessage(@StringRes id: Int) {
        showLoginFailedMessage(getString(id))
    }

    private fun showLoginFailedMessage(message: String) {
        Log.d(TAG, "message: $message")
        showMessage(message)
    }

    private fun getPasswordField(): String = binding.loginInputPassword.text.toString().trim()

    /**↓login*/
    private fun login() {

        hideKeyboard()

        if (!validFields()) return

        passwordViewModel.signInUserByEmail(
            arguments?.getString(BUNDLE_LOGIN_KEY) ?: "",
            getPasswordField()
        )
    }

    private fun handleFailure(throwable: Throwable? = null) {

        when (throwable) {
            is NetworkException.BadRequestException -> {
                showLoginFailedMessage(R.string.auth_error_invalid_password)
            }

            is NetworkException.NoNetwork -> {
                showLoginFailedMessage(R.string.auth_error_network)
            }

            else -> {
                showLoginFailedMessage(R.string.server_error_something_went_wrong)
            }
        }
    }


    private fun startMainScreen() {
        val uri = getString(R.string.deep_link_main_fragment).toUri()

        val navOptions = NavOptions.Builder()
            .setPopUpTo(
                findNavController().graph.id,
                true
            )
            .setLaunchSingleTop(true)
            .build()
        findNavController().navigate(uri, navOptions)
    }

    companion object {
        private const val TAG = "PasswordFragment"

        private const val PASSWORD_MIN_LENGTH = 4

        private const val BUNDLE_LOGIN_KEY = "login_key"

        fun createBundle(
            login: String?,
        ) = bundleOf(
            BUNDLE_LOGIN_KEY to login,
        )
    }
}