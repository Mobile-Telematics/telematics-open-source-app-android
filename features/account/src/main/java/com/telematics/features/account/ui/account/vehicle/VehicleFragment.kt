package com.telematics.features.account.ui.account.vehicle

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.telematics.core.common.extension.getSerializableCompat
import com.telematics.core.common.extension.hideKeyboard
import com.telematics.core.common.extension.showMessage
import com.telematics.core.common.extension.systemBarsInsets
import com.telematics.core.common.fragment.BaseFragment
import com.telematics.core.model.carservice.Vehicle
import com.telematics.features.account.R
import com.telematics.features.account.databinding.FragmentVehicleBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class VehicleFragment : BaseFragment() {

    companion object {
        const val VEHICLE_FRAGMENT_VEHICLE_KEY = "VEHICLE_FRAGMENT_VEHICLE_KEY"
    }

    private val vehicleViewModel: VehicleViewModel by viewModels()
    private var inputVehicle = Vehicle()
    private var isNewVehicle = true
    private var isCanChangeMileage = true
    private var currentManufacturerId = -1
    private var currentModelId = -1

    private lateinit var binding: FragmentVehicleBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVehicleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setBackPressedCallback()
        setInset()

        arguments?.getSerializableCompat(
            VEHICLE_FRAGMENT_VEHICLE_KEY,
            Vehicle::class.java
        )?.also {
            inputVehicle = it
            isNewVehicle = false
            isCanChangeMileage = inputVehicle.initialMileage.isNullOrBlank()
            getManufacturerId()
        }
        bindVehicle(inputVehicle)

        binding.toolbar.menu.findItem(R.id.nav_delete).isVisible = !isNewVehicle

        setListeners()
    }

    private fun setListeners() {

        binding.createButton.setOnClickListener {
            saveVehicle()
        }

        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.nav_delete -> {
                    showAnswerDialog(R.string.are_you_sure,
                        {
                            deleteVehicle()
                        }
                    )
                }
            }
            true
        }

        binding.vehicleInitialMileage.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                checkMileageEdit()
            }
        }

        binding.vehicleInitialMileage.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                hideKeyboard()
            }
            true
        }

        binding.vehicleInitialMileage.setOnClickListener {
            checkMileageEdit()
        }

        binding.vehicleManufacturer.setOnClickListener {
            observeManufacturers()
        }

        binding.vehicleModel.setOnClickListener {
            observeModels(currentManufacturerId)
        }

        binding.vehicleModelChoose.setOnReturnData(object : VehicleChooseView.OnReturnData {
            override fun onReturn(id: Int, name: String, type: VehicleChooseView.Type?) {
                binding.vehicleModelChoose.hide()
                when (type) {
                    VehicleChooseView.Type.MANUFACTURER -> {
                        if (currentManufacturerId != id) {
                            currentModelId = -1
                            binding.vehicleModel.setText("")
                        }

                        currentManufacturerId = id
                        binding.vehicleManufacturer.setText(name)
                    }

                    VehicleChooseView.Type.MODEL -> {
                        currentModelId = id
                        binding.vehicleModel.setText(name)
                    }

                    else -> {}
                }
            }
        })

        binding.vehicleCarYear.doOnTextChanged { text, _, _, _ ->

            if (text?.isEmpty() == true) {
                return@doOnTextChanged
            }

            val year =
                if (text.toString().isNotBlank())
                    try {
                        text.toString().toInt()
                    } catch (e: Exception) {
                        null
                    }
                else -1

            if (year == null) {
                binding.vehicleCarYear.error = "Invalid year"
            } else {
                when {
                    year > Calendar.getInstance().get(Calendar.YEAR) -> {
                        binding.vehicleCarYear.error = "Invalid year"
                    }

                    year <= 0 -> {
                        binding.vehicleCarYear.error = "Invalid year"
                    }
                }
            }
        }

        binding.vehicleModelChoose.hide()
    }


    private fun saveVehicle() {

        val vehicle = getUpdatedVehicle()
        if (!checkFields(vehicle)) {
            return
        }

        showProgress(true)

        val liveData =
            if (isNewVehicle)
                vehicleViewModel.createVehicle(vehicle)
            else
                vehicleViewModel.updateVehicle(vehicle)

        liveData.observe(viewLifecycleOwner) { result ->
            showProgress(false)
            result.onSuccess {
                finish()
            }
            result.onFailure {
                showMessage(R.string.server_error_something_went_wrong)
            }
        }
    }

    private fun getUpdatedVehicle(): Vehicle {

        inputVehicle.plateNumber = binding.vehicleLicencePlate.text.toString()
        inputVehicle.vin = binding.vehicleVinNumber.text.toString()
        inputVehicle.manufacturer = binding.vehicleManufacturer.text.toString()
        inputVehicle.manufacturerId = currentManufacturerId
        inputVehicle.model = binding.vehicleModel.text.toString()
        inputVehicle.modelId = currentModelId
        inputVehicle.name = binding.vehicleCarName.text.toString()

        val year =
            if (binding.vehicleCarYear.text.toString().isNotBlank())
                try {
                    binding.vehicleCarYear.text.toString().toInt()
                } catch (e: Exception) {
                    null
                }
            else -1

        if (year == null) {
            binding.vehicleCarYear.error = "Invalid year"
        } else
            inputVehicle.carYear = year

        if (isCanChangeMileage)
            inputVehicle.initialMileage = binding.vehicleInitialMileage.text.toString()
        else
            inputVehicle.initialMileage = null

        return inputVehicle
    }

    private fun deleteVehicle() {

        showProgress(true)
        vehicleViewModel.deleteVehicle(inputVehicle).observe(viewLifecycleOwner) { result ->

            showProgress(false)
            result.onSuccess {
                finish()
            }
            result.onFailure {
                showMessage(R.string.server_error_something_went_wrong)
            }
        }
    }

    private fun observeManufacturers() {

        binding.vehicleManufacturer.error = null

        vehicleViewModel.getManufacturers().observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                binding.vehicleModelChoose.setManufacturers(it)
                binding.vehicleModelChoose.show()
            }
        }
    }

    private fun observeModels(id: Int) {

        if (currentManufacturerId == 0 || currentManufacturerId == -1) {
            binding.vehicleManufacturer.error = "Select the manufacturer"
            return
        }

        vehicleViewModel.getModels(id).observe(viewLifecycleOwner) { result ->
            result.onSuccess {
                binding.vehicleModelChoose.setModels(it)
                binding.vehicleModelChoose.show()
            }
        }
    }

    private fun getManufacturerId() {

        if (isNewVehicle) return

        fun getModelId(id: Int) {
            vehicleViewModel.getModels(id).observe(viewLifecycleOwner) { result ->
                result.onSuccess { list ->
                    list.find { it.name.equals(inputVehicle.model, true) }?.let {
                        inputVehicle.modelId = it.id
                        currentModelId = it.id
                    }
                }
            }
        }

        vehicleViewModel.getManufacturers().observe(viewLifecycleOwner) { result ->
            result.onSuccess { list ->
                list.find { it.name.equals(inputVehicle.manufacturer, true) }?.let {
                    inputVehicle.manufacturerId = it.id
                    currentManufacturerId = it.id
                    getModelId(it.id)
                }
            }
        }

    }

    private fun bindVehicle(vehicle: Vehicle?) {

        if (vehicle == null) return

        binding.vehicleLicencePlate.setText(vehicle.plateNumber ?: "")
        binding.vehicleVinNumber.setText(vehicle.vin ?: "")
        binding.vehicleManufacturer.setText(vehicle.manufacturer ?: "")
        val modelText =
            if (vehicle.model?.lowercase(Locale.ROOT) == "no option") "" else vehicle.model ?: ""
        binding.vehicleModel.setText(modelText)
        binding.vehicleCarName.setText(vehicle.name ?: "")

        if (vehicle.carYear != -1)
            binding.vehicleCarYear.setText(vehicle.carYear?.toString())

        vehicle.initialMileage?.let {
            binding.vehicleInitialMileage.setText(vehicle.initialMileage)
        }

        currentManufacturerId = vehicle.manufacturerId ?: -1
        currentModelId = vehicle.modelId ?: -1

        currentModelId
        currentManufacturerId
    }

    private fun showProgress(show: Boolean) {

        binding.progressBar.isVisible = show
    }

    private fun checkFields(vehicle: Vehicle): Boolean {

        var result = true

        binding.vehicleVinNumber.error = null
        binding.vehicleManufacturer.error = null
        binding.vehicleCarYear.error = null

        val vin = vehicle.vin?.replace(" ", "")
        binding.vehicleVinNumber.setText(vin)
        vehicle.vin = vin
        if (!vehicle.vin.isNullOrBlank() && vehicle.vin?.length != 17) {
            binding.vehicleVinNumber.error = "VIN must have length of 17 symbols"
            result = false
        }
        if (vehicle.manufacturer.isNullOrEmpty()) {
            binding.vehicleManufacturer.error = "Select the manufacturer"
            result = false
        }
        when {
            vehicle.carYear != null
                    && (vehicle.carYear ?: 0) > Calendar.getInstance().get(Calendar.YEAR) -> {
                binding.vehicleCarYear.error = "Invalid year"
                result = false
            }

            vehicle.carYear != null && (vehicle.carYear ?: 0) <= 0 && vehicle.carYear != -1 -> {
                binding.vehicleCarYear.error = "Invalid year"
                result = false
            }
        }

        return result
    }

    private fun checkMileageEdit() {

        if (!isCanChangeMileage) {
            hideKeyboard()
            showMessage("Mileage can't be changed after adding a car")
            binding.vehicleInitialMileage.isEnabled = false
            binding.vehicleInitialMileage.clearFocus()
            binding.root.clearFocus()
            binding.root.hasFocus()
            Handler(Looper.getMainLooper()).postDelayed({
                binding.vehicleInitialMileage.isEnabled = true
            }, 500)
        }
    }

    private fun finish() {
        onBackPressed()
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