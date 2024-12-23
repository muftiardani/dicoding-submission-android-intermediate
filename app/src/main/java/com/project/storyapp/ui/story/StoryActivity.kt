package com.project.storyapp.ui.story

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.storyapp.R
import com.project.storyapp.data.di.Injector
import com.project.storyapp.data.response.ListStoryItem
import com.project.storyapp.databinding.ActivityStoryBinding
import com.project.storyapp.ui.login.LoginActivity
import com.project.storyapp.ui.maps.MapsActivity
import com.project.storyapp.ui.story_add.StoryAddActivity
import kotlinx.coroutines.launch

class StoryActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "StoryActivity"
    }

    private lateinit var binding: ActivityStoryBinding
    private lateinit var storyAdapter: StoryAdapter

    private val viewModel: StoryViewModel by viewModels {
        Injector.provideStoryViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupView() {
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter()
        binding.rvStories.apply {
            adapter = storyAdapter
            layoutManager = LinearLayoutManager(this@StoryActivity)
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            stories.observe(this@StoryActivity) { pagingData ->
                handlePagingData(pagingData)
            }

            isLoading.observe(this@StoryActivity) { isLoading ->
                logLoadingState(isLoading)
                showLoading(isLoading)
            }

            errorMessage.observe(this@StoryActivity) { message ->
                message?.let { showToast(it) }
            }
        }
    }

    private fun handlePagingData(pagingData: androidx.paging.PagingData<ListStoryItem>?) {
        if (pagingData != null) {
            Log.d(TAG, "PagingData received: $pagingData")
            storyAdapter.submitData(lifecycle, pagingData)
        } else {
            Log.d(TAG, "PagingData is null")
        }
    }

    private fun logLoadingState(isLoading: Boolean) {
        Log.d(TAG, "isLoading: $isLoading")
    }

    private fun setupClickListeners() {
        binding.fabAddstory.setOnClickListener {
            navigateToAddStory()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.appbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                handleLogout()
                true
            }
            R.id.action_maps -> {
                navigateToMaps()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleLogout() {
        lifecycleScope.launch {
            viewModel.logout()
            navigateToLogin()
        }
    }

    private fun navigateToAddStory() {
        startActivity(Intent(this, StoryAddActivity::class.java))
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    private fun navigateToMaps() {
        lifecycleScope.launch {
            startActivity(Intent(this@StoryActivity, MapsActivity::class.java))
            finish()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}