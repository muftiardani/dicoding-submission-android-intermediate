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
        viewModelScope.launch {
            try {
                handleLoginAttempt(email, password)
            } catch (e: Exception) {
                handleLoginError(e)
            }
        }
    }

    private suspend fun handleLoginAttempt(email: String, password: String) {
        setLoadingState(true)
        clearErrorMessage()

        val response = repository.login(email, password)
        processLoginResponse(response)

        Log.d(TAG, "Success: ${response.message}")
    }

    private fun processLoginResponse(response: LoginResponse) {
        setLoadingState(false)
        _loginResult.value = response

        response.loginResult?.token?.let { token ->
            saveUserToken(token)
        }
    }

    private fun handleLoginError(exception: Exception) {
        setLoadingState(false)
        setErrorMessage("Login failed: ${exception.message}")
        Log.e(TAG, "Error: ${exception.message}")
    }

    private fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    private fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }

    private fun saveUserToken(token: String) {
        viewModelScope.launch {
            userPreference.saveToken(token)
        }
    }

    fun getToken() = userPreference.getToken()

    companion object {
        private const val TAG = "LoginViewModel"
    }
}