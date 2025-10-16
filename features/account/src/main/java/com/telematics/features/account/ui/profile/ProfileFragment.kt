package com.telematics.features.account.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.telematics.core.common.NetworkException
import com.telematics.core.common.extension.hideKeyboard
import com.telematics.core.common.extension.showMessage
import com.telematics.core.common.extension.systemBarsInsets
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.core.model.UserProfile
import com.telematics.core.model.getEmptyUserProfile
import com.telematics.features.account.R
import com.telematics.features.account.databinding.FragmentProfileBinding
import com.telematics.features.account.dialog.EnterDateDialog.enterDate
import com.telematics.features.account.dialog.EnterDateDialog.getServerDate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment() {

    @Inject
    lateinit var profileViewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setBackPressedCallback()
        setListeners()
        setInset()
        collectUiState()
    }

    private fun setListeners() {

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }


        binding.accountWizardSaveBtn.setOnClickListener {
            saveUser()
        }

        binding.clientId.textField.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                saveUser()
            }
            return@setOnEditorActionListener true
        }
    }

    private fun collectUiState() {

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { uiState ->

                    with(uiState) {
                        showProgressBar(isLoading)

                        error?.let { throwable ->
                            handleFailure(throwable)
                            profileViewModel.onErrorHandled()
                        }

                        if (profileSaved) finish()
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            profileViewModel.getUserProfileFlow()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { userProfile ->
                    userProfile?.also { user ->
                        bindUser(user)
                    }
                }
        }
    }

    private fun showProgressBar(visible: Boolean) {
        binding.progressBar.isVisible = visible
    }

    private var userEmail: String? = null
    private var userProfileImage: String? = null

    private fun bindUser(user: UserProfile) = with(user) {

        userEmail = email
        userProfileImage = profileImage

        binding.firstName.text = firstName
        binding.lastName.text = lastName

        phoneNumber?.apply {
            binding.phone.fullNumber = this
        }

        enterDate(requireContext(), binding.dateOfBirth, birthday)

        clientId?.apply {
            binding.clientId.text = this
        }
    }

    private fun saveUser() {

        hideKeyboard()

        val phone = binding.phone.fullNumber

        val firstName = binding.firstName.text.toString().trim()
        val lastName = binding.lastName.text.toString().trim()

        val birthday = getServerDate(binding.dateOfBirth)

        val clientId = binding.clientId.text.toString().trim()

        val newUser = getEmptyUserProfile().copy(
            email = userEmail,
            phoneNumber = phone,
            firstName = firstName,
            lastName = lastName,
            birthday = birthday,
            clientId = clientId,
            profileImage = userProfileImage
        )

        profileViewModel.updateUserProfile(newUser)
    }

    private fun handleFailure(throwable: Throwable? = null) {

        when (throwable) {

            is NetworkException.NoNetwork -> {
                showMessage(R.string.auth_error_network)
            }

            else -> {
                showMessage(R.string.something_went_wrong)
            }
        }
    }

    private fun finish() {

        findNavController().popBackStack()
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