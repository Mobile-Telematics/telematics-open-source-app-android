package com.telematics.features.login.registration

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
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
import com.telematics.core.common.extension.isValidEmail
import com.telematics.core.common.extension.showMessage
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.features.login.BuildConfig
import com.telematics.features.login.R
import com.telematics.features.login.databinding.FragmentRegistrationBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegistrationFragment : BaseFragment() {

    private val viewModel: RegistrationViewModel by viewModels()

    private lateinit var binding: FragmentRegistrationBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val login = arguments?.getString(BUNDLE_LOGIN_KEY) ?: ""

        Log.d(TAG, "onViewCreated: login:$login")

        setListeners()
        initScreen()

        bindFields(login)
        collectUiState()
    }

    private fun setListeners() {

        binding.registrationSend.setOnClickListener {
            registration()
        }

        binding.registrationSighIn.setOnClickListener {
            startLogin()
        }

        spanTextButton()

        val rawStringPolicy =
            "<a href=\"${BuildConfig.PRIVACY_POLICY}\">${
                getString(
                    R.string.login_screen_policy
                )
            }</a>"
        binding.registrationPolicy.text =
            HtmlCompat.fromHtml(rawStringPolicy, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.registrationPolicy.movementMethod = LinkMovementMethod.getInstance()

        val rawStringTerms =
            "<a href=\"${BuildConfig.TERMS_OF_USE}\">${
                getString(
                    R.string.login_screen_terms
                )
            }</a>"
        binding.registrationTerms.text =
            HtmlCompat.fromHtml(rawStringTerms, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.registrationTerms.movementMethod = LinkMovementMethod.getInstance()

        binding.registrationInputPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                registration()
            }
            return@setOnEditorActionListener true
        }
    }

    private fun spanTextButton() {
        val textUnderline = getString(R.string.sign_up_end_sign_in)
        val text = getString(R.string.sign_un_go_sign_in) + " " + textUnderline

        val start = text.indexOf(textUnderline)
        val end = start + textUnderline.length

        val spannableString = SpannableString(text)

        spannableString.setSpan(
            UnderlineSpan(),
            start,
            end,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        ResourcesCompat.getFont(
            requireContext(),
            R.font.open_sans_bold_700
        )?.style?.also { style ->
            spannableString.setSpan(
                StyleSpan(style),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        binding.registrationSighIn.text = spannableString
    }

    private fun initScreen() {
        animateViews()
    }

    private fun collectUiState() {

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { uiState ->

                    with(uiState) {
                        showProgressBar(isLoading)

                        error?.let { throwable ->
                            handleFailure(throwable)
                            viewModel.onErrorHandled()
                        }
                        if (isRegistered) {
                            viewModel.onUserStateHandled()
                            startMainScreen()
                        }
                    }
                }
        }
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

        animateView(binding.registrationTitle, 1)
        animateView(binding.registrationInputEmailTill, 1)
        animateView(binding.registrationInputPasswordTill, 1)
        animateView(binding.registrationSend, 2)
        animateView(binding.registrationSighIn, 3)
        animateView(binding.registrationPolicyLayout, 4)
    }

    private fun bindFields(login: String) {

        binding.registrationInputEmail.setText(login)
    }

    private fun showProgressBar(visible: Boolean) {
        binding.progressBar.isVisible = visible
    }

    private fun validFields(): Boolean {

        val passwordField = binding.registrationInputPassword.text.toString()
        val emailField = binding.registrationInputEmail.text.toString()

        if (emailField.isBlank()) {
            showLoginFailedMessage(R.string.auth_error_empty_email)
            return false
        } else {
            if (!emailField.isValidEmail()) {
                showLoginFailedMessage(R.string.auth_error_incorrect_email)
                return false
            }
        }

        if (passwordField.isBlank()) {
            showLoginFailedMessage(R.string.auth_error_empty_password)
            return false
        }

        if (passwordField.length <= PASSWORD_MIN_LENGTH) {
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

    private fun getLoginField(): String = binding.registrationInputEmail.text.toString().trim()

    private fun getPasswordField(): String {
        return binding.registrationInputPassword.text.toString()
    }

    private fun registration() {

        hideKeyboard()

        if (!validFields()) return

        viewModel.registerUserByEmail(
            getLoginField(),
            getPasswordField()
        )
    }

    private fun handleFailure(throwable: Throwable? = null) {

        when (throwable) {
            is NetworkException.BadRequestException -> {
                showLoginFailedMessage(R.string.auth_error_user_exist)
            }

            is NetworkException.ConflictException -> {
                showLoginFailedMessage(R.string.auth_error_user_exist)
            }

            is NetworkException.NoNetwork -> {
                showLoginFailedMessage(R.string.auth_error_network)
            }

            else -> {
                showLoginFailedMessage(R.string.server_error_something_went_wrong)
            }
        }
    }

    private fun startLogin() {
        findNavController().popBackStack()
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
        private const val TAG = "RegistrationFragment"

        private const val PASSWORD_MIN_LENGTH = 4

        private const val BUNDLE_LOGIN_KEY = "login_key"

        fun createBundle(
            login: String?,
        ) = bundleOf(
            BUNDLE_LOGIN_KEY to login,
        )
    }
}