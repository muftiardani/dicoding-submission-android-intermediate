package com.project.storyapp.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.storyapp.data.preference.UserPreference
import com.project.storyapp.data.repository.LoginRepository
import com.project.storyapp.data.response.LoginResponse
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: LoginRepository,
    private val userPreference: UserPreference
) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResponse?>()
    val loginResult: LiveData<LoginResponse?> = _loginResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = repository.login(email, password)
                _isLoading.value = false
                _loginResult.value = response

                response.loginResult?.token?.let { token ->
                    userPreference.saveToken(token)
                }
                Log.d("LoginViewModel", "Success: ${response.message}")
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Login failed: ${e.message}"
                Log.e("LoginViewModel", "Error: ${e.message}")
            }
        }
    }

    fun getUser() = userPreference.getUser()
}