package com.project.storyapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.project.storyapp.data.response.ListStoryItem
import com.project.storyapp.data.response.StoryResponse
import com.project.storyapp.data.retrofit.ApiService
import retrofit2.HttpException
import java.io.IOException

class StoryPagingSource(
    private val apiService: ApiService
) : PagingSource<Int, ListStoryItem>() {

    override fun getRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return try {
            calculateRefreshKey(state)
        } catch (e: Exception) {
            Log.e(TAG, "Error calculating refresh key: ${e.message}")
            null
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ListStoryItem> {
        val position = params.key ?: INITIAL_PAGE_INDEX

        return try {
            val response = fetchStories(position, params.loadSize)
            createLoadResult(position, response)
        } catch (e: IOException) {
            Log.e(TAG, "Network error loading page $position: ${e.message}")
            LoadResult.Error(e)
        } catch (e: HttpException) {
            Log.e(TAG, "HTTP error loading page $position: ${e.message}")
            LoadResult.Error(e)
        } catch (e: Exception) {
            Log.e(TAG, "Error loading page $position: ${e.message}")
            LoadResult.Error(e)
        }
    }

    private fun calculateRefreshKey(state: PagingState<Int, ListStoryItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.let { anchorPage ->
                anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
            }
        }
    }

    private suspend fun fetchStories(position: Int, loadSize: Int) =
        apiService.getStories(position, loadSize).also {
            Log.d(TAG, "Fetching page $position with size $loadSize")
        }

    private fun createLoadResult(
        position: Int,
        response: StoryResponse
    ): LoadResult<Int, ListStoryItem> {
        val stories = response.listStory
        Log.d(TAG, "Loaded ${stories.size} items for page $position")

        val prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1
        val nextKey = if (stories.isEmpty()) null else position + 1

        Log.d(TAG, "Pagination: prevKey=$prevKey, nextKey=$nextKey")

        return LoadResult.Page(
            data = stories,
            prevKey = prevKey,
            nextKey = nextKey
        )
    }

    companion object {
        private const val INITIAL_PAGE_INDEX = 1
        private const val TAG = "StoryPagingSource"
    }
}