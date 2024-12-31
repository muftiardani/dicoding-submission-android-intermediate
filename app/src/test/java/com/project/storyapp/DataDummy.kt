package com.project.storyapp

import com.project.storyapp.data.response.ListStoryItem
import com.project.storyapp.data.response.StoryResponse

object DataDummy {

    fun generateDummyStoryResponse(): StoryResponse {
        val stories: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                id = "story-id",
                name = "User $i",
                description = "Lorem Ipsum $i",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1641623658595_dummy-pic.png",
                createdAt = "2022-01-08T06:34:18.598Z",
                lat = -10.212,
                lon = -16.002
            )
            stories.add(story)
        }
        return StoryResponse(
            error = false,
            message = "Stories fetched successfully",
            listStory = stories
        )
    }
}