package com.project.storyapp.ui.story_detail

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.project.storyapp.R
import com.project.storyapp.data.di.Injector
import com.project.storyapp.data.response.Story
import com.project.storyapp.databinding.ActivityStoryDetailBinding

class StoryDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_STORY_ID = "extra_story_id"
    }

    private lateinit var binding: ActivityStoryDetailBinding

    private val viewModel: StoryDetailViewModel by viewModels {
        Injector.provideStoryDetailViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
        loadStoryData()
        setupObservers()
    }

    private fun setupView() {
        binding = ActivityStoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadStoryData() {
        intent.getStringExtra(EXTRA_STORY_ID)?.let { storyId ->
            viewModel.showDetailStory(storyId)
        }
    }

    private fun setupObservers() {
        with(viewModel) {
            story.observe(this@StoryDetailActivity) { storyResponse ->
                displayStoryDetails(storyResponse)
            }

            isLoading.observe(this@StoryDetailActivity) { isLoading ->
                showLoading(isLoading)
            }

            errorMessage.observe(this@StoryDetailActivity) { message ->
                message?.let { showToast(it) }
            }
        }
    }

    private fun displayStoryDetails(story: Story) {
        with(binding) {
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            loadStoryImage(story.photoUrl)
        }
    }

    private fun loadStoryImage(imageUrl: String?) {
        Glide.with(binding.ivDetailPhoto.context)
            .load(imageUrl)
            .placeholder(R.drawable.img_placeholder)
            .into(binding.ivDetailPhoto)
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}