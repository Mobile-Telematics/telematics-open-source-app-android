package com.telematics.core.common.view

import android.annotation.SuppressLint
import android.content.Context
import android.text.InputFilter
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import com.telematics.core.common.R
import com.telematics.core.common.extension.hideKeyboard

class CustomTextInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.custom_text_input_layout, this)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.CustomTextInputLayout)

        try {

            findViewById<EditText>(R.id.text).apply {
                hint = ta.getString(R.styleable.CustomTextInputLayout_android_hint)
                imeOptions =
                    ta.getInt(R.styleable.CustomTextInputLayout_android_imeOptions, imeOptions)
                inputType =
                    ta.getInt(R.styleable.CustomTextInputLayout_android_inputType, inputType)
                filters = arrayOf(
                    *this.filters,
                    InputFilter.LengthFilter(
                        ta.getInt(
                            R.styleable.CustomTextInputLayout_android_maxLength,
                            256
                        )
                    )
                )
            }

            ta.getDrawable(R.styleable.CustomTextInputLayout_android_icon)?.let {
                findViewById<ImageView>(R.id.icon).setImageDrawable(it)
            }

        } finally {
            ta.recycle()
        }
    }


    val editField: EditText = findViewById(R.id.text)
    val icon: ImageView = findViewById(R.id.icon)

    var isErrorEnabled: Boolean = false
        set(value) {
            field = value
            editField.background = if (value) {
                AppCompatResources.getDrawable(
                    rootView.context,
                    R.drawable.custom_text_input_error_border
                )
            } else {
                AppCompatResources.getDrawable(
                    rootView.context,
                    R.drawable.custom_text_input_background
                )
            }
        }


    @SuppressLint("GestureBackNavigation")
    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {

        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            val state = keyDispatcherState
            if (state != null) {
                if (event.action == KeyEvent.ACTION_DOWN
                    && event.repeatCount == 0
                ) {
                    state.startTracking(event, this)
                    return true
                } else if (event.action == KeyEvent.ACTION_UP && !event.isCanceled && state.isTracking(
                        event
                    )
                ) {
                    editField.hideKeyboard()
                    return true
                }
            }
        }
        return super.dispatchKeyEventPreIme(event)
    }
}