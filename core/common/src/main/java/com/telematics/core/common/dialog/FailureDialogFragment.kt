package com.telematics.core.common.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.telematics.core.common.databinding.FragmentDialogFailureBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FailureDialogFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentDialogFailureBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        isCancelable = true

        binding = FragmentDialogFailureBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeButton.setOnClickListener(this)
        binding.description.text = arguments?.getString(EXTRA_DESCRIPTION_KEY)
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

        fun getNewInstance(
            description: String,
            requestKey: String,
            resultKey: String
        ): FailureDialogFragment {
            return bundleOf(
                EXTRA_DESCRIPTION_KEY to description,
                EXTRA_REQUEST_KEY to requestKey,
                EXTRA_RESULT_KEY to resultKey
            ).let {
                val fragment = FailureDialogFragment()
                fragment.arguments = it
                fragment
            }
        }
    }
}