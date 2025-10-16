package com.telematics.core.common.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.FrameLayout
import com.telematics.core.common.R

class RoundedFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val radius: Float
    private var isPathValid = false
    private val path = Path()

    init {

        val ta = context.obtainStyledAttributes(attrs, R.styleable.RoundedFrameLayout)

        try {

            radius = ta.getDimension(
                R.styleable.RoundedFrameLayout_android_radius, 0f
            )

        } finally {
            ta.recycle()
        }
    }

    private val roundRectPath: Path
        get() {
            if (isPathValid) {
                return path
            }
            path.reset()
            val width = width
            val height = height
            val bounds = RectF(0f, 0f, width.toFloat(), height.toFloat())
            path.addRoundRect(bounds, radius, radius, Path.Direction.CCW)
            isPathValid = true
            return path
        }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.clipPath(roundRectPath)
        super.dispatchDraw(canvas)
    }

    override fun draw(canvas: Canvas) {
        canvas.clipPath(roundRectPath)
        super.draw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val oldWidth = measuredWidth
        val oldHeight = measuredHeight
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val newWidth = measuredWidth
        val newHeight = measuredHeight
        if (newWidth != oldWidth || newHeight != oldHeight) {
            isPathValid = false
        }
    }
}