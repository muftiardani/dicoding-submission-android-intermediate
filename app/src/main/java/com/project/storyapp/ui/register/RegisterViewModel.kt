package com.project.storyapp.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.storyapp.data.repository.RegisterRepository
import com.project.storyapp.data.response.RegisterResponse
import kotlinx.coroutines.launch

class RegisterViewModel(private val repository: RegisterRepository) : ViewModel() {

    private val _registerResult = MutableLiveData<RegisterResponse?>()
    val registerResult: LiveData<RegisterResponse?> = _registerResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            try {
                handleRegistration(name, email, password)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private suspend fun handleRegistration(name: String, email: String, password: String) {
        setLoadingState(true)
        clearErrorMessage()

        val response = repository.register(name, email, password)
        processRegistrationResponse(response)
    }

    private fun processRegistrationResponse(response: RegisterResponse) {
        setLoadingState(false)
        _registerResult.value = response
        Log.d(TAG, "Success: ${response.message}")
    }

    private fun handleError(exception: Exception) {
        setLoadingState(false)
        setErrorMessage("Registration failed: ${exception.message}")
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

    companion object {
        private const val TAG = "RegisterViewModel"
    }
}