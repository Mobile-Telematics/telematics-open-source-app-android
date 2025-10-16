package com.telematics.features.dashboard.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import com.telematics.core.common.extension.dpToPx
import com.telematics.features.dashboard.R
import com.telematics.features.dashboard.databinding.LayoutDrivingStrakesBinding


class DrivingStrakesLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding =
        LayoutDrivingStrakesBinding.inflate(LayoutInflater.from(context), this)

    init {
        init()
    }

    private fun init() {

        background = AppCompatResources.getDrawable(context, R.drawable.bg_card)

        updatePadding(
            16.dpToPx,
            24.dpToPx,
            16.dpToPx,
            24.dpToPx,
        )
    }

    @SuppressLint("SetTextI18n")
    fun setAnnualDrivingData(noSpeedingCount: Int, safeManeuvers: Int) {
        with(binding) {
            speedValue.text = context.resources.getQuantityString(
                R.plurals.trips,
                noSpeedingCount,
                noSpeedingCount
            )
            safeManeuversValue.text = context.resources.getQuantityString(
                R.plurals.trips,
                safeManeuvers,
                safeManeuvers
            )
        }
    }
}
