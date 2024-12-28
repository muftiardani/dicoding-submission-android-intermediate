package com.project.storyapp.data.repository

import android.util.Log
import com.project.storyapp.data.response.RegisterResponse
import com.project.storyapp.data.retrofit.ApiService
import retrofit2.HttpException

class RegisterRepository(private val apiService: ApiService) : BaseRepository() {

    suspend fun register(
        name: String,
        email: String,
        password: String
    ): RegisterResponse {
        try {
            return executeRegistration(name, email, password)
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            handleGeneralException(e)
        }
    }

    private suspend fun executeRegistration(
        name: String,
        email: String,
        password: String
    ): RegisterResponse {
        val response = apiService.register(name, email, password)
        Log.d(TAG, "Registration successful: $response")
        return response
    }

    companion object {
        private const val TAG = "RegisterRepository"
    }
}