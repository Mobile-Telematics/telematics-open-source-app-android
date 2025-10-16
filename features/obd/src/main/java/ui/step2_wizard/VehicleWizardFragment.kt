package ui.step2_wizard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.telematics.core.common.dialog.ChooserBottomSheetDialogFragment
import com.telematics.core.common.extension.getMediaPermissions
import com.telematics.core.common.extension.showMessage
import com.telematics.core.common.extension.systemBarsAndDisplayCutout
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.core.common.fragment.CropFragment
import com.telematics.core.common.fragment.CropFragment.Companion.CROP_FILE_PATH_KEY
import com.telematics.core.common.fragment.CropFragment.Companion.CROP_KEY
import com.telematics.core.common.util.PhotoUtils
import com.telematics.features.obd.R
import com.telematics.features.obd.databinding.FragmentObdVehicleWizardBinding
import dagger.hilt.android.AndroidEntryPoint
import ui.CommonObdViewModel

@AndroidEntryPoint
class VehicleWizardFragment : BaseFragment() {
    companion object {

        private const val DIALOG_TAG = "vehicle_wizard_dialog_tag"
        private const val CHOOSER_REQUEST_KEY =
            "vehicle_wizard_error_request_key"
        private const val DIALOG_RESULT_KEY = "vehicle_wizard_dialog_result_key"
    }

    private val permissionUtils = com.telematics.core.common.util.PermissionUtils()
    private val odometerPictureName = "odometerPicture.png"

    private lateinit var binding: FragmentObdVehicleWizardBinding

    private val commonObdViewModel: CommonObdViewModel by activityViewModels()

    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            // Callback is invoked after the user selects a media item or closes the
            // photo picker.
            uri?.also {
                PhotoUtils.onSelectFile(this, it, odometerPictureName)
            }
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
        binding = FragmentObdVehicleWizardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            applyInsets(insets)
        }

        binding.obdWizardViewPager.adapter = ObdWizardPagerAdapter().apply {
            setOnClick(object : ObdWizardPagerAdapter.OnClick {
                override fun onClickNext() {
                    askPermissions()
                }
            })
        }
        binding.obdPagesIndicator.setViewPager(binding.obdWizardViewPager)

        findNavController().currentBackStackEntry?.savedStateHandle?.getLiveData<Bundle>(
            CROP_KEY
        )?.observe(
            viewLifecycleOwner
        ) { result ->
            val filePath = result.getString(CROP_FILE_PATH_KEY)

            filePath?.let {
                findNavController().currentBackStackEntry?.savedStateHandle?.remove<Bundle>(CROP_KEY)
                commonObdViewModel.setOdometerImage(it)
                findNavController().navigate(R.id.action_vehicleWizardFragment_to_confirmPhotoFragment)
            } ?: run {
                showFilePathError()
            }
        }
    }

    private fun showFilePathError() {
        showMessage(getString(R.string.something_went_wrong))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        permissionUtils.registerContract(this)
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
        PhotoUtils.openCamera(this, odometerPictureName)
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
        PhotoUtils.createDir(this, odometerPictureName)
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        PhotoUtils.onActivityResult(
            this,
            requestCode,
            resultCode,
            data
        )
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun openCrop(fileFrom: String?, fileTo: String?) {

        val bundle = bundleOf(
            CropFragment.CROP_INPUT_FILE_KEY to fileFrom,
            CropFragment.CROP_RESULT_FILE_KEY to fileTo,
            CropFragment.CROP_BOTTOM_PADDING_ENABLED_KEY to true,
            CropFragment.CROP_ASPECT_RATIO_X_KEY to 16,
            CropFragment.CROP_ASPECT_RATIO_Y_KEY to 9,
        )
        findNavController().navigate(R.id.action_vehicleWizardFragment_to_cropPhotoFragment, bundle)
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

    private fun applyInsets(windowInsets: WindowInsetsCompat): WindowInsetsCompat {
        val insetTypeMask = systemBarsAndDisplayCutout()

        val insets = windowInsets.getInsets(insetTypeMask)

        binding.root.updatePadding(top = insets.top, bottom = insets.bottom)

        return WindowInsetsCompat.Builder()
            .setInsets(insetTypeMask, insets)
            .build()
    }
}
