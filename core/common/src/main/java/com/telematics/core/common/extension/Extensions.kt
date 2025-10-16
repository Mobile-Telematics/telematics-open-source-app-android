package com.telematics.core.common.extension

import android.app.ActivityManager
import android.content.Context
import android.content.res.Resources
import android.util.Patterns
import android.view.View
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.getSystemService
import androidx.core.graphics.Insets
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.telematics.core.common.R
import com.telematics.core.common.databinding.DialogConfirmationBinding
import com.telematics.core.common.extension.Extension.backPressedTime

fun String.isValidEmail(): Boolean {
    return this.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun systemBarsInsets(insets: WindowInsets): Insets {
    return WindowInsetsCompat.toWindowInsetsCompat(insets)
        .getInsets(WindowInsetsCompat.Type.systemBars())
}

fun systemBarsAndDisplayCutout(): Int {
    return WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
}

fun ComponentActivity.hideStatusBar() {
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    insetsController.hide(WindowInsetsCompat.Type.statusBars())
}

fun ComponentActivity.showStatusBar() {
    val insetsController = WindowCompat.getInsetsController(window, window.decorView)
    insetsController.show(WindowInsetsCompat.Type.statusBars())
}

val Int.pxToDp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()
val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun getMemoryInfo(context: Context): ActivityManager.MemoryInfo {
    val activityManager = context.getSystemService<ActivityManager>()
    return ActivityManager.MemoryInfo().also { memoryInfo ->
        activityManager?.getMemoryInfo(memoryInfo)
    }
}

object Extension {
    var backPressedTime: Long = 0
}

fun Fragment.checkExit(): Boolean =
    if (backPressedTime + 2000 > System.currentTimeMillis()) {
        true
    } else {
        Toast.makeText(
            requireContext(),
            getString(R.string.message_double_back_press),
            Toast.LENGTH_LONG
        ).show()

        backPressedTime = System.currentTimeMillis()
        false
    }

fun Fragment.exit() {
    requireActivity().finishAffinity()
}

fun Fragment.showConfirmationDialog(
    message: String,
    action: () -> Unit,
    buttonText: String,
    arrowEnabled: Boolean
) {
    val dialogConfirmationBinding = DialogConfirmationBinding.inflate(layoutInflater).apply {
        description.text = message
        button.text = buttonText
        if (!arrowEnabled) {
            button.setCompoundDrawablesRelativeWithIntrinsicBounds(
                null,
                null,
                null,
                null
            )
        }
    }

    AlertDialog.Builder(requireContext())
        .setView(dialogConfirmationBinding.root)
        .setCancelable(true)
        .show()
        .apply {
            dialogConfirmationBinding.button.setOnClickListener {
                action()
                cancel()
            }
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
}

fun Fragment.showMessage(msg: String) {

    if (isAdded) {
        val view = requireActivity().findViewById<View>(android.R.id.content)
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show()
    }
}

fun Fragment.showMessage(@StringRes stringRes: Int) {

    showMessage(getString(stringRes))
}

fun Fragment.hideKeyboard() {
    requireActivity().getSystemService<InputMethodManager>()?.let { imm ->
        var view = requireActivity().currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}