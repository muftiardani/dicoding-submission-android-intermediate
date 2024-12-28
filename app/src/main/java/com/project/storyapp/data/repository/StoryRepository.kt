package com.project.storyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.project.storyapp.data.StoryPagingSource
import com.project.storyapp.data.response.ListStoryItem
import com.project.storyapp.data.retrofit.ApiService

class StoryRepository private constructor(
    private val apiService: ApiService
) {
    companion object {
        private const val ITEMS_PER_PAGE = 5

        @Volatile
        private var instance: StoryRepository? = null

        fun getInstance(
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService).also {
                    instance = it
                }
            }
    }

    fun getStories(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false,
                initialLoadSize = ITEMS_PER_PAGE * 2
            ),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
            }
        ).liveData
    }

    private fun getDefaultPageConfig(): PagingConfig =
        PagingConfig(
            pageSize = ITEMS_PER_PAGE,
            enablePlaceholders = false,
            initialLoadSize = ITEMS_PER_PAGE * 2,
            prefetchDistance = ITEMS_PER_PAGE,
            maxSize = PagingConfig.MAX_SIZE_UNBOUNDED
        )
}