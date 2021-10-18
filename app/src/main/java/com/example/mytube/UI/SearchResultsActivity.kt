package com.example.mytube.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytube.R
import com.example.mytube.adapters.SearchedVideosAdapter
import com.example.mytube.adapters.VideosAdapter
import com.example.mytube.db.SearchDatabase
import com.example.mytube.repository.VideosRepository
import com.example.mytube.util.Resource
import kotlinx.coroutines.launch

class SearchResultsActivity : AppCompatActivity() {
    lateinit var viewModel: SearchedVideosViewModel
    lateinit var videosAdapter: SearchedVideosAdapter
    var searchQuery = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        searchQuery = intent.getStringExtra("searchQuery").toString()
        Log.d("searched", "searchQuery is $searchQuery")


//        searchQuery = intent.getStringExtra("searchQuery").toString()

        val repository = VideosRepository(SearchDatabase.getSearchDatabase(this))
        val viewModelFactory = SearchedVideosViewModelProviderFactory(repository, searchQuery)
        viewModel = ViewModelProvider(this,viewModelFactory).get(SearchedVideosViewModel::class.java)

        Log.d("searched", "initialised viewModel")

        videosAdapter = SearchedVideosAdapter(viewModel)
        Log.d("searched", "initialised videosAdapter")
        val recyclerView = findViewById<RecyclerView>(R.id.videos)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchResultsActivity)
            adapter = videosAdapter
            addOnScrollListener(this@SearchResultsActivity.scrollListener)
        }
        Log.d("searched", "set up the recycler view")

        val progressBar = findViewById<ProgressBar>(R.id.paginationProgressBar)
        val searchBtn = findViewById<ImageView>(R.id.search)

        searchBtn.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        Log.d("searched", "started searching")

        viewModel.searchResults.observe(this, androidx.lifecycle.Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    Log.d("searched", "Started the success section!")
                    hideProgressBar(progressBar)
                    resource.data?.let { videoResponse ->
                        if (viewModel.nextSearchPageId != videoResponse.nextPageToken) {
                            viewModel.nextSearchPageId = videoResponse.nextPageToken
                            viewModel.searchedVideos.addAll(videoResponse.items)
                            viewModel.searchedVideos.forEach {
                                viewModel.getVideoDetails(it.id.videoId)
                            }
//                            videoResponse.items.forEach { viewModel.getChannel(it.snippet.channelId) }

                            Log.d(
                                "searched",
                                "videos List = ${viewModel.searchedVideoDetails.toList()}"
                            )
//                            videosAdapter.differ.submitList(viewModel.searchedVideoDetails.toList())
                        }
//                        else {
//                            Log.d("Videos", "next token dekh ${viewModel.nextSearchPageId}")
//                            videosAdapter.differ.submitList(viewModel.searchedVideoDetails.toList())
//                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar(progressBar)
                    Log.e("Videos", resource.message.toString())
                }
                is Resource.Loading -> {
                    showProgressBar(progressBar)
                    Log.d("searched", "Loading was done")
                }
            }
        })

        viewModel.singleVideo.observe(this, androidx.lifecycle.Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    hideProgressBar(progressBar)
                    resource.data?.let { videoResponse ->
                        if (!viewModel.searchedVideoDetails.contains(videoResponse.items[0])) {
                            viewModel.searchedVideoDetails.add(videoResponse.items[0])
                        }
                        Log.d("searched", "added ${viewModel.searchedVideoDetails.toList()}")
                        videosAdapter.differ.submitList(viewModel.searchedVideoDetails.toList())
                    }
                }
                is Resource.Loading -> {
                    showProgressBar(bar = progressBar)
                }
                is Resource.Error -> {
                    hideProgressBar(bar = progressBar)
                }
            }
        })

        viewModel.searchedResultsChannelResponse.observe(this,
            androidx.lifecycle.Observer { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { channels ->
                            viewModel.searchedResultchannels.set(
                                channels.items[0].id,
                                channels.items[0]
                            )
                            Log.d("searched", viewModel.searchedResultchannels.toString())
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
                videosAdapter.differ.submitList(viewModel.searchedVideoDetails.toList())
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