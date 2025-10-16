package com.telematics.features.dashboard.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.os.bundleOf
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.telematics.core.model.TripRecordMode
import com.telematics.features.dashboard.databinding.FragmentDialogTripRecordModeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TripRecordModeDialogFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentDialogTripRecordModeBinding

    private var currentMode: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        isCancelable = true

        binding = FragmentDialogTripRecordModeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentMode =
            arguments?.getString(EXTRA_TRIP_RECORD_MODE_KEY, TripRecordMode.ALWAYS_ON.name)

        with(binding) {
            alwaysOn.tag = TripRecordMode.ALWAYS_ON.name
            shiftMode.tag = TripRecordMode.SHIFT_MODE.name
            onDemand.tag = TripRecordMode.ON_DEMAND.name
            disabled.tag = TripRecordMode.DISABLED.name
        }

        binding.tripTypeRadioGroup.children.find {
            it.tag == currentMode
        }?.apply {
            (this as RadioButton).isChecked = true
        }

        binding.tripTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            binding.okBtn.isEnabled = binding.tripTypeRadioGroup.findViewById<RadioButton>(
                checkedId
            )?.let { it.tag != currentMode } ?: false
        }

        binding.okBtn.setOnClickListener(this)
        binding.cancelBtn.setOnClickListener(this)
    }

    private fun setResult(result: String?) {
        val requestKey = arguments?.getString(EXTRA_REQUEST_KEY) ?: EXTRA_REQUEST_KEY
        val resultKey = arguments?.getString(EXTRA_RESULT_KEY) ?: EXTRA_RESULT_KEY
        setFragmentResult(requestKey, bundleOf(resultKey to result))
        dismiss()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.okBtn.id -> {

                val checkedRadioButtonId = binding.tripTypeRadioGroup.checkedRadioButtonId

                val selectedMode = binding.tripTypeRadioGroup.findViewById<RadioButton>(
                    checkedRadioButtonId
                )?.tag as String?

                setResult(selectedMode)
            }

            binding.cancelBtn.id -> {
                dismiss()
            }
        }
    }

    companion object {
        private const val EXTRA_TRIP_RECORD_MODE_KEY = "extra_trip_record_mode_key"
        private const val EXTRA_REQUEST_KEY = "extra_request_key"
        private const val EXTRA_RESULT_KEY = "extra_result_key"

        fun getNewInstance(
            currentMode: TripRecordMode?,
            requestKey: String,
            resultKey: String
        ): TripRecordModeDialogFragment {
            return bundleOf(
                EXTRA_TRIP_RECORD_MODE_KEY to currentMode?.name,
                EXTRA_REQUEST_KEY to requestKey,
                EXTRA_RESULT_KEY to resultKey
            ).let {
                val fragment = TripRecordModeDialogFragment()
                fragment.arguments = it
                fragment
            }
        }
    }
}