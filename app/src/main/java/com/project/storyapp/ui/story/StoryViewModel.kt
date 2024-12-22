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

    var stories: LiveData<PagingData<ListStoryItem>> =
        repository.getStories().cachedIn(viewModelScope)


    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun logout() {
        viewModelScope.launch {
            userPreference.clearToken()
        }
    }
}