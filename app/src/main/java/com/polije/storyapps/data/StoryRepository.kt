package com.polije.storyapps.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.polije.storyapps.db.StoryDatabase
import com.polije.storyapps.model.Story
import com.polije.storyapps.network.Service
import kotlinx.coroutines.flow.Flow

class StoryRepository(private val service: Service) {
    fun getStories(token: String): LiveData<PagingData<Story>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoryPagingSource(service, token) }
        ).liveData
    }
}