package com.project.storyapp.data.repository

import android.util.Log
import com.project.storyapp.data.response.StoryDetailResponse
import com.project.storyapp.data.retrofit.ApiService
import retrofit2.HttpException

class StoryDetailRepository private constructor(
    private val apiService: ApiService
) : BaseRepository() {

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

    companion object {
        private const val TAG = "StoryDetailRepository"

        @Volatile
        private var instance: StoryDetailRepository? = null

        fun getInstance(
            apiService: ApiService
        ): StoryDetailRepository = instance ?: synchronized(this) {
            instance ?: StoryDetailRepository(apiService)
                .also { instance = it }
        }
    }
}