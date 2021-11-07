package com.example.mytube.UI.Fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.ChannelDetailsActivity
import com.example.mytube.UI.ChannelViewModel
import com.example.mytube.UI.VideoActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.adapters.SectionAdapter
import com.example.mytube.models.*
import com.example.mytube.util.Resource
import com.example.mytube.util.VideoViewsFormatter

class ChannelHomeFragment : Fragment(R.layout.fragment_channel_home) {
    lateinit var viewModel: ChannelViewModel
    lateinit var channelId: String
    lateinit var channelImageUrl: String
    var playListItems: MutableList<PlaylistItem> = mutableListOf()
    lateinit var recyclerView: RecyclerView
    lateinit var sectionAdapter: SectionAdapter
    lateinit var currentSectionTitle: String
    var currentSectionDescription: String = ""
    lateinit var allUploadsPlaylistId: String
    lateinit var nextPageToken:String
    var section: MutableList<PlaylistItemsSection> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as ChannelDetailsActivity).viewModel
        channelId = (activity as ChannelDetailsActivity).channelId
        viewModel.getCompleteChannelDetails(channelId)
        Log.d("playlists1","channelId: ${channelId}")
        sectionAdapter = SectionAdapter(viewModel)
        return inflater.inflate(R.layout.fragment_channel_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mainLayout = view.findViewById<ConstraintLayout>(R.id.channel_home_main_layout)
        val channelBanner = view.findViewById<ImageView>(R.id.channel_banner_image)
        val channelLogo = view.findViewById<ImageView>(R.id.channel_image)
        val channelTitle = view.findViewById<TextView>(R.id.channel_title)
        val channelSubs = view.findViewById<TextView>(R.id.channel_subscribers)
        val channelDescription = view.findViewById<TextView>(R.id.channel_one_line_description)
        val image = view.findViewById<ImageView>(R.id.video_thumbnail)
        val title = view.findViewById<TextView>(R.id.video_title)
        val subtitle = view.findViewById<TextView>(R.id.video_subtitle)
        val channelLogo2 = view.findViewById<ImageView>(R.id.channel_logo)
        val videoCard = view.findViewById<LinearLayout>(R.id.video_card)
        val videoOptions = view.findViewById<ImageView>(R.id.video_options)
        recyclerView = view.findViewById(R.id.playlists_recycler_view)
        val channelTrailerCard = view.findViewById<ConstraintLayout>(R.id.channel_trailer_video_thumbnail)

        recyclerView.apply {
            adapter = sectionAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.channelResponse.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { channelList ->
                        val channel = channelList.items[0]
                        if (channel.brandingSettings.image.bannerExternalUrl.isNotEmpty()) {
                            Log.d("channelBranding", channel.brandingSettings.image.bannerExternalUrl)
                            Glide.with(requireContext()).load(channel.brandingSettings.image.bannerExternalUrl).into(channelBanner)
                        }
                        else {
                            mainLayout.removeView(channelBanner)
                        }
                        if (!channel.statistics.hiddenSubscriberCount) {
                            channelSubs.text = resources.getString(R.string.subscribers, VideoViewsFormatter.viewsFormatter(channel.statistics.subscriberCount))
                        }
                        channelImageUrl = channel.snippet.thumbnails.medium.url
                        Log.d("playlists1", "channelName: ${channel.snippet.title}, channelSubs: ${channel.statistics.subscriberCount}")
                        Glide.with(requireContext()).load(channel.snippet.thumbnails.medium.url).into(channelLogo)
                        channelTitle.text = channel.snippet.title
                        if (channel.snippet.description.isNotEmpty()) {
                            channelDescription.text = channel.snippet.description
                        }
                        if (channel.brandingSettings.channel.unsubscribedTrailer.isNotEmpty()) {
                            viewModel.getVideoDetails(channel.brandingSettings.channel.unsubscribedTrailer)
                        }
                        else {
                            mainLayout.removeView(channelTrailerCard)
                        }
                        allUploadsPlaylistId = channel.contentDetails.relatedPlaylists.uploads
                        // Calling the channelSections only on the first call success
                        // Also this ensures that the value allUploadsPlaylistId is updated correctly
                        viewModel.getChannelSections(channelId)
                    }
                }
                is Resource.Error -> {
                    Log.d("channel", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("channel", "Loading")
                }
            }
        })

        viewModel.singleVideo.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let { videosList ->
                        val video = videosList.items[0]
                        Glide.with(requireContext()).load(video.snippet.thumbnails.maxres?.url ?: video.snippet.thumbnails.high?.url ?: video.snippet.thumbnails.medium?.url ?: video.snippet.thumbnails.standard?.url ?: video.snippet.thumbnails.defaultThumb?.url).into(image)
                        title.text = video.snippet.title
                        subtitle.text = resources.getString(R.string.video_subtitle2, video.snippet.channelTitle, VideoViewsFormatter.viewsFormatter(video.statistics.viewCount.toString()), VideoViewsFormatter.timeFormatter(viewModel.findMillisDifference(video.snippet.publishedAt), requireContext()))
                        Glide.with(requireContext()).load(channelImageUrl).into(channelLogo2)

                        videoCard.setOnClickListener {
                            val intent = Intent(requireContext(), VideoActivity::class.java)
                            intent.putExtra("videoId", video.id)
                            startActivity(intent)
                        }
                    }
                }
                is Resource.Error -> {
                    Log.e("channelTrailer", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("channelTrailer", "loading")
                }
            }
        })

        viewModel.channelSectionsResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        val items = it.items
                        if (items.isEmpty()) {
                            currentSectionTitle = "Uploads"
                            viewModel.getPlaylistItemsForSmallChannels(allUploadsPlaylistId)
                        }
                        for (item in items) {
                            viewModel.setUpChannelHomeSection(item, allUploadsPlaylistId, channelId)
                        }
                    }
                }
                is Resource.Error -> {
                    Log.d("playlists1", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("playlists1", "Loading")
                }
            }
        })
        viewModel.playlistItemsResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        Log.d("playlists1", "playlistItems ${it.items.toString()}")
                        var counter = 0
                        nextPageToken = it.nextPageToken
                        for (item in it.items) {
                            counter++
                            playListItems.add(item)
                            if (counter == 12) {
                                break
                            }
                        }
                        val recentUploads: List<ChannelHomePlaylistItem> = playListItems.map { videoItem ->
                            ChannelHomePlaylistItem(
                                videoId = videoItem.contentDetails.videoId,
                                videoThumbnailUrl = videoItem.snippet.thumbnails.high.url,
                                videoTitle = videoItem.snippet.title,
                                videoPostedTime = videoItem.snippet.publishedAt
                            )
                        }
                        try {
                            val section1 = PlaylistItemsSection(
                                title = "Uploads",
                                description = currentSectionDescription,
                                recentUploads.toSet().toList()
                            )
                            section.add(section1)
                            sectionAdapter.differ.submitList(section.toSet().toList())
                        }
                        catch (e: Exception) {
                            Log.e("playlistsError", e.stackTraceToString())
                        }
                    }
                }
                is Resource.Error -> {
                    Log.d("playlists1", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("playlists1", "Loading")
                }
            }
        })
        viewModel.popularVideosOfChannel.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        var counter = 0
                        val items = mutableListOf<ItemXXXXX>()
                        for (item in it.items) {
                            counter++
                            items.add(item)
                            if (counter == 12) {
                                break
                            }
                        }
                        val popularUploads = items.map {
                            ChannelHomePlaylistItem(
                                videoId = it.id.videoId,
                                videoTitle = it.snippet.title,
                                videoPostedTime = it.snippet.publishedAt,
                                videoThumbnailUrl = it.snippet.thumbnails.high.url
                            )
                        }
                        val section2 = PlaylistItemsSection(
                            title = "Popular Uploads",
                            description = currentSectionDescription,
                            popularUploads.toSet().toList()
                        )
                        section.add(section2)
                        sectionAdapter.differ.submitList(section.toSet().toList())
                    }
                }
                is Resource.Error -> {
                    Log.e("popular", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("popular", "Loading")
                }
            }
        })
    }
}









