package com.project.storyapp.ui.story

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.project.storyapp.data.preference.UserPreference
import com.project.storyapp.data.repository.StoryRepository
import com.project.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class StoryViewModel(
    private val userPreference: UserPreference,
    private val repository: StoryRepository
) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Initialize stories in init block for better organization
    val stories: LiveData<PagingData<ListStoryItem>> = initializeStories()

    private fun initializeStories(): LiveData<PagingData<ListStoryItem>> {
        return repository.getStories()
            .cachedIn(viewModelScope)
    }

    fun logout() {
        viewModelScope.launch {
            try {
                clearUserSession()
            } catch (e: Exception) {
                handleLogoutError(e)
            }
        }
    }

    private suspend fun clearUserSession() {
        setLoadingState(true)
        userPreference.clearToken()
        setLoadingState(false)
    }

    private fun handleLogoutError(exception: Exception) {
        setLoadingState(false)
        setErrorMessage("Logout failed: ${exception.message}")
    }

    private fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    private fun setErrorMessage(message: String) {
        _errorMessage.value = message
    }
}