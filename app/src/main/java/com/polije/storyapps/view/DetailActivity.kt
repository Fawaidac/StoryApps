package com.polije.storyapps.view

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.polije.storyapps.R
import com.polije.storyapps.network.Config
import com.polije.storyapps.model.StoryDetailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailActivity : AppCompatActivity() {

    private lateinit var photoImageView: ImageView
    private lateinit var nameTextView: TextView
    private lateinit var descTextView: TextView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        photoImageView = findViewById(R.id.iv_detail_photo)
        nameTextView = findViewById(R.id.tv_detail_name)
        descTextView = findViewById(R.id.tv_detail_description)

        val id = intent.getStringExtra("id")
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        loadStoryDetail(token.toString(), id.toString())
    }
    private fun loadStoryDetail(token: String, storyId: String) {
        val service = Config.getServices()

        service.getStoryDetail("Bearer $token", storyId).enqueue(object :
            Callback<StoryDetailResponse> {
            override fun onResponse(call: Call<StoryDetailResponse>, response: Response<StoryDetailResponse>) {
                if (response.isSuccessful) {
                    val storyDetailResponse = response.body()
                    if (storyDetailResponse != null && !storyDetailResponse.error) {
                        val story = storyDetailResponse.story
                        nameTextView.text = story.name
                        descTextView.text = story.description
                        Glide.with(this@DetailActivity)
                            .load(story.photoUrl)
                            .into(photoImageView)
                    } else {
                        showToast("Failed to load story detail: ${storyDetailResponse?.message}")
                    }
                } else {
                    showToast("Failed to load story detail")
                }
            }

            override fun onFailure(call: Call<StoryDetailResponse>, t: Throwable) {
                showToast("Failed to load story detail: ${t.message}")
            }
        })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}