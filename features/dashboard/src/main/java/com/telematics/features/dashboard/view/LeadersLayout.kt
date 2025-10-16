package com.telematics.features.dashboard.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.updatePadding
import com.telematics.core.common.extension.dpToPx
import com.telematics.features.dashboard.R
import com.telematics.features.dashboard.databinding.LayoutLeadesBinding


class LeadersLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding =
        LayoutLeadesBinding.inflate(LayoutInflater.from(context), this)

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
    fun setLeadersData(position: Int, percentage: Int) {
        with(binding) {
            leaders.text = getFormattedPosition(context, position)
            relative.text = context.getString(R.string.dashboard_position_percent, percentage)
        }
    }

    private fun getFormattedPosition(context: Context, position: Int): Spanned {

        val formattedString = context.getString(R.string.dashboard_position, position)

        val spannableString = SpannableString(formattedString)
        val positionString = position.toString()

        val startIndex = formattedString.indexOf(positionString)
        val endIndex = startIndex + positionString.length

        if (startIndex != -1) {
            val color = ContextCompat.getColor(context, R.color.design_blue)
            spannableString.setSpan(
                ForegroundColorSpan(color),
                startIndex,
                endIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }

        return spannableString
    }
}
