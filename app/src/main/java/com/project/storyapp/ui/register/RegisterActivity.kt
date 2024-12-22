package com.project.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.project.storyapp.R
import com.project.storyapp.data.di.Injector
import com.project.storyapp.databinding.ActivityRegisterBinding
import com.project.storyapp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels {
        Injector.provideRegisterViewModelFactory(
            this
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()

        binding.tvLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.editText?.text.toString()
            val email = binding.edRegisterEmail.editText?.text.toString()
            val password = binding.edRegisterPassword.editText?.text.toString()

            var isValid = true

            if (name.isEmpty()) {
                binding.edRegisterName.error = getString(R.string.et_name_error)
                isValid = false
            } else {
                binding.edRegisterName.error = null
            }

            if (email.isEmpty()) {
                binding.edRegisterEmail.error = getString(R.string.error_email_empty)
                isValid = false
            } else {
                binding.edRegisterEmail.error = null
            }

            if (password.isEmpty()) {
                binding.edRegisterPassword.error = getString(R.string.error_password_empty)
                isValid = false
            } else {
                binding.edRegisterPassword.error = null
            }

            if (isValid) {
                viewModel.register(name, email, password)
            }
        }

        viewModel.registerResult.observe(this) { response ->
            if (response != null) {
                Toast.makeText(this, getString(R.string.regist_successful), Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, getString(R.string.regist_fail), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.isLoading.observe(this) {
            showLoading(it)
        }

        viewModel.errorMessage.observe(this) { errorMessage ->
            errorMessage?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(700)
        val name = ObjectAnimator.ofFloat(binding.edRegisterName, View.ALPHA, 1f).setDuration(700)
        val email = ObjectAnimator.ofFloat(binding.edRegisterEmail, View.ALPHA, 1f).setDuration(700)
        val password =
            ObjectAnimator.ofFloat(binding.edRegisterPassword, View.ALPHA, 1f).setDuration(700)
        val register = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(700)
        val loginPrompt = ObjectAnimator.ofFloat(binding.tvLogin, View.ALPHA, 1f).setDuration(700)

        val together = AnimatorSet().apply {
            playTogether(loginPrompt, register)
        }

        AnimatorSet().apply {
            playSequentially(title, name, email, password, together)
            start()
        }
    }
}