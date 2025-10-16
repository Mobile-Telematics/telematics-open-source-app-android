package com.telematics.features.account.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.telematics.core.common.extension.hideKeyboard
import com.telematics.features.account.R

class ProfilePhoneInputLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        init(attrs)
    }

    private var clearEnabled: Boolean = true
    private fun init(attrs: AttributeSet?) {
        View.inflate(context, R.layout.profile_phone_input_layout, this)

        val ta = context.obtainStyledAttributes(attrs, R.styleable.ProfilePhoneInputLayout)

        try {

            val hintView = findViewById<TextView>(R.id.hint).apply {
                hint = ta.getString(R.styleable.ProfilePhoneInputLayout_android_hint)
            }

            val clearView = findViewById<ImageView>(R.id.clear)

            val ccp = findViewById<com.hbb20.CountryCodePicker>(R.id.code)

            val text = findViewById<EditText>(R.id.text).apply {
                setText(ta.getString(R.styleable.ProfilePhoneInputLayout_android_text))
                hintView.isVisible = text.isNotEmpty()
                hint = ta.getString(R.styleable.ProfilePhoneInputLayout_android_hint)
                imeOptions =
                    ta.getInt(R.styleable.ProfilePhoneInputLayout_android_imeOptions, imeOptions)
                inputType =
                    ta.getInt(R.styleable.ProfilePhoneInputLayout_android_inputType, inputType)
                filters = arrayOf(
                    *this.filters,
                    InputFilter.LengthFilter(
                        ta.getInt(
                            R.styleable.ProfilePhoneInputLayout_android_maxLength,
                            256
                        )
                    )
                )


                setOnFocusChangeListener { _, hasFocus ->
                    if (hasFocus) {
                        ccp.isVisible = true
                    } else {
                        ccp.isVisible = text.isNotEmpty()
                    }
                }

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

            ccp.registerCarrierNumberEditText(text)

            findViewById<ImageView>(R.id.icon).apply {
                setImageDrawable(ta.getDrawable(R.styleable.ProfilePhoneInputLayout_android_icon))

                val tintColor =
                    ta.getColor(R.styleable.ProfilePhoneInputLayout_tint, Color.TRANSPARENT)
                if (tintColor != Color.TRANSPARENT) {
                    setColorFilter(
                        tintColor,
                        android.graphics.PorterDuff.Mode.SRC_IN
                    )
                }
            }

            clearEnabled = ta.getBoolean(R.styleable.ProfileCardLayout_clearEnabled, true)

            clearView.apply {
                isVisible = clearEnabled && text.text.isNotEmpty()
                setOnClickListener {
                    editField.apply {
                        setText("")
                        if (!hasFocus()) ccp.isVisible = false
                    }
                }
            }

        } finally {
            ta.recycle()
        }
    }

    private val ccp = findViewById<com.hbb20.CountryCodePicker>(R.id.code)

    val editField: EditText = findViewById(R.id.text)
    val icon: ImageView = findViewById(R.id.icon)

    var fullNumber: String
        get() = if (editField.text.isBlank()) ""
        else "+${ccp.fullNumber}"
        set(value) {
            try {
                if (value.startsWith("+")) {
                    ccp.fullNumber = value
                } else {
                    editField.setText(value)
                }

            } catch (e: Exception) {
                editField.setText(value)
            }
            ccp.isVisible = value.isNotEmpty()
        }

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