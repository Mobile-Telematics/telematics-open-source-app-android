package com.telematics.features.dashboard.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.telematics.core.common.extension.dpToPx
import com.telematics.core.model.TripRecordMode
import com.telematics.features.dashboard.R
import com.telematics.features.dashboard.databinding.LayoutTripRecordModeBinding


class TripRecordModeCardLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    private val binding =
        LayoutTripRecordModeBinding.inflate(LayoutInflater.from(context), this)

    init {
        init()
    }

    private fun init() {

        background = AppCompatResources.getDrawable(context, R.drawable.bg_card)

        updatePadding(
            16.dpToPx,
            16.dpToPx,
            16.dpToPx,
            16.dpToPx,
        )
    }

    val modeDropdown: View = binding.mode
    val onButton: View = binding.onButton
    val offButton: View = binding.offButton
    fun setTripRecordMode(mode: TripRecordMode, isActive: Boolean) = with(binding) {
        title.text = context.getString(mode.titleId)

        when (mode) {
            TripRecordMode.ALWAYS_ON -> {
                divider.isVisible = false
                onButton.isVisible = false
                offButton.isVisible = false
            }

            TripRecordMode.SHIFT_MODE -> {
                divider.isVisible = true
                onButton.text = context.getString(R.string.dashboard_sign_on)
                offButton.text = context.getString(R.string.dashboard_sign_off)

                if (isActive) {
                    onButton.isVisible = false
                    offButton.isVisible = true
                } else {
                    onButton.isVisible = true
                    offButton.isVisible = false
                }
            }

            TripRecordMode.ON_DEMAND -> {
                divider.isVisible = true
                onButton.text = context.getString(R.string.dashboard_start)
                offButton.text = context.getString(R.string.dashboard_stop)

                if (isActive) {
                    onButton.isVisible = false
                    offButton.isVisible = true
                } else {
                    onButton.isVisible = true
                    offButton.isVisible = false
                }
            }

            TripRecordMode.DISABLED -> {
                divider.isVisible = false
                onButton.isVisible = false
                offButton.isVisible = false
            }
        }
    }
}