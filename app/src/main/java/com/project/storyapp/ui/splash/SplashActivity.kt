package com.project.storyapp.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.project.storyapp.R
import com.project.storyapp.data.di.Injector
import com.project.storyapp.ui.login.LoginActivity
import com.project.storyapp.ui.story.StoryActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.lifecycle.lifecycleScope
import com.project.storyapp.ui.login.LoginViewModel

class SplashActivity : AppCompatActivity() {

    private val viewModel: LoginViewModel by viewModels { Injector.provideLoginViewModelFactory(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        setupSplashScreen()
    }

    private fun setupSplashScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            checkUserIsLoggedIn()
        }, SPLASH_DELAY)
    }

    private fun checkUserIsLoggedIn() {
        lifecycleScope.launch {
            val user = viewModel.getToken().first()
            if (user.token.isNotEmpty()) {
                navigateToStory()
            } else {
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun navigateToStory() {
        startActivity(Intent(this, StoryActivity::class.java))
        finish()
    }

    companion object {
        private const val SPLASH_DELAY = 2000L
    }
}
