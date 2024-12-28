package com.project.storyapp.ui.story_add

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.storyapp.data.preference.UserPreference
import com.project.storyapp.data.repository.StoryAddRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class StoryAddViewModel(
    private val repository: StoryAddRepository,
    private val userPreference: UserPreference,
    private val locationService: LocationService
) : ViewModel() {

    private val _uploadResult = MutableLiveData<Boolean>()
    val uploadResult: LiveData<Boolean> = _uploadResult

    private val _currentImageUri = MutableLiveData<Uri?>()
    val currentImageUri: LiveData<Uri?> get() = _currentImageUri

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private var latitude: Float? = null
    private var longitude: Float? = null

    suspend fun uploadImage(
        file: MultipartBody.Part,
        description: RequestBody,
        withLocation: Boolean
    ) {
        _isLoading.value = true
        _errorMessage.value = null

        if (withLocation) {
            val location = locationService.getCurrentLocation()
            latitude = location?.latitude?.toFloat()
            longitude = location?.longitude?.toFloat()
        }

        viewModelScope.launch {
            try {
                val response = repository.uploadImage(file, description, latitude, longitude)
                _uploadResult.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _uploadResult.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setImageUri(uri: Uri?) {
        _currentImageUri.value = uri
    }
}