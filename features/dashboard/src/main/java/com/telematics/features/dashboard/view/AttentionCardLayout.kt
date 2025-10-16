package com.telematics.features.dashboard.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updatePadding
import com.telematics.core.common.extension.dpToPx
import com.telematics.features.dashboard.R
import com.telematics.features.dashboard.databinding.LayoutAttentionCardBinding


class AttentionCardLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding =
        LayoutAttentionCardBinding.inflate(LayoutInflater.from(context), this)

    init {
        init()
    }

    private fun init() {

        background = AppCompatResources.getDrawable(context, R.drawable.bg_card_blue_gradient)

        updatePadding(
            24.dpToPx,
            24.dpToPx,
            24.dpToPx,
            24.dpToPx,
        )
    }

}