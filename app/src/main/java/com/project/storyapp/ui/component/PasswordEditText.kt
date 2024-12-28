package com.project.storyapp.ui.component

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import com.google.android.material.textfield.TextInputEditText
import com.project.storyapp.R

class PasswordEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    private var isPasswordVisible = false

    init {
        setupEditText()
    }

    private fun setupEditText() {
        textSize = TEXT_SIZE
        addTextChangedListener(createTextWatcher())
        setupPasswordToggle()
    }

    private fun createTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validatePassword(s)
        }

        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun validatePassword(text: CharSequence?) {
        error = if (text != null && text.length < MIN_PASSWORD_LENGTH) {
            context.getString(R.string.password_error)
        } else {
            null
        }
    }

    override fun setError(error: CharSequence?) {
        super.setError(error)
        updateEndIcon(error)
    }

    private fun updateEndIcon(error: CharSequence?) {
        val drawableResId = if (error.isNullOrEmpty()) R.drawable.ic_eye_off else 0
        setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, drawableResId, 0)
    }

    private fun setupPasswordToggle() {
        setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0)
        setOnTouchListener { view, event ->
            handlePasswordToggleTouch(view, event)
        }
    }

    private fun handlePasswordToggleTouch(view: android.view.View, event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            val drawableEnd = compoundDrawablesRelative[2]
            if (drawableEnd != null && event.rawX >= (right - drawableEnd.bounds.width())) {
                togglePasswordVisibility()
                view.performClick()
                return true
            }
        }
        return false
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        updateInputType()
        updatePasswordIcon()
        moveSelectionToEnd()
    }

    private fun updateInputType() {
        inputType = if (isPasswordVisible) {
            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
    }

    private fun updatePasswordIcon() {
        val drawableResId = if (isPasswordVisible) R.drawable.ic_eye else R.drawable.ic_eye_off
        setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, drawableResId, 0)
    }

    private fun moveSelectionToEnd() {
        setSelection(text?.length ?: 0)
    }

    companion object {
        private const val TEXT_SIZE = 17f
        private const val MIN_PASSWORD_LENGTH = 8
    }
}