package com.example.mytube.UI.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.PopupMenu
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytube.R
import com.example.mytube.UI.ChannelDetailsActivity
import com.example.mytube.UI.ChannelViewModel
import com.example.mytube.UI.VideoActivity
import com.example.mytube.adapters.PlaylistVideosAdapter
import com.example.mytube.adapters.RelatedVideosAdapter
import com.example.mytube.models.ChannelHomePlaylistItem
import com.example.mytube.util.Constants
import com.example.mytube.util.Constants.Companion.DATE_DESCENDING
import com.example.mytube.util.Constants.Companion.MOST_POPULAR
import com.example.mytube.util.Resource

class ChannelVideosFragment : Fragment(R.layout.fragment_channel_videos) {

    lateinit var viewModel: ChannelViewModel
    lateinit var channelId: String
    lateinit var videosAdapter: PlaylistVideosAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var recentUploadsPlaylist: String
    var totalVideos: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as ChannelDetailsActivity).viewModel
        channelId = (activity as ChannelDetailsActivity).channelId
        videosAdapter = PlaylistVideosAdapter(viewModel)
        viewModel.getCompleteChannelDetails(channelId)
        return inflater.inflate(R.layout.fragment_channel_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val sortVideosBy = view.findViewById<ConstraintLayout>(R.id.sort_videos)
        recyclerView = view.findViewById(R.id.channel_videos_recycler_view)
        val progressBar = view.findViewById<ProgressBar>(R.id.channel_videos_progress_bar)

        sortVideosBy.setOnClickListener {
            val popup = PopupMenu(requireContext(), sortVideosBy)

            popup.inflate(R.menu.channel_videos_sort_by)
            popup.setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.most_popular -> {
                        if (viewModel.currentChannelVideosSortFormat != MOST_POPULAR){
                            viewModel.currentChannelVideos.value = listOf()
                            viewModel.currentChannelVideosSortFormat = MOST_POPULAR
                            viewModel.getAllPopularVideosOfChannel(channelId, null)
                        }
                        else {
                            viewModel.getAllPopularVideosOfChannel(channelId, null)
                        }
                        true
                    }
                    R.id.date_added_descending -> {
                        if (viewModel.currentChannelVideosSortFormat != DATE_DESCENDING) {
                            viewModel.currentChannelVideos.value = listOf()
                            viewModel.currentChannelVideosSortFormat = DATE_DESCENDING
                            viewModel.getRecentChannelVideos(recentUploadsPlaylist, null)
                        }
                        else {
                            viewModel.getRecentChannelVideos(recentUploadsPlaylist, null)
                        }
                        true
                    }
                    else -> true
                }
            }
            popup.show()
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = videosAdapter
            addOnScrollListener(this@ChannelVideosFragment.scrollListener)
        }


        viewModel.channelResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let { channelFullDetails ->
                        recentUploadsPlaylist = channelFullDetails.items[0].contentDetails.relatedPlaylists.uploads
                        viewModel.getRecentChannelVideos(recentUploadsPlaylist, null)
                    }
                }
                is Resource.Error -> {
                    Log.e("channelVideos", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("channelVideos", "Loading")
                }
            }
        })

        viewModel.recentChannelVideos.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    hideProgressBar(progressBar)
                    resource.data?.let {
                        for (video in it.items) {
                            if (!viewModel.recentUploadIds.contains(video.id)) {
                                viewModel.recentUploads.add(video)
                                viewModel.recentUploadIds.add(video.id)
                            }
                        }
                        totalVideos = it.pageInfo.totalResults
                        viewModel.nextRecentUploadsId = it.nextPageToken
                        val recentUploads = viewModel.recentUploads.map { item ->
                            ChannelHomePlaylistItem(
                                videoId = item.contentDetails.videoId,
                                videoThumbnailUrl = item.snippet.thumbnails.high.url,
                                videoTitle = item.snippet.title,
                                videoPostedTime = item.snippet.publishedAt
                            )
                        }
                        viewModel.currentChannelVideos.value = recentUploads
                    }
                }
                is Resource.Error -> {
                    hideProgressBar(progressBar)
                    Log.e("channelVideos", resource.message.toString())
                }
                is Resource.Loading -> {
                    showProgressBar(progressBar)
                    Log.d("channelVideos", "Loading")
                }
            }
        })

        viewModel.allPopularVideosOfChannel.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    hideProgressBar(progressBar)
                    resource.data?.let {
                        for (video in it.items) {
                            if (!viewModel.popularUploadIds.contains(video.id.videoId)) {
                                viewModel.popularUploads.add(video)
                                viewModel.popularUploadIds.add(video.id.videoId)
                            }
                        }
                        viewModel.nextPopularUploadsId = it.nextPageToken
                        val popularVideos = viewModel.popularUploads.map { pop ->
                            ChannelHomePlaylistItem(
                                videoId = pop.id.videoId,
                                videoTitle = pop.snippet.title,
                                videoPostedTime = pop.snippet.publishedAt,
                                videoThumbnailUrl = pop.snippet.thumbnails.high.url
                            )
                        }
                        viewModel.currentChannelVideos.value = popularVideos
                    }
                }
                is Resource.Error -> {
                    Log.e("channelPopularVideos", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("channelPopularVideos", "loading")
                }
            }
        })

        viewModel.currentChannelVideos.observe(viewLifecycleOwner, Observer { ll ->
            videosAdapter.differ.submitList(ll)
        })
    }

    private var isLoading = false
    private var isScrolling = false

    private fun showProgressBar(bar: ProgressBar) {
        bar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideProgressBar(bar: ProgressBar) {
        bar.visibility = View.INVISIBLE
        isLoading = false
    }

    val scrollListener = object: RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
                Log.d("scrolling", "Channel Videos is scrolling")
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val manager = recyclerView.layoutManager as LinearLayoutManager
            val currentItems = manager.childCount
            val totalItems = manager.itemCount
            val scrolledItems = manager.findFirstVisibleItemPosition()
            if (isScrolling && totalItems == currentItems + scrolledItems && viewModel.recentUploads.size <= totalVideos) {
                if (viewModel.currentChannelVideosSortFormat == MOST_POPULAR) {
                    viewModel.getAllPopularVideosOfChannel(channelId, viewModel.nextPopularUploadsId)
                }
                else if (viewModel.currentChannelVideosSortFormat == DATE_DESCENDING) {
                    viewModel.getRecentChannelVideos(recentUploadsPlaylist, viewModel.nextRecentUploadsId)
                }
                isScrolling = false
                Log.d("scrolling", "Total items : $totalVideos")
                Log.d("scrolling", "channel videos scrolled")
                Log.d("scrolling", "NextPageid: ${viewModel.nextRecentUploadsId}")
            }
            else {
                if (viewModel.currentChannelVideosSortFormat == DATE_DESCENDING) {
                    val recent = viewModel.recentUploads.map {
                        ChannelHomePlaylistItem(
                            videoId = it.contentDetails.videoId,
                            videoThumbnailUrl = it.snippet.thumbnails.high.url,
                            videoTitle = it.snippet.title,
                            videoPostedTime = it.snippet.publishedAt
                        )
                    }
                    viewModel.currentChannelVideos.value = recent
                }
                else if (viewModel.currentChannelVideosSortFormat == MOST_POPULAR) {
                    val pop = viewModel.popularUploads.map {
                        ChannelHomePlaylistItem(
                            videoId = it.id.videoId,
                            videoThumbnailUrl = it.snippet.thumbnails.high.url,
                            videoTitle = it.snippet.title,
                            videoPostedTime = it.snippet.publishedAt
                        )
                    }
                    viewModel.currentChannelVideos.value = pop
                }
                recyclerView.setPadding(0,0,0,0)
            }
        }
    }

}