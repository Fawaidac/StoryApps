package com.polije.storyapps.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.polije.storyapps.model.Story
import com.polije.storyapps.network.Service
import retrofit2.HttpException
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StoryPagingSource(private val service: Service,private val token: String): PagingSource<Int, Story>() {

    companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, Story>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Story> {
        val position = params.key ?: INITIAL_PAGE_INDEX
        return try {
            val response = withContext(Dispatchers.IO) {
                service.getAllStories(
                    token = "Bearer $token",
                    page = position,
                    size = params.loadSize
                ).execute()
            }

            if (response.isSuccessful) {
                val stories = response.body()?.listStory ?: emptyList()
                LoadResult.Page(
                    data = stories,
                    prevKey = if (position == INITIAL_PAGE_INDEX) null else position - 1,
                    nextKey = if (stories.isEmpty()) null else position + 1
                )
            } else {
                LoadResult.Error(IOException("Failed to fetch data"))
            }
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}