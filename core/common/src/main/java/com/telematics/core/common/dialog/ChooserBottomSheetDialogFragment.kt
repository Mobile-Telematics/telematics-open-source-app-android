package com.telematics.core.common.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.telematics.core.common.R
import com.telematics.core.common.databinding.FragmentDialogChooserBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooserBottomSheetDialogFragment :
    BottomSheetDialogFragment(),
    View.OnClickListener {

    private lateinit var binding: FragmentDialogChooserBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentDialogChooserBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        setStyle(STYLE_NO_FRAME, R.style.BottomSheetDialog)
        val dialog = super.onCreateDialog(savedInstanceState)
        if (dialog is BottomSheetDialog) {
            dialog.behavior.skipCollapsed = true
            dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
            dialog.setCanceledOnTouchOutside(true)
        }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        binding.takePhotoButton.setOnClickListener(this)
        binding.choosePhotoButton.setOnClickListener(this)
        binding.cancelButton.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            binding.takePhotoButton.id -> {
                setResult(ChooserMode.TAKE_PHOTO)
            }

            binding.choosePhotoButton.id -> {
                setResult(ChooserMode.CHOOSE_PHOTO)
            }

            binding.cancelButton.id -> {
                setResult(ChooserMode.CANCEL)
            }
        }
    }

    private fun setResult(result: ChooserMode) {
        val requestKey = arguments?.getString(EXTRA_REQUEST_KEY) ?: EXTRA_REQUEST_KEY
        val resultKey = arguments?.getString(EXTRA_RESULT_KEY) ?: EXTRA_RESULT_KEY
        setFragmentResult(requestKey, bundleOf(resultKey to result.name))
        dismiss()
    }

    companion object {
        private const val EXTRA_REQUEST_KEY = "extra_request_key"
        private const val EXTRA_RESULT_KEY = "extra_result_key"

        fun getNewInstance(
            requestKey: String,
            resultKey: String
        ): ChooserBottomSheetDialogFragment {
            return bundleOf(
                EXTRA_REQUEST_KEY to requestKey,
                EXTRA_RESULT_KEY to resultKey
            ).let {
                val fragment = ChooserBottomSheetDialogFragment()
                fragment.arguments = it
                fragment
            }
        }
    }

    enum class ChooserMode {
        TAKE_PHOTO,
        CHOOSE_PHOTO,
        CANCEL
    }
}