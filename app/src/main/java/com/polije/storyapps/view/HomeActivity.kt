package com.polije.storyapps.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityOptionsCompat
import com.polije.storyapps.network.Config
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.polije.storyapps.R
import com.polije.storyapps.adapter.OnItemClickListener
import com.polije.storyapps.adapter.StoryAdapter
import com.polije.storyapps.adapter.StoryLoadStateAdapter
import com.polije.storyapps.data.StoryRepository
import com.polije.storyapps.model.Story
import com.polije.storyapps.view.viewmodel.HomeViewModel
import com.polije.storyapps.view.viewmodel.HomeViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity(), OnItemClickListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var storyAdapter: StoryAdapter
    private lateinit var addButton: AppCompatButton
    private lateinit var logoutButton: AppCompatButton
    private lateinit var mapsButton: ImageView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        addButton = findViewById(R.id.btn_add)
        logoutButton = findViewById(R.id.btn_logout)
        mapsButton = findViewById(R.id.iv_maps)
        recyclerView = findViewById(R.id.rv_story)

        val repository = StoryRepository(Config.getServices())
        val viewModelFactory = HomeViewModelFactory(repository, sharedPreferences)
        homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]

        setupRecyclerView()
        fetchPagedStories()

        addButton.setOnClickListener {
            startActivity(Intent(this, AddActivity::class.java))
        }

        logoutButton.setOnClickListener {
            homeViewModel.logout()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        mapsButton.setOnClickListener {
            startActivity(Intent(this, MapsActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        storyAdapter = StoryAdapter(this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = storyAdapter.withLoadStateFooter(
                footer = StoryLoadStateAdapter { storyAdapter.retry() }
            )
        }
    }

    private fun fetchPagedStories() {
        homeViewModel.getPagedStories().observe(this, Observer { pagingData ->
            pagingData?.let {
                lifecycleScope.launch {
                    storyAdapter.submitData(it)
                }
            }
        })
        homeViewModel.errorMessage.observe(this, Observer { errorMessage ->
            errorMessage?.let {
                showToast(it)
            }
        })
    }

    override fun onItemClick(story: Story) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("id", story.id)
        startActivity(intent, ActivityOptionsCompat.makeSceneTransitionAnimation(this).toBundle())
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
