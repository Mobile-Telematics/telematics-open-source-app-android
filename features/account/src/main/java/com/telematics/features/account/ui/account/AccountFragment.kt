package com.telematics.features.account.ui.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.telematics.core.common.NetworkException
import com.telematics.core.common.dialog.ChooserBottomSheetDialogFragment
import com.telematics.core.common.extension.checkExit
import com.telematics.core.common.extension.exit
import com.telematics.core.common.extension.getMediaPermissions
import com.telematics.core.common.extension.showMessage
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.core.common.fragment.CropFragment
import com.telematics.core.common.fragment.CropFragment.Companion.CROP_FILE_PATH_KEY
import com.telematics.core.common.fragment.CropFragment.Companion.CROP_KEY
import com.telematics.core.common.navigation.AppNavigation
import com.telematics.core.common.navigation.AppNavigationViewModel
import com.telematics.core.common.util.PermissionUtils
import com.telematics.core.common.util.PhotoUtils
import com.telematics.core.model.UserProfile
import com.telematics.core.model.carservice.Vehicle
import com.telematics.features.account.R
import com.telematics.features.account.databinding.FragmentAccountBinding
import com.telematics.features.account.ui.account.vehicle.VehicleFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch


@AndroidEntryPoint
class AccountFragment : BaseFragment(), View.OnClickListener {

    companion object {
        private const val TAG = "AccountFragment"

        private const val DIALOG_TAG = "edit_profile_dialog_tag"
        private const val CHOOSER_REQUEST_KEY =
            "edit_profile_error_request_key"
        private const val DIALOG_RESULT_KEY = "edit_profile_dialog_result_key"
    }

    private val appNavigationViewModel: AppNavigationViewModel by activityViewModels()
    private val accountViewModel: AccountViewModel by viewModels()

    private val permissionUtils = PermissionUtils()

    private lateinit var binding: FragmentAccountBinding

    private val profilePictureName = "profilePicture.png"

    private var simpleMode = false

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            uri?.also {
                PhotoUtils.onSelectFile(this, it, profilePictureName)
            }
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        permissionUtils.registerContract(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        childFragmentManager.setFragmentResultListener(
            CHOOSER_REQUEST_KEY,
            this
        ) { _, bundle ->
            val result = bundle.getString(
                DIALOG_RESULT_KEY,
                ChooserBottomSheetDialogFragment.ChooserMode.CANCEL.name
            )
            when (ChooserBottomSheetDialogFragment.ChooserMode.valueOf(result)) {
                ChooserBottomSheetDialogFragment.ChooserMode.TAKE_PHOTO -> {
                    showCamera()
                }

                ChooserBottomSheetDialogFragment.ChooserMode.CHOOSE_PHOTO -> {
                    showGallery()
                }

                ChooserBottomSheetDialogFragment.ChooserMode.CANCEL -> {

                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setBackPressedAccountCallback()

        setListeners()
        collectUiState()

        observeVehicles()
    }

    fun setBackPressedAccountCallback() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (simpleMode) {
                        appNavigationViewModel.navigateTo(AppNavigation.DashboardScreen)
                    } else if (checkExit()) exit()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun setListeners() {

        binding.profileCard.root.setOnClickListener(this)
        binding.addProfileCard.root.setOnClickListener(this)

        //listener for update picture
        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(
            CROP_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->
            val filePath = result.getString(CROP_FILE_PATH_KEY)
            filePath?.let {
                uploadProfilePic(filePath)
            } ?: run {
                showFilePathError()
            }
        }
    }

    private fun collectUiState() {

        viewLifecycleOwner.lifecycleScope.launch {
            accountViewModel.uiState
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { uiState ->

                    with(uiState) {
                        showProgressBar(isLoading)

                        error?.let { throwable ->
                            handleFailure(throwable)
                            accountViewModel.onErrorHandled()
                        }
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            accountViewModel.getUserProfileFlow()
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { userProfile ->
                    userProfile?.apply {
                        bindUser(this)
                    }
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            appNavigationViewModel.editAvatar
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .collect {
                    askPermissions()
                }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            accountViewModel.simpleModeFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle)
                .distinctUntilChanged()
                .collect { mode ->
                    simpleMode = mode
                }
        }
    }

    private fun showProgressBar(visible: Boolean) {
        binding.progressBar.isVisible = visible
    }

    private fun bindUser(user: UserProfile) = with(user) {

        val userName = fullName

        binding.profileCard.user.text = userName
        //binding.profileCard.user.isVisible = userName.isNotBlank()

        binding.profileCard.clientId.text = clientId
        binding.profileCard.clientId.isVisible = !clientId.isNullOrBlank()

        binding.addProfileCard.root.isVisible = userName.isBlank() && clientId.isNullOrBlank()
        binding.profileCard.root.isVisible = !binding.addProfileCard.root.isVisible

    }

    private fun handleFailure(throwable: Throwable? = null) {

        when (throwable) {

            is NetworkException.NoNetwork -> {
                showMessage(R.string.auth_error_network)
            }

            else -> {
                showMessage(R.string.server_error_something_went_wrong)
            }
        }
    }

    private fun uploadProfilePic(filePath: String) {

        Log.d(TAG, "updateProfilePic: file path $filePath")
        accountViewModel.uploadProfilePicture(filePath)
    }

    private fun askPermissions() {

        permissionUtils.setPermissionListener { allIsGranted ->
            if (allIsGranted) {
                showPickupDialog()
            } else {
                showPermissionError()
            }
        }

        permissionUtils.askPermissions(
            requireActivity(),
            getMediaPermissions()
        )
    }

    private fun showPickupDialog() {

        ChooserBottomSheetDialogFragment.getNewInstance(
            requestKey = CHOOSER_REQUEST_KEY,
            resultKey = DIALOG_RESULT_KEY
        ).show(childFragmentManager, DIALOG_TAG)
    }

    private fun showCamera() {

        PhotoUtils.setCallback(object : PhotoUtils.Callback {
            override fun openCropScreen(fileFrom: String?, fileTo: String?) {
                openCrop(fileFrom, fileTo)
            }

            override fun onError(message: String?) {
                message?.also {
                    showMessage(it)
                }
            }
        })
        PhotoUtils.openCamera(this, profilePictureName)
    }

    private fun showGallery() {

        PhotoUtils.setCallback(object : PhotoUtils.Callback {
            override fun openCropScreen(fileFrom: String?, fileTo: String?) {
                openCrop(fileFrom, fileTo)
            }

            override fun onError(message: String?) {
                message?.also {
                    showMessage(it)
                }
            }
        })
        PhotoUtils.createDir(this, profilePictureName)
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        PhotoUtils.onActivityResult(this, requestCode, resultCode, data)
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun openCrop(fileFrom: String?, fileTo: String?) {

        val bundle = bundleOf(
            CropFragment.CROP_INPUT_FILE_KEY to fileFrom,
            CropFragment.CROP_RESULT_FILE_KEY to fileTo
        )
        findNavController().navigate(R.id.action_accountFragment_to_cropFragment, bundle)
    }

    private fun openProfileFragment() {
        findNavController().navigate(R.id.action_accountFragment_to_profileFragment)
    }

    private fun showPermissionError() {

        val snackBar = Snackbar.make(
            binding.root,
            R.string.account_access_permission_msg,
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction(R.string.retry) {
            askPermissions()
        }
        snackBar.show()
    }

    private fun showFilePathError() {
        showMessage(getString(R.string.something_went_wrong))
    }

    /*vehicles*/
    private fun observeVehicles() {

        accountViewModel.getVehicles().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                bindVehicleList(it)
            }
            result.onFailure {
                showEmptyVehicle(true)
            }
        }

        /*listeners*/
        with(binding.accountVehicles) {
            accountVehicleAddParent.setOnClickListener {
                openAddVehicleFragment()
            }
            accountNewCarDocumentAddIcon.setOnClickListener {
                openAddVehicleFragment()
            }
        }
    }

    private fun bindVehicleList(list: List<Vehicle>) {

        if (list.isEmpty()) {
            showEmptyVehicle(true)
            return
        }

        showEmptyVehicle(false)

        val adapter = VehicleListAdapter(list)
        adapter.setOnClickListener(object : VehicleListAdapter.ClickListeners {
            override fun onItemClick(vehicle: Vehicle, listItemPosition: Int) {
                openVehicleFragment(vehicle)
            }
        })
        with(binding.accountVehicles) {
            recyclerViewAccountVehicleList.layoutManager =
                LinearLayoutManager(requireContext())
            recyclerViewAccountVehicleList.adapter = adapter
        }
    }

    private fun showEmptyVehicle(show: Boolean) = with(binding.accountVehicles) {

        if (show) {
            accountNewCarDocumentAddIcon.isVisible = false
            recyclerViewAccountVehicleList.isVisible = false
            accountVehicleAddParent.isVisible = true
            accountVehicleAddParent.alpha = 0f
            accountVehicleAddParent.animate().setDuration(200).alpha(1f)
                .start()
        } else {
            accountVehicleAddParent.isVisible = false
            recyclerViewAccountVehicleList.isVisible = true
            accountNewCarDocumentAddIcon.isVisible = true
            recyclerViewAccountVehicleList.alpha = 0f
            recyclerViewAccountVehicleList.animate().setDuration(300).alpha(1f)
            accountNewCarDocumentAddIcon.alpha = 0f
            accountNewCarDocumentAddIcon.animate().setDuration(200).alpha(1f)
        }
    }

    private fun openAddVehicleFragment() {

        openVehicleFragment(null)
    }

    private fun openVehicleFragment(vehicle: Vehicle?) {

        val bundle = bundleOf(
            VehicleFragment.VEHICLE_FRAGMENT_VEHICLE_KEY to vehicle
        )
        findNavController().navigate(R.id.action_accountFragment_to_vehicleFragment, bundle)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.profileCard.root.id,
            binding.addProfileCard.root.id -> {
                openProfileFragment()
            }
        }
    }
}