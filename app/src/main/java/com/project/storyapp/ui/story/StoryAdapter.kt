package com.project.storyapp.ui.story

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.storyapp.R
import com.project.storyapp.data.response.ListStoryItem
import com.project.storyapp.databinding.ItemStoryCardBinding
import com.project.storyapp.ui.story_detail.StoryDetailActivity

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemStoryCardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { story ->
            holder.bind(story)
            setupItemClickListener(holder, story)
        }
    }

    private fun setupItemClickListener(holder: ViewHolder, story: ListStoryItem) {
        holder.itemView.setOnClickListener {
            navigateToDetail(holder, story)
        }
    }

    private fun navigateToDetail(holder: ViewHolder, story: ListStoryItem) {
        val context = holder.itemView.context
        val intent = createDetailIntent(context, story.id)
        val optionsCompat = createTransitionAnimation(holder)

        startActivity(context, intent, optionsCompat.toBundle())
    }

    private fun createDetailIntent(context: android.content.Context, storyId: String?): Intent {
        return Intent(context, StoryDetailActivity::class.java).apply {
            putExtra(StoryDetailActivity.EXTRA_STORY_ID, storyId)
        }
    }

    private fun createTransitionAnimation(holder: ViewHolder): ActivityOptionsCompat {
        return ActivityOptionsCompat.makeSceneTransitionAnimation(
            holder.itemView.context as Activity,
            Pair(holder.getPhotoView(), TRANSITION_NAME_PROFILE),
            Pair(holder.getNameView(), TRANSITION_NAME_NAME)
        )
    }

    class ViewHolder(private val binding: ItemStoryCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(storyItem: ListStoryItem) {
            binding.apply {
                tvItemName.text = storyItem.name ?: UNKNOWN_NAME
                loadImage(storyItem.photoUrl)
            }
        }

        private fun loadImage(url: String?) {
            Glide.with(binding.ivItemPhoto.context)
                .load(url)
                .into(binding.ivItemPhoto)
        }

        fun getPhotoView() = itemView.findViewById<android.view.View>(R.id.ivItemPhoto)
        fun getNameView() = itemView.findViewById<android.view.View>(R.id.tvItemName)
    }

    companion object {
        private const val UNKNOWN_NAME = "Unknown"
        private const val TRANSITION_NAME_PROFILE = "profile"
        private const val TRANSITION_NAME_NAME = "name"

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}