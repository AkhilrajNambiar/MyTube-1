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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytube.R
import com.example.mytube.adapters.PlaylistDetailScreenAdapter
import com.example.mytube.models.VideoInPlaylistDetail
import com.example.mytube.db.SearchDatabase
import com.example.mytube.models.SinglePlaylist
import com.example.mytube.repository.VideosRepository
import com.example.mytube.util.Resource
import com.example.mytube.util.VideoViewsFormatter

class PlaylistDetailActivity : AppCompatActivity() {

    lateinit var playlist: SinglePlaylist
    lateinit var viewModel: ChannelViewModel
    lateinit var detailAdapter: PlaylistDetailScreenAdapter
    lateinit var recyclerView: RecyclerView
    var totalPlaylistItems: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlist_detail)

        playlist = intent.getBundleExtra("playlist")?.getSerializable("playlist") as SinglePlaylist

        totalPlaylistItems = playlist.contentDetails.itemCount

        val repository = VideosRepository(SearchDatabase.getSearchDatabase(this))
        val viewModelFactory = ChannelViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ChannelViewModel::class.java)

        viewModel.equatableLists = mutableListOf(playlist)
        viewModel.getPlaylistItemsForDetailScreen(playlistId = playlist.id, null)

        detailAdapter = PlaylistDetailScreenAdapter(viewModel)
        recyclerView = findViewById(R.id.playlist_detail_recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@PlaylistDetailActivity)
            adapter = detailAdapter
            addOnScrollListener(this@PlaylistDetailActivity.scrollListener)
        }
        val playlistName = findViewById<TextView>(R.id.playlist_name)
        val progressBar = findViewById<ProgressBar>(R.id.playlist_detail_progress_bar)
        val goBack = findViewById<ImageView>(R.id.go_back_from_playlist_detail)
        val search = findViewById<ImageView>(R.id.search_in_playlist_detail)

        playlistName.text = playlist.snippet.title

        goBack.setOnClickListener {
            finish()
        }

        search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        viewModel.playlistItemsForDetailScreen.observe(this, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    hideProgressBar(progressBar)
                    resource.data?.let {
                        for (item in it.items) {
                            if (!viewModel.playlistItemIds.contains(item.id)) {
                                viewModel.playlistItemIds.add(item.id)
                                viewModel.playlistItems.add(item)
                            }
                        }
                        viewModel.nextPlaylistItemId = it.nextPageToken
                        val playlistItems = viewModel.playlistItems.map { playlistItem ->
                            VideoInPlaylistDetail(
                                videoId = playlistItem.contentDetails.videoId,
                                videoTitle = playlistItem.snippet.title,
                                videoThumbnailUrl = VideoViewsFormatter.obtainThumbnailUrl(playlistItem.snippet.thumbnails),
                                channelTitle = playlistItem.snippet.channelTitle
                            )
                        }
                        viewModel.equatableLists.addAll(playlistItems)
                        detailAdapter.differ.submitList(viewModel.equatableLists.toList())
                    }
                }
                is Resource.Error -> {
                    hideProgressBar(progressBar)
                    Log.e("detail", resource.message.toString())
                }
                is Resource.Loading -> {
                    showProgressBar(progressBar)
                    Log.d("detail", "Loading")
                }
            }
        })
    }

    private var isScrolling = false
    private var isLoading = false

    private fun showProgressBar(bar: ProgressBar) {
        bar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar(bar: ProgressBar) {
        bar.visibility = View.INVISIBLE
        isLoading = false
    }

    private val scrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
                Log.d("detail", "is Scrolling")
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val manager = recyclerView.layoutManager as LinearLayoutManager
            val totalItemCount = manager.itemCount
            val currentItemCount = manager.childCount
            val scrolledItemCount = manager.findFirstVisibleItemPosition()
            // Here we do +1 at last as we have an extra item at the top of the list
            if (isScrolling && totalItemCount == currentItemCount + scrolledItemCount && !isLoading && viewModel.nextPlaylistItemId.isNotEmpty()) {
                viewModel.getPlaylistItemsForDetailScreen(playlist.id, viewModel.nextPlaylistItemId)
                isScrolling = false
                Log.d("detail", "scrolling is finished")
            }
            else {
                Log.d("detail", "ids count: ${viewModel.playlistItemIds.size}, items count: ${viewModel.playlistItems.size}")
                detailAdapter.differ.submitList(viewModel.equatableLists.toList())
                recyclerView.setPadding(0, 0, 0, 0)
            }
        }
    }
}