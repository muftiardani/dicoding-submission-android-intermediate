package com.project.storyapp.data.repository

import android.util.Log
import com.project.storyapp.data.response.ErrorResponse
import com.project.storyapp.data.response.LoginResponse
import com.project.storyapp.data.retrofit.ApiService
import com.google.gson.Gson
import retrofit2.HttpException

class LoginRepository(private val apiService: ApiService) {

    suspend fun login(email: String, password: String): LoginResponse {
        return try {
            val response = apiService.login(email, password)
            Log.d("LoginRepository", "Success: $response")
            response
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Log.e("LoginRepository", "Error: ${e.message}")
            throw Exception(errorBody.message)
        }
    }
}