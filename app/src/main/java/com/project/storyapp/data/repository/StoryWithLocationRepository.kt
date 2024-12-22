package com.project.storyapp.data.repository

import android.util.Log
import com.project.storyapp.data.preference.UserPreference
import com.project.storyapp.data.response.ErrorResponse
import com.project.storyapp.data.response.StoryResponse
import com.project.storyapp.data.retrofit.ApiService
import com.google.gson.Gson
import retrofit2.HttpException

class StoryWithLocationRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {
    suspend fun getStoriesWithLocation(): StoryResponse {
        return try {
            val response = apiService.getStoriesWithLocation()
            Log.d("StoryRepository", "Success: $response")
            response
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Log.e("StoryRepository", "Error: ${e.message}")
            throw Exception(errorBody.message)
        }
    }

    companion object {
        @Volatile
        private var instance: StoryWithLocationRepository? = null

        fun getInstance(apiService: ApiService, userPreference: UserPreference): StoryWithLocationRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryWithLocationRepository(apiService, userPreference).also { instance = it }
            }
        }
    }
}