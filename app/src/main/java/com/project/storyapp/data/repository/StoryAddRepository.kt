package com.project.storyapp.data.repository

import android.util.Log
import com.project.storyapp.data.preference.UserPreference
import com.project.storyapp.data.response.ErrorResponse
import com.project.storyapp.data.response.FileUploadResponse
import com.project.storyapp.data.retrofit.ApiService
import com.google.gson.Gson
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryAddRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference
) {

    suspend fun uploadImage(
        file: MultipartBody.Part,
        description: RequestBody,
        latitude: Float?,
        longitude: Float?
    ): FileUploadResponse {
        val latBody: RequestBody? = latitude?.let {
            RequestBody.create(MultipartBody.FORM, it.toString())
        }
        val lonBody: RequestBody? = longitude?.let {
            RequestBody.create(MultipartBody.FORM, it.toString())
        }

        return try {
            val response = apiService.uploadImage(file, description, latBody, lonBody)
            Log.d("StoryAddRepository", "Success: $response")
            response
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            Log.e("StoryAddRepository", "Error: ${e.message}")
            throw Exception(errorBody.message)
        }
    }

    companion object {
        @Volatile
        private var instance: StoryAddRepository? = null

        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): StoryAddRepository {
            return instance ?: synchronized(this) {
                instance ?: StoryAddRepository(apiService, userPreference).also { instance = it }
            }
        }
    }
}