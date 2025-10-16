package com.telematics.core.common.fragment

import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.telematics.core.common.extension.checkExit
import com.telematics.core.common.extension.exit

abstract class BaseFragment : Fragment() {

    fun setBackPressedExitCallback() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (checkExit()) exit()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    fun setBackPressedCallback() {
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().popBackStack()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    fun onBackPressed() {
        findNavController().popBackStack()
    }

    private fun showAnswerDialog(
        title: String,
        onPositive: (() -> Unit)?,
        onNegative: (() -> Unit)? = null
    ) {

        AlertDialog.Builder(requireContext()).apply {
            setPositiveButton(com.telematics.core.content.R.string.dialog_yes) { d, _ ->
                onPositive?.let {
                    onPositive()
                }
                d.dismiss()
            }
            setNegativeButton(com.telematics.core.content.R.string.dialog_no) { d, _ ->
                onNegative?.invoke()
                d.dismiss()
            }
            setCancelable(true)
            setTitle(title)
        }.show()
    }

    fun showAnswerDialog(
        @StringRes stringRes: Int,
        onPositive: (() -> Unit)?,
        onNegative: (() -> Unit)? = null
    ) {

        showAnswerDialog(getString(stringRes), onPositive, onNegative)
    }

}