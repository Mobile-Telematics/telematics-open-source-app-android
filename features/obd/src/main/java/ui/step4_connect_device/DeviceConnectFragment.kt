package ui.step4_connect_device

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.telematics.core.common.extension.showMessage
import com.telematics.core.common.extension.systemBarsAndDisplayCutout
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.core.model.tracking.ElmDevice
import com.telematics.features.obd.R
import com.telematics.features.obd.databinding.FragmentObdDeviceConnectBinding
import dagger.hilt.android.AndroidEntryPoint
import ui.CommonObdViewModel
import ui.ObdViewModel

@AndroidEntryPoint
class DeviceConnectFragment : BaseFragment() {

    private val obdViewModel: ObdViewModel by viewModels()
    private val commonObdViewModel: CommonObdViewModel by activityViewModels()

    private lateinit var binding: FragmentObdDeviceConnectBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentObdDeviceConnectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            applyInsets(insets)
        }
        setBackPressedCallback()

        binding.obdDeviceConnectSearchLayout.takingTooLongText.setOnClickListener {
            val browserIntent =
                Intent(Intent.ACTION_VIEW, Uri.parse(resources.getString(R.string.obd_help_link)))
            startActivity(browserIntent)
        }
        binding.obdDeviceConnectSearchLayout.root.setOnClickListener { }

        registerEmlManagerLinkingResult()
        initPager()
    }

    private fun initPager() {

        binding.obdDeviceConnectViewPager.adapter = DeviceConnectedWizardAdapter().apply {
            setOnClick(object : DeviceConnectedWizardAdapter.OnSearchClickListener {
                override fun search() {
                    startScan()
                }
            })
        }
        binding.obdDeviceConnectPagesIndicator.setViewPager(binding.obdDeviceConnectViewPager)
    }

    private fun registerEmlManagerLinkingResult() {

        obdViewModel.registerElmManagerLinkingResult().observe(viewLifecycleOwner) { result ->
            result.onSuccess { elmManagerLinkingResult ->
                if (elmManagerLinkingResult?.isScanningComplete == true) {
                    showFoundDevices(elmManagerLinkingResult.foundDevices)
                }
                if (elmManagerLinkingResult?.error != null) {
                    handleError(elmManagerLinkingResult.error)
                }
            }
            result.onFailure {
                handleError(null)
            }
        }
    }

    private fun startScan() {

        if (checkBLE()) {
            showSearchOverlay(true)
            obdViewModel.getElmDevices()
        }
    }

    @SuppressLint("MissingPermission")
    private fun checkBLE(): Boolean {

        if (!requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            showMessage(R.string.obd_ble_error)
            finish()
        }
        val mBluetoothAdapter = obdViewModel.getBluetoothAdapter(requireContext())
        mBluetoothAdapter?.let {
            if (!mBluetoothAdapter.isEnabled) {
                val intent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                @Suppress("DEPRECATION")
                startActivityForResult(intent, obdViewModel.getRequestBluetoothEnableCode())
                return false
            }
        }
        return true
    }

    private fun showSearchOverlay(show: Boolean) {

        binding.obdDeviceConnectSearchLayout.root.isVisible = show
    }

    private fun showFoundDevices(devices: List<ElmDevice>?) {

        showSearchOverlay(false)
        devices?.let {
            commonObdViewModel.setFoundedDevices(devices)
        }

        findNavController().navigate(R.id.action_deviceConnectFragment_to_foundedDevicesFragment)
    }

    private fun handleError(error: String?) {

        when (error) {
            "SERVER_ERROR_NETWORK_CONNECTION_NOT_AVAILABLE",
            "SERVER_ERROR_UNKNOWN" ->
                showMessage(R.string.obd_internet_error)

            "VEHICLE_NOT_SUPPORTED" ->
                findNavController().navigate(R.id.action_deviceConnectFragment_to_vehicleNotSupportedFragment)

            else ->
                findNavController().navigate(R.id.action_deviceConnectFragment_to_couldNotConnectFragment)

        }
    }

    private fun finish() {

        findNavController().navigateUp()
    }

    private fun applyInsets(windowInsets: WindowInsetsCompat): WindowInsetsCompat {
        val insetTypeMask = systemBarsAndDisplayCutout()

        val insets = windowInsets.getInsets(insetTypeMask)

        binding.root.updatePadding(top = insets.top, bottom = insets.bottom)

        return WindowInsetsCompat.Builder()
            .setInsets(insetTypeMask, insets)
            .build()
    }

    /* private fun _setInset() {
         binding.toolbar.setOnApplyWindowInsetsListener { view, insets ->
             val systemBarsInsets = systemBarsInsets(insets)
             val params = view.layoutParams as ViewGroup.MarginLayoutParams

             params.updateMargins(
                 top = systemBarsInsets.top
             )

             insets
         }
     }*/
}
