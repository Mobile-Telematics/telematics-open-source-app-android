package com.telematics.features.account.view

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import com.telematics.features.account.R

class ProfileCardLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        init(attrs)
    }

    private var clearEnabled: Boolean = false
    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.profile_card_layout, this)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.ProfileCardLayout)

        try {

            val hintView = findViewById<TextView>(R.id.hint).apply {
                hint = ta.getString(R.styleable.ProfileCardLayout_android_hint)
            }

            val clearView = findViewById<ImageView>(R.id.clear)

            val text = findViewById<TextView>(R.id.text).apply {
                text = ta.getString(R.styleable.ProfileCardLayout_android_text)
                hintView.isVisible = text.isNotEmpty()
                hint = ta.getString(R.styleable.ProfileTextInputLayout_android_hint)
                addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {

                    }

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        hintView.isVisible = !s.isNullOrEmpty()
                        clearView.isVisible = clearEnabled && !s.isNullOrEmpty()
                    }
                })
            }

            findViewById<ImageView>(R.id.icon).apply {
                setImageDrawable(ta.getDrawable(R.styleable.ProfileCardLayout_android_icon))

                val tintColor = ta.getColor(R.styleable.ProfileCardLayout_tint, Color.TRANSPARENT)
                if (tintColor != Color.TRANSPARENT) {
                    setColorFilter(
                        tintColor,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            }

            clearEnabled = ta.getBoolean(R.styleable.ProfileCardLayout_clearEnabled, false)

            clearView.apply {
                isVisible = clearEnabled && text.text.isNotEmpty()
                setOnClickListener {
                    textFiled.text = ""
                }
            }

            //background = AppCompatResources.getDrawable(context, R.drawable.profile_card_background)

        } finally {
            ta.recycle()
        }
    }

    private val textFiled: TextView = findViewById(R.id.text)
    private val hintFiled: TextView = findViewById(R.id.hint)

    var hint: String
        get() = hintFiled.text.toString()
        set(value) {
            hintFiled.text = value
        }
    var text: String?
        get() = textFiled.text.toString()
        set(value) {
            textFiled.text = value
        }
}