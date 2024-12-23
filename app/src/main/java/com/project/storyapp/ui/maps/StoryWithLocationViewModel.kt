package com.project.storyapp.ui.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.storyapp.data.repository.StoryWithLocationRepository
import com.project.storyapp.data.response.ListStoryItem
import kotlinx.coroutines.launch

class StoryWithLocationViewModel(
    private val repository: StoryWithLocationRepository
) : ViewModel() {

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun showStoriesWithLocation() {
        viewModelScope.launch {
            try {
                fetchStories()
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private suspend fun fetchStories() {
        setLoadingState(true)
        clearErrorMessage()

        val response = repository.getStoriesWithLocation()
        _listStory.value = response.listStory

        setLoadingState(false)
    }

    private fun handleError(exception: Exception) {
        setErrorMessage(exception.message)
        setLoadingState(false)
    }

    private fun setLoadingState(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    private fun clearErrorMessage() {
        _errorMessage.value = null
    }

    private fun setErrorMessage(message: String?) {
        _errorMessage.value = message
    }
}