package com.telematics.features.dashboard.dialog

import android.R.color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.telematics.core.common.NetworkException
import com.telematics.core.common.extension.drawable
import com.telematics.core.common.extension.hideKeyboard
import com.telematics.core.common.extension.showConfirmationDialog
import com.telematics.core.common.extension.showMessage
import com.telematics.core.content.R
import com.telematics.features.dashboard.databinding.FragmentDialogReAuthBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReAuthDialogFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentDialogReAuthBinding
    private val viewModel: ReAuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(color.transparent)

        isCancelable = false

        binding = FragmentDialogReAuthBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectUiState()

        binding.confirmBtn.setOnClickListener(this)
        binding.logOutBtn.setOnClickListener(this)
        binding.resetPassword.setOnClickListener(this)

        binding.inputPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.confirmBtn.isEnabled = !s.isNullOrEmpty()
                updatePasswordHint()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.confirmBtn.isEnabled = !binding.inputPassword.text.isNullOrEmpty()

        binding.inputPassword.setOnFocusChangeListener { _, hasFocus ->
            updatePasswordHint()
        }

        binding.inputPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_GO) {
                if (!binding.inputPassword.text.isNullOrEmpty()) {
                    binding.confirmBtn.performClick()
                }
                true
            } else {
                false
            }
        }
    }

    private fun updatePasswordHint() {
        val hasFocus = binding.inputPassword.hasFocus()
        val hasText = !binding.inputPassword.text.isNullOrEmpty()
        if (hasFocus || hasText) {
            binding.password.hint = null
        } else {
            binding.password.hint = getString(R.string.dashboard_dialog_re_auth_hint)
        }
    }

    private fun collectUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getUserProfileFlow()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collectLatest {
                    it?.apply {
                        binding.email.text = email
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { uiState ->

                    with(uiState) {
                        showProgressBar(isLoading)

                        binding.apply {
                            inputPassword.background = resources.drawable(
                                if (errorPasswordEnabled) R.drawable.bg_text_input_error_rect
                                else R.drawable.bg_text_input_rect,
                                requireContext()
                            )
                            error.isVisible = errorPasswordEnabled
                        }

                        error?.let { throwable ->
                            handleFailure(throwable)
                            viewModel.onErrorHandled()
                        }

                        if (this.isLoggedIn) {
                            setResult(ReAuthResult.LoggedIn)
                        }

                        if (isLinkSent) {
                            viewModel.onLinkSentHandled()
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

    private fun showLoginFailedMessage(@StringRes id: Int) {
        showMessage(getString(id))
    }

    private fun showProgressBar(visible: Boolean) {
        binding.progressBar.isVisible = visible
    }

    private fun setResult(result: ReAuthResult?) {
        val requestKey = arguments?.getString(EXTRA_REQUEST_KEY) ?: EXTRA_REQUEST_KEY
        val resultKey = arguments?.getString(EXTRA_RESULT_KEY) ?: EXTRA_RESULT_KEY
        setFragmentResult(requestKey, bundleOf(resultKey to result))
        dismiss()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.confirmBtn.id -> {
                binding.inputPassword.hideKeyboard()
                binding.inputPassword.clearFocus()
                viewModel.reAuthByEmail(
                    binding.email.text.toString(),
                    binding.inputPassword.text?.toString() ?: ""
                )
            }

            binding.logOutBtn.id -> {
                setResult(ReAuthResult.LogOut)
            }

            binding.resetPassword.id -> {
                binding.inputPassword.hideKeyboard()
                binding.inputPassword.clearFocus()
                viewModel.resetPassword(binding.email.text.toString())
            }
        }
    }

    companion object {
        private const val EXTRA_REQUEST_KEY = "extra_request_key"
        private const val EXTRA_RESULT_KEY = "extra_result_key"

        fun getNewInstance(
            requestKey: String,
            resultKey: String
        ): ReAuthDialogFragment {
            return bundleOf(
                EXTRA_REQUEST_KEY to requestKey,
                EXTRA_RESULT_KEY to resultKey
            ).let {
                val fragment = ReAuthDialogFragment()
                fragment.arguments = it
                fragment
            }
        }
    }

    enum class ReAuthResult {
        LoggedIn,
        LogOut
    }
}