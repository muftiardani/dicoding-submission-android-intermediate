package com.project.storyapp.ui.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.project.storyapp.R
import com.project.storyapp.data.di.Injector
import com.project.storyapp.databinding.ActivityMapsBinding
import com.project.storyapp.data.response.ListStoryItem
import com.project.storyapp.ui.story.StoryActivity

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val viewModel: StoryWithLocationViewModel by viewModels {
        Injector.provideStoryWithLocationViewModelFactory(this)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            getMyLocation()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupMap()
        setupObservers()
        viewModel.showStoriesWithLocation()
    }

    private fun setupView() {
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupMap() {
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setupObservers() {
        with(viewModel) {
            listStory.observe(this@MapsActivity) { stories ->
                showStoryMarkers(stories)
            }
            isLoading.observe(this@MapsActivity) { isLoading ->
                showLoading(isLoading)
            }
            errorMessage.observe(this@MapsActivity) { message ->
                message?.let { showToast(it) }
            }
        }
    }

    private fun showStoryMarkers(stories: List<ListStoryItem>) {
        stories.forEach { story ->
            createStoryMarker(story)
        }
    }

    private fun createStoryMarker(story: ListStoryItem) {
        val latLng = story.getLatLng() ?: return
        mMap.addMarker(
            MarkerOptions()
                .position(latLng)
                .title(story.name)
                .snippet(story.description)
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setupMapSettings()
        getMyLocation()
        setMapStyle()
    }

    private fun setupMapSettings() {
        with(mMap.uiSettings) {
            isZoomControlsEnabled = true
            isIndoorLevelPickerEnabled = true
            isCompassEnabled = true
            isMapToolbarEnabled = true
        }
    }

    private fun getMyLocation() {
        if (hasLocationPermission()) {
            enableMyLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun hasLocationPermission() = ContextCompat.checkSelfPermission(
        applicationContext,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun enableMyLocation() {
        mMap.isMyLocationEnabled = true
    }

    private fun requestLocationPermission() {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun setMapStyle() {
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style)
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (exception: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", exception)
        }
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

    override fun onBackPressed() {
        super.onBackPressed()
        navigateToStory()
    }

    private fun navigateToStory() {
        startActivity(Intent(this, StoryActivity::class.java))
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val TAG = "MapsActivity"
    }
}

// Extension functions
private fun ListStoryItem.getLatLng(): LatLng? {
    return if (lat != null && lon != null) LatLng(lat, lon) else null
}