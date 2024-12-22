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
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                val response = repository.register(name, email, password)
                _isLoading.value = false
                _registerResult.value = response
                Log.d("RegisterViewModel", "Success: ${response.message}")
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Registration failed: ${e.message}"
                Log.e("RegisterViewModel", "Error: ${e.message}")
            }
        }
    }
}