package com.example.mytube.UI.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytube.R
import com.example.mytube.UI.ChannelDetailsActivity
import com.example.mytube.UI.ChannelViewModel
import com.example.mytube.adapters.PlaylistsAdapter
import com.example.mytube.models.SinglePlaylist
import com.example.mytube.util.Resource
import com.google.android.youtube.player.internal.r

class ChannelPlaylistsFragment : Fragment(R.layout.fragment_channel_playlists) {
    lateinit var viewModel: ChannelViewModel
    lateinit var channelId: String
    lateinit var playlistAdapter: PlaylistsAdapter
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as ChannelDetailsActivity).viewModel
        channelId = (activity as ChannelDetailsActivity).channelId
        viewModel.getAllPlaylistsForChannel(channelId, null)
        Log.d("playlistsChannelId", channelId)
        return inflater.inflate(R.layout.fragment_channel_playlists, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.channel_playlists_recycler_view)
        val progressBar = view.findViewById<ProgressBar>(R.id.channel_playlists_progress_bar)
        val noPlaylistsSection = view.findViewById<ConstraintLayout>(R.id.no_playlists_box)

        playlistAdapter = PlaylistsAdapter(viewModel)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = playlistAdapter
            addOnScrollListener(this@ChannelPlaylistsFragment.scrollListener)
        }

        viewModel.allPlaylistsResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    hideProgressBar(progressBar)
                    resource.data?.let {
                        viewModel.nextPlaylistPage = it.nextPageToken
                        Log.d("playlistsImages", it.items.map { pl -> pl.snippet.thumbnails }.toString())
                        if (it.items.isNotEmpty()) {
                            hideNoPlaylistsBox(noPlaylistsSection)
                            for (playlist in it.items) {
                                Log.d("playlistsImage", playlist.snippet.thumbnails.high.url)
                                if (playlistIsNotDuplicateAndContainsVideos(playlist)) {
                                    viewModel.allPlaylists.add(playlist)
                                    viewModel.allPlaylistIds.add(playlist.id)
                                }
                            }
                            playlistAdapter.differ.submitList(viewModel.allPlaylists.toList())
                        }
                        else {
                            showNoPlaylistsBox(noPlaylistsSection)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar(progressBar)
                    Log.e("playlistsOnly", resource.message.toString())
                }
                is Resource.Loading -> {
                    showProgressBar(progressBar)
                    Log.d("playlistsOnly", "Loading")
                }
            }
        })
    }

    private var isScrolling = false
    private var isLoading = false

    private fun hideProgressBar(bar: ProgressBar) {
        bar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(bar: ProgressBar) {
        bar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun showNoPlaylistsBox(box: ConstraintLayout) {
        box.visibility = View.VISIBLE
    }

    private fun hideNoPlaylistsBox(box: ConstraintLayout) {
        box.visibility = View.INVISIBLE
    }

    var scrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val manager = recyclerView.layoutManager as LinearLayoutManager
            val totalItems = manager.itemCount
            val currentItems = manager.childCount
            val scrolledItems = manager.findFirstVisibleItemPosition()
            if (isScrolling && totalItems == currentItems + scrolledItems && !isLoading) {
                viewModel.getAllPlaylistsForChannel(channelId, viewModel.nextPlaylistPage)
            }
        }
    }

    private fun playlistIsNotDuplicateAndContainsVideos(playlist: SinglePlaylist): Boolean {
        if (!viewModel.allPlaylistIds.contains(playlist.id) && playlist.contentDetails.itemCount > 0) {
            return true
        }
        return false
    }
}