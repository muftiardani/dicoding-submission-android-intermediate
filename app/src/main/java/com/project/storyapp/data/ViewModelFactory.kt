package com.project.storyapp.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.storyapp.data.preference.UserPreference
import com.project.storyapp.data.repository.LoginRepository
import com.project.storyapp.data.repository.RegisterRepository
import com.project.storyapp.data.repository.StoryAddRepository
import com.project.storyapp.data.repository.StoryDetailRepository
import com.project.storyapp.data.repository.StoryRepository
import com.project.storyapp.data.repository.StoryWithLocationRepository
import com.project.storyapp.ui.story.StoryViewModel
import com.project.storyapp.ui.login.LoginViewModel
import com.project.storyapp.ui.maps.StoryWithLocationViewModel
import com.project.storyapp.ui.register.RegisterViewModel
import com.project.storyapp.ui.story_add.LocationService
import com.project.storyapp.ui.story_add.StoryAddViewModel
import com.project.storyapp.ui.story_detail.StoryDetailViewModel

class ViewModelFactory(
    private val registerRepository: RegisterRepository? = null,
    private val loginRepository: LoginRepository? = null,
    private val storyRepository: StoryRepository? = null,
    private val storyDetailRepository: StoryDetailRepository? = null,
    private val storyAddRepository: StoryAddRepository? = null,
    private val storyWithLocationRepository: StoryWithLocationRepository? = null,
    private val userPreference: UserPreference? = null,
    private val locationService: LocationService? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(registerRepository!!) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(loginRepository!!, userPreference!!) as T
            }

            modelClass.isAssignableFrom(StoryViewModel::class.java) -> {
                StoryViewModel(userPreference!!, storyRepository!!) as T
            }

            modelClass.isAssignableFrom(StoryDetailViewModel::class.java) -> {
                StoryDetailViewModel(storyDetailRepository!!) as T
            }

            modelClass.isAssignableFrom(StoryAddViewModel::class.java) -> {
                StoryAddViewModel(userPreference!!, storyAddRepository!!, locationService!!) as T
            }

            modelClass.isAssignableFrom(StoryWithLocationViewModel::class.java) -> {
                StoryWithLocationViewModel(storyWithLocationRepository!!) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}