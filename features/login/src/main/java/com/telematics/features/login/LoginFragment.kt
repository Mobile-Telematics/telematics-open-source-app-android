package com.telematics.features.login

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
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.telematics.core.common.NetworkException
import com.telematics.core.common.extension.hideKeyboard
import com.telematics.core.common.extension.isValidEmail
import com.telematics.core.common.extension.showConfirmationDialog
import com.telematics.core.common.extension.showMessage
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.core.model.UserTypeByEmail
import com.telematics.features.login.databinding.FragmentLoginBinding
import com.telematics.features.login.password.PasswordFragment
import com.telematics.features.login.registration.RegistrationFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private val loginViewModel: LoginViewModel by viewModels()

    private lateinit var binding: FragmentLoginBinding

    private var isNavigated = false

    override fun onResume() {
        super.onResume()
        isNavigated = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        initScreen()
        collectUiState()
    }

    private fun setListeners() {

        binding.loginSend.setOnClickListener {
            login()
        }

        binding.loginRegistration.setOnClickListener {
            startRegistrationFragment()
        }

        spanTextButton()

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

        binding.loginInputEmail.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                login()
            }
            return@setOnEditorActionListener true
        }
    }

    private fun spanTextButton() {
        val textUnderline = getString(R.string.sign_in_end_sign_up)
        val text = getString(R.string.sign_in_go_sign_up) + " " + textUnderline

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

        binding.loginRegistration.text = spannableString
    }

    private fun initScreen() {
        animateViews()
    }

    private fun collectUiState() {

        viewLifecycleOwner.lifecycleScope.launch {
            loginViewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { uiState ->

                    with(uiState) {
                        showProgressBar(isLoading)

                        if (!isLoading) {
                            error?.let { throwable ->
                                handleFailure(throwable)
                                loginViewModel.onErrorHandled()
                            }
                            when (userState) {
                                UserTypeByEmail.NEW_USER -> {
                                    loginViewModel.onUserStateHandled()
                                    showConfirmationDialog(
                                        getString(R.string.dialog_new_user),
                                        ::startNextScreen,
                                        getString(R.string.dialog_confirm),
                                        false
                                    )
                                }

                                UserTypeByEmail.REGULAR_USER -> {
                                    loginViewModel.onUserStateHandled()
                                    startNextScreen()
                                }

                                UserTypeByEmail.NO_USER -> {
                                    loginViewModel.onUserStateHandled()
                                    showConfirmationDialog(
                                        getString(R.string.dialog_no_user),
                                        ::startRegistrationFragment,
                                        getString(R.string.sign_up),
                                        true
                                    )
                                }

                                else -> {

                                }
                            }
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

        animateView(binding.loginTitle, 1)
        animateView(binding.loginInputEmailTill, 1)
        animateView(binding.loginSend, 2)
        animateView(binding.loginRegistration, 3)
        animateView(binding.loginPolicyLayout, 4)
    }

    private fun showProgressBar(visible: Boolean) {
        binding.progressBar.isVisible = visible
    }

    private fun validFields(): Boolean {

        val emailField = binding.loginInputEmail.text.toString().trim()

        if (emailField.isBlank()) {
            showLoginFailedMessage(R.string.auth_error_empty_email)
            return false
        } else {
            if (!emailField.isValidEmail()) {
                showLoginFailedMessage(R.string.auth_error_incorrect_email)
                return false
            }
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

    private fun getLoginField(): String = binding.loginInputEmail.text.toString().trim()

    /**↓login*/
    private fun login() {

        hideKeyboard()

        if (loginViewModel.uiState.value.isLoading) return

        if (!validFields()) return

        loginViewModel.checkUserByEmail(getLoginField())

    }

    private fun handleFailure(throwable: Throwable? = null) {

        when (throwable) {
            is NetworkException.NoNetwork -> {
                showLoginFailedMessage(R.string.auth_error_network)
            }

            else -> {
                showLoginFailedMessage(R.string.server_error_something_went_wrong)
            }
        }
    }

    private fun startNextScreen() {
        if (isNavigated) return
        isNavigated = true
        loginViewModel.onUserStateHandled()
        val bundle = PasswordFragment.createBundle(
            getLoginField()
        )
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.login_fragment) {
            navController.navigate(R.id.action_login_fragment_to_password_fragment, bundle)
        }
    }

    private fun startRegistrationFragment() {
        if (isNavigated) return
        isNavigated = true
        val bundle = RegistrationFragment.createBundle(
            login = getLoginField(),
        )
        val navController = findNavController()
        if (navController.currentDestination?.id == R.id.login_fragment) {
            navController.navigate(R.id.action_login_fragment_to_registration_fragment, bundle)
        }
    }

    companion object {
        private const val TAG = "LoginFragment"
    }
}