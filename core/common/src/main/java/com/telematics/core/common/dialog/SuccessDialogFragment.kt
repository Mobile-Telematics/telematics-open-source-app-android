package com.telematics.core.common.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.telematics.core.common.R
import com.telematics.core.common.databinding.FragmentDialogSuccessBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SuccessDialogFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentDialogSuccessBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        isCancelable = true

        binding = FragmentDialogSuccessBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeButton.setOnClickListener(this)
        arguments?.apply {
            binding.description.text = getString(EXTRA_DESCRIPTION_KEY)
            binding.closeButton.text =
                getString(EXTRA_BUTTON_KEY, getString(R.string.back_to_profile))
        }

    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        setResult(false)
    }

    private fun setResult(result: Boolean) {
        val requestKey = arguments?.getString(EXTRA_REQUEST_KEY) ?: EXTRA_REQUEST_KEY
        val resultKey = arguments?.getString(EXTRA_RESULT_KEY) ?: EXTRA_RESULT_KEY
        setFragmentResult(requestKey, bundleOf(resultKey to result))
        dismiss()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.closeButton.id -> {
                setResult(true)
            }
        }
    }

    companion object {
        private const val EXTRA_REQUEST_KEY = "extra_request_key"
        private const val EXTRA_RESULT_KEY = "extra_result_key"
        private const val EXTRA_DESCRIPTION_KEY = "extra_description_key"
        private const val EXTRA_BUTTON_KEY = "extra_button_key"

        fun getNewInstance(
            description: String,
            button: String,
            requestKey: String,
            resultKey: String
        ): SuccessDialogFragment {
            return bundleOf(
                EXTRA_DESCRIPTION_KEY to description,
                EXTRA_BUTTON_KEY to button,
                EXTRA_REQUEST_KEY to requestKey,
                EXTRA_RESULT_KEY to resultKey
            ).let {
                val fragment = SuccessDialogFragment()
                fragment.arguments = it
                fragment
            }
        }
    }
}