package ui.step3_photo

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.telematics.core.common.extension.showMessage
import com.telematics.core.common.extension.systemBarsAndDisplayCutout
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.features.obd.R
import com.telematics.features.obd.databinding.FragmentObdConfirmPhotoBinding
import dagger.hilt.android.AndroidEntryPoint
import ui.CommonObdViewModel
import ui.ObdViewModel

@AndroidEntryPoint
class ConfirmPhotoFragment : BaseFragment() {

    private val obdViewModel: ObdViewModel by viewModels()
    private val commonObdViewModel: CommonObdViewModel by activityViewModels()

    private lateinit var binding: FragmentObdConfirmPhotoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentObdConfirmPhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            applyInsets(insets)
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack(R.id.vehicleWizardFragment, false)
                }
            })

        binding.confirmButton.setOnClickListener {
            uploadPhoto()
        }

        observeImage()
        setBackPressedCallback()
    }

    private fun uploadPhoto() {

        loading(true)
        obdViewModel.uploadOdometerPhoto("").observe(viewLifecycleOwner) { result ->
            loading(false)
            result.onSuccess {
                nextStep()
            }
            result.onFailure {
                showMessage("Error occurred")
            }

        }
    }

    private fun observeImage() {

        val imagePath = commonObdViewModel.getImagePath()
        binding.odometerImage.setImageURI(Uri.parse(imagePath))
    }

    private fun loading(show: Boolean = true) {
        if (show) {
            binding.confirmPhotoProgressBar.visibility = View.VISIBLE
            binding.confirmButton.isEnabled = false
        } else {
            binding.confirmPhotoProgressBar.visibility = View.INVISIBLE
            binding.confirmButton.isEnabled = true
        }
    }

    private fun nextStep() {

        findNavController().navigate(R.id.action_confirmPhotoFragment_to_deviceConnectFragment)
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
