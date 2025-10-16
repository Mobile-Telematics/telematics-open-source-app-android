package com.telematics.features.dashboard.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.telematics.features.dashboard.R

class ProgressSemiWheelIndicator @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    //Sizes (with defaults)
    private var barWidth = resources.getDimension(R.dimen.semi_bar_width)
    private var countTextSize =
        resources.getDimensionPixelSize(R.dimen.semi_count_text_size).toFloat()
    private var layoutHeight = 0
    private var layoutWidth = 0

    //Colors (with defaults)
    private var backColor = ContextCompat.getColor(context, R.color.design_white)
    private var progressColor = ContextCompat.getColor(context, R.color.design_green)
    private var rimColor = ContextCompat.getColor(context, R.color.design_green)
    private var countTextColor = ContextCompat.getColor(context, R.color.design_green)

    //Rectangles
    private var wheelBounds = RectF()

    //Paints
    private val circlePaint = Paint()
    private val backPaint = Paint()
    private val barPaint = Paint()
    private var countTextPaint = TextPaint()

    private var countText: String? = null

    private var countTextWidth: Float = 0f

    // Set percentage
    private var percentage = 0

    init {
        init(attrs, defStyle)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
            MeasureSpec.getSize(widthMeasureSpec) / 2 + (barWidth.toInt() / 2),
            MeasureSpec.EXACTLY
        )

        setMeasuredDimension(widthMeasureSpec, newHeightMeasureSpec)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        layoutWidth = w
        layoutHeight = h
        setupBounds()

        invalidate()
    }


    private fun setupPaints() {
        barPaint.color = progressColor
        barPaint.isAntiAlias = true
        barPaint.style = Paint.Style.STROKE
        barPaint.strokeWidth = barWidth
        barPaint.strokeCap = Paint.Cap.ROUND

        circlePaint.color = rimColor
        circlePaint.isAntiAlias = true
        circlePaint.style = Paint.Style.STROKE
        circlePaint.strokeWidth = barWidth
        circlePaint.strokeCap = Paint.Cap.ROUND
        circlePaint.alpha = 77 // 30%

        backPaint.color = backColor
        backPaint.isAntiAlias = true
        backPaint.style = Paint.Style.FILL

        countTextPaint.color = countTextColor
        countTextPaint.flags = Paint.ANTI_ALIAS_FLAG
        countTextPaint.textSize = countTextSize
        val countTexTypeface = ResourcesCompat.getFont(context, R.font.open_sans_bold_700)
        countTextPaint.typeface = Typeface.create(countTexTypeface, Typeface.BOLD)

    }

    private fun setupBounds() {
        if (width == 0) return
        val viewWidth = width

        val offset = barWidth / 2f

        wheelBounds = RectF(
            offset,
            offset,
            viewWidth.toFloat() - offset,
            viewWidth.toFloat() - (barWidth.toInt() / 2)
        )

        countTextWidth =
            countTextPaint.measureText(if (countText.isNullOrEmpty()) " " else countText)
    }

    private fun init(attrs: AttributeSet?, defStyle: Int) {
        // Load attributes
        val typedArray = context.obtainStyledAttributes(
            attrs, R.styleable.ProgressSemiWheelIndicator, defStyle, 0
        )

        if (typedArray.hasValue(R.styleable.ProgressSemiWheelIndicator_countText))
            countText = typedArray.getString(R.styleable.ProgressSemiWheelIndicator_countText)

        barWidth =
            typedArray.getDimension(R.styleable.ProgressSemiWheelIndicator_barWidth, barWidth)
        progressColor =
            typedArray.getColor(R.styleable.ProgressSemiWheelIndicator_progressColor, progressColor)
        rimColor = typedArray.getColor(R.styleable.ProgressSemiWheelIndicator_rimColor, rimColor)
        backColor = typedArray.getColor(R.styleable.ProgressSemiWheelIndicator_backColor, backColor)
        countTextColor =
            typedArray.getColor(
                R.styleable.ProgressSemiWheelIndicator_countTextColor,
                countTextColor
            )
        countTextSize =
            typedArray.getDimension(
                R.styleable.ProgressSemiWheelIndicator_countTextSize,
                countTextSize
            )
        percentage =
            typedArray.getInt(R.styleable.ProgressSemiWheelIndicator_percentage, percentage)

        typedArray.recycle()

        setupPaints()

        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawArc(wheelBounds, 180f, 180f, false, backPaint)
        canvas.drawArc(wheelBounds, 180f, 180f, false, circlePaint)
        canvas.drawArc(wheelBounds, 180f, percentage.toFloat(), false, barPaint)

        countText?.let {

            canvas.drawText(
                it,
                this.width / 2f - countTextWidth / 2f,
                this.height.toFloat() * 0.99f,
                countTextPaint
            )
        }
    }

    fun setCountText(countText: String) {
        this.countText = countText
        setupBounds()
        invalidate()
    }

    fun setProgressColor(color: Int) {
        progressColor = color
        invalidate()
    }

    fun setPercentage(per: Int) {
        startAnimation(per)
    }

    fun setProgress(counter: Int) {

        countText = if (counter < 0) {
            countTextSize =
                resources.getDimensionPixelSize(R.dimen.semi_count_text_size_half).toFloat()
            "N/A"
        } else {
            countTextSize = resources.getDimensionPixelSize(R.dimen.semi_count_text_size).toFloat()
            "$counter"
        }
        countTextPaint.textSize = countTextSize

        progressColor = getProgressColor(counter)
        rimColor = progressColor

        countTextPaint.color = progressColor

        barPaint.color = progressColor
        circlePaint.color = rimColor
        circlePaint.alpha = 77

        val per = if (counter < 0) {
            0
        } else {
            (180f * counter.toFloat() / 100.0f).toInt()
        }

        setupBounds()
        startAnimation(per)
    }

    fun setUnicolorProgress(counter: Int, total: Int) {
        countText = counter.toString()

        val per = if (total > 0f) {
            (180f * counter.toFloat() / total.toFloat()).toInt()
        } else {
            0
        }
        setupBounds()
        startAnimation(per)
    }

    private fun startAnimation(per: Int) {
        val diff = per - percentage
        val valueAnimator = ValueAnimator
            .ofInt(percentage, percentage + diff)
            .setDuration(1000)
        valueAnimator.interpolator = AccelerateInterpolator()
        valueAnimator.addUpdateListener { animation ->
            percentage = animation.animatedValue as Int
            invalidate()
        }
        valueAnimator.start()
    }

    private fun getProgressColor(counter: Int) = ContextCompat.getColor(
        context,
        when (counter) {
            in 0..55 -> {
                R.color.design_red
            }

            in 56..70 -> {
                R.color.design_orange
            }

            in 71..85 -> {
                R.color.design_yellow
            }

            in 86..100 -> {
                R.color.design_green
            }

            else -> {
                R.color.design_black
            }
        }
    )
}
