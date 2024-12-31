package com.project.storyapp.data.repository

import android.util.Log
import com.project.storyapp.data.response.ErrorResponse
import com.project.storyapp.data.response.StoryResponse
import com.project.storyapp.data.retrofit.ApiService
import com.google.gson.Gson
import retrofit2.HttpException

class StoryWithLocationRepository(
    private val apiService: ApiService
) {

    suspend fun getStoriesWithLocation(): StoryResponse {
        return try {
            val response = apiService.getStoriesWithLocation()
            Log.d("StoryRepository", "Success: $response")
            response
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Log.e("StoryRepository", "HTTP Error: ${e.message}")
            throw Exception(errorBody.message)
        } catch (e: Exception) {
            Log.e("StoryRepository", "Unknown Error: ${e.message}")
            throw Exception("An unexpected error occurred: ${e.message}")
        }
    }

    companion object {
        @Volatile
        private var instance: StoryWithLocationRepository? = null

        fun getInstance(apiService: ApiService): StoryWithLocationRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryWithLocationRepository(apiService).also { instance = it }
            }
        }
    }
}