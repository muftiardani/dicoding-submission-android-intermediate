package com.project.storyapp.data.repository

import android.util.Log
import com.project.storyapp.data.response.FileUploadResponse
import com.project.storyapp.data.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class StoryAddRepository private constructor(
    private val apiService: ApiService
) : BaseRepository() {

    suspend fun uploadImage(
        file: MultipartBody.Part,
        description: RequestBody,
        latitude: Float?,
        longitude: Float?
    ): FileUploadResponse {
        try {
            val locationData = createLocationRequestBody(latitude, longitude)
            return executeUpload(file, description, locationData)
        } catch (e: HttpException) {
            handleHttpException(e)
        } catch (e: Exception) {
            handleGeneralException(e)
        }
    }

    private data class LocationRequestBody(
        val lat: RequestBody?,
        val lon: RequestBody?
    )

    private fun createLocationRequestBody(
        latitude: Float?,
        longitude: Float?
    ): LocationRequestBody {
        return LocationRequestBody(
            lat = latitude?.let { createRequestBody(it.toString()) },
            lon = longitude?.let { createRequestBody(it.toString()) }
        )
    }

    private fun createRequestBody(value: String): RequestBody {
        return RequestBody.create(MULTIPART_FORM, value)
    }

    private suspend fun executeUpload(
        file: MultipartBody.Part,
        description: RequestBody,
        locationData: LocationRequestBody
    ): FileUploadResponse {
        val response = apiService.uploadImage(
            file = file,
            description = description,
            lat = locationData.lat,
            lon = locationData.lon
        )
        Log.d(TAG, "Upload successful: $response")
        return response
    }

    companion object {
        private const val TAG = "StoryAddRepository"
        private val MULTIPART_FORM = MultipartBody.FORM

        @Volatile
        private var instance: StoryAddRepository? = null

        fun getInstance(
            apiService: ApiService
        ): StoryAddRepository = instance ?: synchronized(this) {
            instance ?: StoryAddRepository(apiService)
                .also { instance = it }
        }
    }
}