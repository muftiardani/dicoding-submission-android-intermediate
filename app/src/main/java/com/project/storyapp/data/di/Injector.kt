package com.project.storyapp.data.di

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.project.storyapp.data.ViewModelFactory
import com.project.storyapp.data.preference.UserPreference
import com.project.storyapp.data.repository.LoginRepository
import com.project.storyapp.data.repository.RegisterRepository
import com.project.storyapp.data.repository.StoryAddRepository
import com.project.storyapp.data.repository.StoryDetailRepository
import com.project.storyapp.data.repository.StoryRepository
import com.project.storyapp.data.repository.StoryWithLocationRepository
import com.project.storyapp.data.retrofit.ApiConfig
import com.project.storyapp.ui.story_add.LocationService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injector {

    private val Context.dataStore by preferencesDataStore(name = "user_preferences")

    private fun provideRegisterRepository(): RegisterRepository {
        val apiService = ApiConfig.getApiService()
        return RegisterRepository(apiService)
    }

    private fun provideLoginRepository(): LoginRepository {
        val apiService = ApiConfig.getApiService()
        return LoginRepository(apiService)
    }

    private fun provideUserPreference(context: Context): UserPreference {
        val dataStore = context.dataStore
        return UserPreference.getInstance(dataStore)
    }

    fun provideRegisterViewModelFactory(context: Context): ViewModelFactory {
        val registerRepository = provideRegisterRepository()
        val userPreference = provideUserPreference(context)
        return ViewModelFactory(
            registerRepository = registerRepository,
            userPreference = userPreference
        )
    }

    fun provideLoginViewModelFactory(context: Context): ViewModelFactory {
        val loginRepository = provideLoginRepository()
        val userPreference = provideUserPreference(context)
        return ViewModelFactory(loginRepository = loginRepository, userPreference = userPreference)
    }

    private fun provideStoryRepository(context: Context): StoryRepository {
        val userPreference = provideUserPreference(context)
        val user = runBlocking { userPreference.getToken().first() }
        val apiService = ApiConfig.getApiService(user.token)

        return StoryRepository.getInstance(apiService)
    }


    fun provideStoryViewModelFactory(context: Context): ViewModelFactory {
        val storyRepository = provideStoryRepository(context)
        val userPreference = provideUserPreference(context)

        return ViewModelFactory(
            storyRepository = storyRepository,
            userPreference = userPreference
        )
    }

    private fun provideStoryDetailRepository(context: Context): StoryDetailRepository {
        val userPreference = provideUserPreference(context)
        val user = runBlocking { userPreference.getToken().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryDetailRepository.getInstance(apiService)
    }

    fun provideStoryDetailViewModelFactory(context: Context): ViewModelFactory {
        val storyDetailRepository = provideStoryDetailRepository(context)
        val userPreference = provideUserPreference(context)
        return ViewModelFactory(
            storyDetailRepository = storyDetailRepository,
            userPreference = userPreference
        )
    }

    private fun provideStoryAddRepository(context: Context): StoryAddRepository {
        val userPreference = provideUserPreference(context)
        val user = runBlocking { userPreference.getToken().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryAddRepository.getInstance(apiService)
    }

    private fun provideLocationService(context: Context): LocationService {
        return LocationService(context.applicationContext)
    }

    fun provideStoryAddViewModelFactory(context: Context): ViewModelFactory {
        val storyAddRepository = provideStoryAddRepository(context)
        val userPreference = provideUserPreference(context)
        val locationService = provideLocationService(context)

        return ViewModelFactory(
            storyAddRepository = storyAddRepository,
            userPreference = userPreference,
            locationService = locationService
        )
    }

    private fun provideStoryWithLocationRepository(context: Context): StoryWithLocationRepository {
        val userPreference = provideUserPreference(context)
        val user = runBlocking { userPreference.getToken().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryWithLocationRepository.getInstance(apiService)
    }

    fun provideStoryWithLocationViewModelFactory(context: Context): ViewModelFactory {
        val storyWithLocation = provideStoryWithLocationRepository(context)
        val userPreference = provideUserPreference(context)
        return ViewModelFactory(
            storyWithLocationRepository = storyWithLocation,
            userPreference = userPreference
        )
    }
}