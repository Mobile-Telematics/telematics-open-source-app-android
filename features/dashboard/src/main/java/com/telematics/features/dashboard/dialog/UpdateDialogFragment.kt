package com.telematics.features.dashboard.dialog

import android.R.color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.telematics.core.common.NetworkException
import com.telematics.core.common.extension.drawable
import com.telematics.core.common.extension.hideKeyboard
import com.telematics.core.common.extension.showConfirmationDialog
import com.telematics.core.common.extension.showMessage
import com.telematics.core.content.R
import com.telematics.features.dashboard.databinding.FragmentDialogReAuthBinding
import com.telematics.features.dashboard.databinding.FragmentDialogUpdateBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@AndroidEntryPoint
class UpdateDialogFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentDialogUpdateBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(color.transparent)

        isCancelable = false

        binding = FragmentDialogUpdateBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.updateBtn.setOnClickListener(this)
    }

    private fun setResult(result: Boolean) {
        val requestKey = arguments?.getString(EXTRA_REQUEST_KEY) ?: EXTRA_REQUEST_KEY
        val resultKey = arguments?.getString(EXTRA_RESULT_KEY) ?: EXTRA_RESULT_KEY
        setFragmentResult(requestKey, bundleOf(resultKey to result))
        dismiss()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.updateBtn.id -> {
                setResult(true)
            }
        }
    }

    companion object {
        private const val EXTRA_REQUEST_KEY = "extra_request_key"
        private const val EXTRA_RESULT_KEY = "extra_result_key"

        fun getNewInstance(
            requestKey: String,
            resultKey: String
        ): UpdateDialogFragment {
            return bundleOf(
                EXTRA_REQUEST_KEY to requestKey,
                EXTRA_RESULT_KEY to resultKey
            ).let {
                val fragment = UpdateDialogFragment()
                fragment.arguments = it
                fragment
            }
        }
    }
}