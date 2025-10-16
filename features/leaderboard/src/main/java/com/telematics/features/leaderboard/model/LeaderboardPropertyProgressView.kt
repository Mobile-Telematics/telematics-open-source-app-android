package com.telematics.features.leaderboard.model

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.telematics.features.leaderboard.R
import com.telematics.features.leaderboard.databinding.LeaderboardUserValueItemBinding
import kotlin.math.roundToInt

class LeaderboardPropertyProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val binding =
        LeaderboardUserValueItemBinding.inflate(LayoutInflater.from(context), this)

    init {
        with(binding) {
            seekBar.setOnTouchListener { _, _ -> true }
            topView.setOnClickListener { performClick() }

            background = ContextCompat.getDrawable(context, R.drawable.progress_card_background)
        }
    }

    fun setTextRes(@StringRes res: Int) = with(binding) {
        propertyHeader.text = context.getString(res)
    }

    fun setImageRes(@DrawableRes res: Int) = with(binding) {
        propertyIcon.setImageResource(res)
    }

    fun setProgressMax(max: Int = 100) = with(binding) {
        seekBar.max = max * 10
    }

    fun setProgress(progress: Double) = with(binding) {
        if (progress.roundToInt() == seekBar.progress) return
        seekBar.progress = 0
        val animator = ObjectAnimator.ofInt(seekBar, "progress", progress.roundToInt() * 10)
            .setDuration(900)

        animator.start()
    }

    fun highlight() = with(binding) {
        // hided icon
        propertyIcon.isVisible = false


        // changed typeface and text size of type text
        propertyHeader.setTypeface(propertyPlace.typeface, Typeface.BOLD)
        propertyHeader.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimensionPixelSize(R.dimen.leaderboard_user_item_type_text_big).toFloat()
        )

        // changed text size of place
        propertyPlace.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimensionPixelSize(R.dimen.leaderboard_user_item_place_text_big).toFloat()
        )
    }

    @SuppressLint("SetTextI18n")
    fun setPlace(place: Int) = with(binding) {
        ValueAnimator.ofInt(0, place).apply {
            duration = 900
            addUpdateListener {
                propertyPlace.text = "#${it.animatedValue}"
            }
        }.start()

    }

    fun setClickListener(listener: OnClickListener?) {
        this.setOnClickListener(listener)
    }


}