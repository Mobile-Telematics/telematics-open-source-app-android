package com.telematics.core.common.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.telematics.core.common.databinding.FragmentDialogAttentionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AttentionDialogFragment : DialogFragment(), View.OnClickListener {

    private lateinit var binding: FragmentDialogAttentionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        isCancelable = true

        binding = FragmentDialogAttentionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nextButton.setOnClickListener(this)
        binding.closeButton.setOnClickListener(this)

        arguments?.apply {
            binding.icon.setImageResource(getInt(EXTRA_ICON_KEY, -1))
            binding.title.text = getString(EXTRA_TITLE_KEY)
            binding.description.text = getString(EXTRA_DESCRIPTION_KEY)
            binding.nextButton.text = getString(EXTRA_NEXT_KEY)
            binding.closeButton.text = getString(EXTRA_CLOSE_KEY)
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
            binding.nextButton.id -> {
                setResult(true)
            }

            binding.closeButton.id -> {
                setResult(false)
            }
        }
    }

    companion object {
        private const val EXTRA_REQUEST_KEY = "extra_request_key"
        private const val EXTRA_RESULT_KEY = "extra_result_key"
        private const val EXTRA_DESCRIPTION_KEY = "extra_description_key"
        private const val EXTRA_TITLE_KEY = "extra_title_key"
        private const val EXTRA_ICON_KEY = "extra_icon_key"
        private const val EXTRA_NEXT_KEY = "extra_next_key"
        private const val EXTRA_CLOSE_KEY = "extra_close_key"

        fun getNewInstance(
            title: String,
            description: String,
            iconId: Int,
            nextButton: String,
            closeButton: String,
            requestKey: String,
            resultKey: String
        ): AttentionDialogFragment {
            return bundleOf(
                EXTRA_TITLE_KEY to title,
                EXTRA_DESCRIPTION_KEY to description,
                EXTRA_ICON_KEY to iconId,
                EXTRA_NEXT_KEY to nextButton,
                EXTRA_CLOSE_KEY to closeButton,
                EXTRA_REQUEST_KEY to requestKey,
                EXTRA_RESULT_KEY to resultKey
            ).let {
                val fragment = AttentionDialogFragment()
                fragment.arguments = it
                fragment
            }
        }
    }
}