package com.project.storyapp.data.repository

import android.util.Log
import com.project.storyapp.data.response.LoginResponse
import com.project.storyapp.data.retrofit.ApiService
import retrofit2.HttpException

class LoginRepository(private val apiService: ApiService) : BaseRepository() {

    suspend fun login(email: String, password: String): LoginResponse {
        try {
            return executeLogin(email, password)
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            handleGeneralException(e)
        }
    }

    private suspend fun executeLogin(email: String, password: String): LoginResponse {
        val response = apiService.login(email, password)
        Log.d(TAG, "Login successful: $response")
        return response
    }

    companion object {
        private const val TAG = "LoginRepository"
    }
}