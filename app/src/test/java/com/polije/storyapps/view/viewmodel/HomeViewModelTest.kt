package com.polije.storyapps.view.viewmodel

import android.content.SharedPreferences
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.polije.storyapps.DataDummy
import com.polije.storyapps.MainDispatcherRule
import com.polije.storyapps.adapter.StoryAdapter
import com.polije.storyapps.data.StoryPagingSource
import com.polije.storyapps.data.StoryRepository
import com.polije.storyapps.getOrAwaitValue
import com.polije.storyapps.model.Story
import org.junit.Assert
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class HomeViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    @get:Rule
    val mainDispatcherRules = MainDispatcherRule()
    @Mock
    private lateinit var storyRepository: StoryRepository
    @Mock
    private lateinit var sharedPreferences: SharedPreferences
    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generateDummyStoryResponse()
        val pagingData = PagingData.from(dummyStories)

        val liveData: LiveData<PagingData<Story>> = MutableLiveData(pagingData)

        Mockito.`when`(sharedPreferences.getString("token", null)).thenReturn("dummy_token")
        Mockito.`when`(storyRepository.getStories("dummy_token")).thenReturn(liveData)

        val viewModel = HomeViewModel(storyRepository, sharedPreferences)

        val observedData = viewModel.getPagedStories().getOrAwaitValue()

        Assert.assertNotNull(observedData)
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback
        )
        differ.submitData(observedData)

        Assert.assertNotNull(differ.snapshot())
        Assert.assertEquals(dummyStories.size, differ.snapshot().size)
        Assert.assertEquals(dummyStories[0], differ.snapshot()[0])
    }
    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val data: PagingData<Story> = PagingData.from(emptyList())
        val expectedStory = MutableLiveData<PagingData<Story>>()
        expectedStory.value = data

        Mockito.`when`(sharedPreferences.getString("token", null)).thenReturn("dummy_token")
        Mockito.`when`(storyRepository.getStories("dummy_token")).thenReturn(expectedStory)

        val viewModel = HomeViewModel(storyRepository, sharedPreferences)
        val observedData = viewModel.getPagedStories().getOrAwaitValue()

        Assert.assertNotNull(observedData)
        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback
        )
        differ.submitData(observedData)
        Assert.assertEquals(0, differ.snapshot().size)
    }
}
class StoryPagingSource : PagingSource<Int, LiveData<List<Story>>>() {
    companion object {
        fun snapshot(items: List<Story>): PagingData<Story> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LiveData<List<Story>>>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LiveData<List<Story>>> {
        return LoadResult.Page(emptyList(), 0, 1)
    }
}
val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}