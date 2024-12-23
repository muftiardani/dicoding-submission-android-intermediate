package com.project.storyapp.data.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Class untuk konfigurasi API client
 */
class ApiConfig private constructor() {
    companion object {
        private const val BASE_URL = "https://story-api.dicoding.dev/v1/"
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "

        /**
         * Membuat instance ApiService
         * @param token String? token autentikasi (opsional)
         * @return ApiService instance
         */
        fun getApiService(token: String? = null): ApiService {
            val client = createOkHttpClient(token)
            return createRetrofit(client).create(ApiService::class.java)
        }

        /**
         * Membuat OkHttpClient dengan interceptor
         */
        private fun createOkHttpClient(token: String?): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(createLoggingInterceptor())
                .addInterceptor(createAuthInterceptor(token))
                .build()
        }

        /**
         * Membuat logging interceptor
         */
        private fun createLoggingInterceptor(): HttpLoggingInterceptor {
            return HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
        }

        /**
         * Membuat auth interceptor
         */
        private fun createAuthInterceptor(token: String?): Interceptor {
            return Interceptor { chain ->
                val originalRequest = chain.request()
                val requestWithAuth = originalRequest.newBuilder()
                    .addHeader(AUTHORIZATION_HEADER, "$BEARER_PREFIX${token.orEmpty()}")
                    .build()
                chain.proceed(requestWithAuth)
            }
        }

        /**
         * Membuat Retrofit instance
         */
        private fun createRetrofit(client: OkHttpClient): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }
    }
}