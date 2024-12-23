package com.project.storyapp.ui.component

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import com.google.android.material.textfield.TextInputEditText
import com.project.storyapp.R

class EmailEditText @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.editTextStyle
) : TextInputEditText(context, attrs, defStyleAttr) {

    companion object {
        private const val TEXT_SIZE = 17f
    }

    init {
        setupEditText()
    }

    private fun setupEditText() {
        textSize = TEXT_SIZE
        addTextChangedListener(createTextWatcher())
    }

    private fun createTextWatcher() = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not used
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Not used
        }

        override fun afterTextChanged(s: Editable?) {
            validateEmail(s)
        }
    }

    private fun validateEmail(text: Editable?) {
        val isValidEmail = !text.isNullOrEmpty() &&
                Patterns.EMAIL_ADDRESS.matcher(text).matches()

        error = if (!isValidEmail) {
            context.getString(R.string.email_error)
        } else {
            null
        }
    }
}