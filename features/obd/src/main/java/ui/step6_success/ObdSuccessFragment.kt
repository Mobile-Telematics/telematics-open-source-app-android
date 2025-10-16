package ui.step6_success

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import com.telematics.core.common.extension.systemBarsAndDisplayCutout
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.features.obd.R
import com.telematics.features.obd.databinding.FragmentOdbCongratulationsBinding
import dagger.hilt.android.AndroidEntryPoint
import ui.CommonObdViewModel

@AndroidEntryPoint
class ObdSuccessFragment : BaseFragment() {

    //private val obdViewModel: ObdViewModel by viewModels()
    private val commonObdViewModel: CommonObdViewModel by activityViewModels()

    private lateinit var binding: FragmentOdbCongratulationsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOdbCongratulationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            applyInsets(insets)
        }

        val vehicleToken = commonObdViewModel.getConnectedDevice()?.first ?: ""
        val elmMac = commonObdViewModel.getConnectedDevice()?.second ?: ""

        binding.deviceNumberText.text = getString(R.string.odb_device_number, vehicleToken)
        binding.policyNumberText.text = getString(R.string.odb_policy_number, elmMac)
        binding.finishButton.setOnClickListener {
            onBackPressed()
        }
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
