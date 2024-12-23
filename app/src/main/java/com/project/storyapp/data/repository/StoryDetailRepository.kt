package com.project.storyapp.data.repository

import android.util.Log
import com.google.gson.Gson
import com.project.storyapp.data.preference.UserPreference
import com.project.storyapp.data.response.ErrorResponse
import com.project.storyapp.data.response.StoryDetailResponse
import com.project.storyapp.data.retrofit.ApiService
import retrofit2.HttpException

class StoryDetailRepository private constructor(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    companion object {
        private const val TAG = "StoryDetailRepository"

        @Volatile
        private var instance: StoryDetailRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryDetailRepository = instance ?: synchronized(this) {
            instance ?: StoryDetailRepository(apiService, userPreference)
                .also { instance = it }
        }
    }

    suspend fun getStoryDetail(id: String): StoryDetailResponse {
        try {
            return fetchStoryDetail(id)
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            handleGeneralException(e)
        }
    }

    private suspend fun fetchStoryDetail(id: String): StoryDetailResponse {
        val response = apiService.getStoryDetail(id)
        Log.d(TAG, "Successfully fetched story detail: $response")
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