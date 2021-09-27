package com.example.mytube.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytube.R
import com.example.mytube.adapters.VideosAdapter
import com.example.mytube.db.SearchDatabase
import com.example.mytube.repository.VideosRepository
import com.example.mytube.util.Resource

class SearchResultsActivity : AppCompatActivity() {
    lateinit var viewModel: VideosViewModel
    lateinit var videosAdapter: VideosAdapter
    var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        searchQuery = intent.getStringExtra("searchQuery").toString()

        val repository = VideosRepository(SearchDatabase.getSearchDatabase(this))
        val viewModelFactory = VideosViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(VideosViewModel::class.java)
        videosAdapter = VideosAdapter(viewModel)
        val recyclerView = findViewById<RecyclerView>(R.id.videos)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchResultsActivity)
            adapter = videosAdapter
            addOnScrollListener(this@SearchResultsActivity.scrollListener)
        }
        val progressBar = findViewById<ProgressBar>(R.id.paginationProgressBar)
        viewModel.getSearchResults(searchQuery)

        viewModel.searchResults.observe(this, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    hideProgressBar(progressBar)
                    resource.data?.let { videoResponse ->
                        if (viewModel.nextSearchPageId != videoResponse.nextPageToken) {
                            viewModel.nextSearchPageId = videoResponse.nextPageToken
                            viewModel.searchedVideos.addAll(videoResponse.items)
//                            videoResponse.items.forEach { viewModel.getChannel(it.snippet.channelId) }
                            Log.d("Channels", viewModel.channels.toString())
                            videosAdapter.differ.submitList(viewModel.searchedVideos.toList())
                        }
                        else {
                            Log.d("Videos", "next token dekh ${viewModel.nextPageId}")
                            videosAdapter.differ.submitList(viewModel.searchedVideos.toList())
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar(progressBar)
                    Log.e("Videos", resource.message.toString())
                }
                is Resource.Loading -> {
                    showProgressBar(progressBar)
                }
            }
        })

        viewModel.channelResponse.observe(this, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { channels ->
                        viewModel.channels.set(channels.items[0].id, channels.items[0])
//                        videosAdapter.differ.submitList(viewModel.videos.toList())
                    }
                }
                is Resource.Error -> {
                    Log.e("Channels", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("Channels", "Waiting")
                }
            }
        })

    }


    private fun hideProgressBar(bar: ProgressBar) {
        bar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(bar: ProgressBar){
        bar.visibility = View.VISIBLE
        isLoading = true
    }

    var isScrolling = false
    var isLoading = false

    val scrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val manager = recyclerView.layoutManager as LinearLayoutManager
            val currentItems = manager.childCount
            val totaltItems = manager.itemCount
            val scrolledItems = manager.findFirstVisibleItemPosition()
            if (isScrolling && totaltItems == currentItems + scrolledItems && !isLoading && viewModel.searchedVideos.size <= 1_000_000) {
                viewModel.getNextSearchResults(searchQuery)
                isScrolling = false
            } else {
                videosAdapter.differ.submitList(viewModel.searchedVideos.toList())
                recyclerView.setPadding(0, 0, 0, 0)
                Log.d("Videos", viewModel.searchedVideos.size.toString())
            }
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d("searched", "Search Results Activity paused")
    }

    override fun onStop() {
        super.onStop()
        Log.d("searched", "Search Results Activity stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("searched", "Search Results Activity destroyed")
    }
}