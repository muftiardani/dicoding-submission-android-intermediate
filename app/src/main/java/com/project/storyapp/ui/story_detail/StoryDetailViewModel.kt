package com.project.storyapp.ui.story_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.storyapp.data.repository.StoryDetailRepository
import com.project.storyapp.data.response.Story
import kotlinx.coroutines.launch

class StoryDetailViewModel(
    private val repository: StoryDetailRepository
) : ViewModel() {

    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun showDetailStory(id: String) {
        viewModelScope.launch {
            try {
                fetchStoryDetail(id)
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }

    private suspend fun fetchStoryDetail(id: String) {
        setLoadingState(true)
        clearErrorMessage()

        val response = repository.getStoryDetail(id)
        processStoryResponse(response.story)

        setLoadingState(false)
    }

    private fun processStoryResponse(story: Story?) {
        story?.let {
            _story.value = it
        } ?: handleNullStory()
    }

    private fun handleNullStory() {
        setErrorMessage("Story data not found")
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