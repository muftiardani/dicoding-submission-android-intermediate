package com.project.storyapp.ui.story_add

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.project.storyapp.R
import com.project.storyapp.data.di.Injector
import com.project.storyapp.databinding.ActivityStoryAddBinding
import com.project.storyapp.ui.story.StoryActivity
import com.project.storyapp.utils.ImageUtils.getImageUri
import com.project.storyapp.utils.ImageUtils.reduceFileImage
import com.project.storyapp.utils.ImageUtils.uriToFile
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class StoryAddActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "StoryAddActivity"
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var binding: ActivityStoryAddBinding

    private val viewModel: StoryAddViewModel by viewModels {
        Injector.provideStoryAddViewModelFactory(applicationContext)
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        handleGalleryResult(uri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        handleCameraResult(isSuccess)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupObservers()
        setupClickListeners()
        checkLocationPermission()
    }

    private fun setupView() {
        binding = ActivityStoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupObservers() {
        with(viewModel) {
            currentImageUri.observe(this@StoryAddActivity) { uri ->
                uri?.let { binding.previewImageView.setImageURI(it) }
            }

            uploadResult.observe(this@StoryAddActivity) { isSuccess ->
                handleUploadResult(isSuccess)
            }
        }
    }

    private fun setupClickListeners() {
        with(binding) {
            galleryButton.setOnClickListener { startGallery() }
            cameraButton.setOnClickListener { startCamera() }
            buttonAdd.setOnClickListener { handleAddButtonClick() }
        }
    }

    private fun handleAddButtonClick() {
        if (viewModel.currentImageUri.value == null) {
            showToast(getString(R.string.choose_image_first))
        } else {
            lifecycleScope.launch {
                uploadImage()
            }
        }
    }

    private fun handleUploadResult(isSuccess: Boolean) {
        showLoading(false)
        if (isSuccess) {
            handleSuccessfulUpload()
        } else {
            viewModel.errorMessage.value?.let { showToast(it) }
        }
    }

    private fun handleSuccessfulUpload() {
        showToast(getString(R.string.image_uploaded))
        navigateToStory()
    }

    private fun navigateToStory() {
        Intent(this, StoryActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(this)
        }
    }

    private fun startGallery() {
        launcherGallery.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun handleGalleryResult(uri: Uri?) {
        if (uri != null) {
            viewModel.setImageUri(uri)
            showImage()
        } else {
            Log.d(TAG, "Image uri is null")
        }
    }

    private fun startCamera() {
        viewModel.setImageUri(getImageUri(this))
        viewModel.currentImageUri.value?.let { launcherIntentCamera.launch(it) }
    }

    private fun handleCameraResult(isSuccess: Boolean) {
        if (isSuccess) {
            showImage()
        } else {
            viewModel.setImageUri(null)
        }
    }

    private suspend fun uploadImage() {
        val currentUri = viewModel.currentImageUri.value ?: return
        val imageFile = uriToFile(currentUri, this).reduceFileImage()
        val description = binding.edAddDescription.editText?.text.toString()

        showLoading(true)

        val multipartBody = createMultipartBody(imageFile)
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val withLocation = binding.checkBoxUploadWithLocation.isChecked

        viewModel.uploadImage(multipartBody, requestBody, withLocation)
    }

    private fun createMultipartBody(imageFile: java.io.File): MultipartBody.Part {
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        return MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
    }

    private fun showImage() {
        viewModel.currentImageUri.value?.let { uri ->
            binding.previewImageView.setImageURI(uri)
        } ?: showToast(getString(R.string.no_image))
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun checkLocationPermission() {
        if (!hasLocationPermissions()) {
            requestLocationPermissions()
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
                hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_PERMISSION_REQUEST_CODE
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}