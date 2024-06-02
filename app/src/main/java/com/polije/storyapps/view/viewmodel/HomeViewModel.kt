package com.polije.storyapps.view.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.polije.storyapps.data.StoryPagingSource
import com.polije.storyapps.data.StoryRepository
import com.polije.storyapps.model.Story
import com.polije.storyapps.network.Config
import kotlinx.coroutines.flow.Flow

class HomeViewModel(private val repository: StoryRepository, private val sharedPreferences: SharedPreferences): ViewModel() {

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun getPagedStories(): LiveData<PagingData<Story>> {
        val currentToken = getTokenFromSharedPreferences()
        return repository.getStories(currentToken).cachedIn(viewModelScope)
    }

    private fun getTokenFromSharedPreferences(): String {
        return sharedPreferences.getString("token", null)
            ?: throw IllegalStateException("Token must be set before fetching stories")
    }

    fun logout() {
        sharedPreferences.edit().clear().apply()
    }

}

class HomeViewModelFactory(
    private val repository: StoryRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, sharedPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
