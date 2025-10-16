package com.telematics.features.settings.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StyleableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.telematics.features.settings.R

class SettingsItemLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.settings_item_layout, this)

        val text: TextView = findViewById(R.id.text)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.SettingsItemLayout)
        try {
            val textField = ta.getString(R.styleable.SettingsItemLayout_android_text)

            text.apply {
                this.text = textField

                @StyleableRes
                val textColor =
                    ta.getResourceId(R.styleable.SettingsItemLayout_android_textColor, 0)
                if (textColor != 0) {
                    setTextColor(ContextCompat.getColor(context, textColor))
                }
            }

            findViewById<ImageView>(R.id.icon).apply {
                ta.getDrawable(R.styleable.SettingsItemLayout_android_icon)?.also {
                    setImageDrawable(it)
                    isVisible = true
                }

                val tintColor =
                    ta.getColor(R.styleable.SettingsItemLayout_iconTint, Color.TRANSPARENT)
                if (tintColor != Color.TRANSPARENT) {
                    setColorFilter(
                        tintColor,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            }
        } finally {
            ta.recycle()
        }
    }

    val text: TextView = findViewById(R.id.text)
    val textMode: TextView = findViewById(R.id.text_mode)
}