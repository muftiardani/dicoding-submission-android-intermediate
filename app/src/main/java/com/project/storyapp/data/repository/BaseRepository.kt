package com.project.storyapp.data.repository

import android.util.Log
import com.google.gson.Gson
import com.project.storyapp.data.response.ErrorResponse
import retrofit2.HttpException

abstract class BaseRepository {

    protected fun  handleHttpException(exception: HttpException): Nothing {
        val errorResponse = parseErrorResponse(exception)
        Log.e(TAG, "HTTP Error: ${exception.message}")
        throw Exception(errorResponse.message)
    }

    protected fun handleGeneralException(exception: Exception): Nothing {
        Log.e(TAG, "General Error: ${exception.message}")
        throw exception
    }

    private fun parseErrorResponse(exception: HttpException): ErrorResponse {
        val errorBody = exception.response()?.errorBody()?.string()
        return Gson().fromJson(errorBody, ErrorResponse::class.java)
    }

    companion object {
        private const val TAG = "BaseRepository"
    }
}