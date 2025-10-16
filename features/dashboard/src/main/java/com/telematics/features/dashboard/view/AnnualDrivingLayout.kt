package com.telematics.features.dashboard.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import com.telematics.core.common.extension.dpToPx
import com.telematics.core.common.extension.format
import com.telematics.core.data.formatter.MeasuresFormatter
import com.telematics.features.dashboard.R
import com.telematics.features.dashboard.databinding.LayoutAnnualDrivingBinding


class AnnualDrivingLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding =
        LayoutAnnualDrivingBinding.inflate(LayoutInflater.from(context), this)

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

    @SuppressLint("SetTextI18n")
    fun setAnnualDrivingData(
        tripsCount: Int,
        mileage: Double,
        drivingTime: Double,
        formatter: MeasuresFormatter
    ) {
        with(binding) {
            tripsValue.text = tripsCount.toString()
            formatter.getDistanceByKm(mileage).apply {
                mileageValue.text = this.let {
                    it.format(
                        if (it > 100) {
                            "0"
                        } else {
                            "0.0"
                        }
                    )
                }
            }
            hoursValue.text = drivingTime.let {
                it.format(
                    if (it > 100) {
                        "0"
                    } else {
                        "0.0"
                    }
                )
            }
        }
    }
}
