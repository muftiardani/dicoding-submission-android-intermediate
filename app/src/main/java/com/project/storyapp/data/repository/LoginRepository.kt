package com.project.storyapp.data.repository

import android.util.Log
import com.google.gson.Gson
import com.project.storyapp.data.response.ErrorResponse
import com.project.storyapp.data.response.LoginResponse
import com.project.storyapp.data.retrofit.ApiService
import retrofit2.HttpException

class LoginRepository(private val apiService: ApiService) {

    companion object {
        private const val TAG = "LoginRepository"
    }

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

    private fun handleHttpException(exception: HttpException): Nothing {
        val errorResponse = parseErrorResponse(exception)
        Log.e(TAG, "HTTP Error: ${exception.message}")
        throw Exception(errorResponse.message)
    }

    private fun handleGeneralException(exception: Exception): Nothing {
        Log.e(TAG, "General Error: ${exception.message}")
        throw exception
    }

    private fun parseErrorResponse(exception: HttpException): ErrorResponse {
        val errorBody = exception.response()?.errorBody()?.string()
        return Gson().fromJson(errorBody, ErrorResponse::class.java)
    }
}