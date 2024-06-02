package com.polije.storyapps.view.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.polije.storyapps.network.Config
import com.polije.storyapps.model.GetStoriesResponse
import com.polije.storyapps.model.Story
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsViewModel: ViewModel() {
    private val _stories = MutableLiveData<List<Story>>()
    val stories: LiveData<List<Story>> get() = _stories

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    fun fetchStories(token: String) {
        viewModelScope.launch {
            val service = Config.getServices()
            val call = service.getAllStoriesLocation("Bearer $token")
            call.enqueue(object : Callback<GetStoriesResponse> {
                override fun onResponse(call: Call<GetStoriesResponse>, response: Response<GetStoriesResponse>) {
                    if (response.isSuccessful) {
                        _stories.value = response.body()?.listStory ?: emptyList()
                    } else {
                        _error.value = "Failed to fetch stories"
                    }
                }

                override fun onFailure(call: Call<GetStoriesResponse>, t: Throwable) {
                    _error.value = "Error: ${t.message}"
                }
            })
        }
    }
}