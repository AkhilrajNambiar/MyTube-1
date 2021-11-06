package com.example.mytube.UI.Fragments

import android.app.DownloadManager
import android.content.*
import android.content.Context.DOWNLOAD_SERVICE
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.ChannelDetailsActivity
import com.example.mytube.UI.SearchedVideosViewModel
import com.example.mytube.UI.VideoActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.adapters.RelatedVideosAdapter
import com.example.mytube.adapters.SearchedVideosAdapter
import com.example.mytube.adapters.VideosAdapter
import com.example.mytube.db.LikedVideoItem
import com.example.mytube.db.WatchHistoryItem
import com.example.mytube.db.WatchLaterItem
import com.example.mytube.models.AboutVideo
import com.example.mytube.util.Resource
import com.example.mytube.util.VideoViewsFormatter
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT
import com.google.android.material.snackbar.Snackbar
import com.google.android.youtube.player.internal.c
import java.io.File

class VideoDataFragment : Fragment(R.layout.fragment_video_data) {

    lateinit var viewModel: VideosViewModel
    lateinit var relatedVideosAdapter: RelatedVideosAdapter
    lateinit var video: AboutVideo
    lateinit var videoId: String
    var totalRelatedVideos: Int = 0
    var downloadId: Long = 0
    lateinit var likedVideos: List<LikedVideoItem>
    lateinit var likedVideoIds: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as VideoActivity).viewModel

        videoId = (activity as VideoActivity).videoId

        viewModel.getSingleRecyclerViewVideo(videoId)

        return inflater.inflate(R.layout.fragment_video_data, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        val videoViews = view.findViewById<TextView>(R.id.video_views)
        val videoPublishedDate = view.findViewById<TextView>(R.id.video_published_date)
        val videoLikes = view.findViewById<TextView>(R.id.like_text)
        val videoLikeBox = view.findViewById<LinearLayout>(R.id.like_video)
        val videoLikeImage = view.findViewById<ImageView>(R.id.like_image)
        val videoDislikes = view.findViewById<TextView>(R.id.dislike_text)
        val videoTitle = view.findViewById<TextView>(R.id.video_title_in_video_screen)
        val shareVideo = view.findViewById<LinearLayout>(R.id.share_video)
        val saveToWatchLater = view.findViewById<LinearLayout>(R.id.save_video)
        val saveImage = view.findViewById<ImageView>(R.id.save_image)
        val saveText = view.findViewById<TextView>(R.id.save_text)
        val dropdown = view.findViewById<ImageView>(R.id.description_dropdown)
        val relatedVideos = view.findViewById<RecyclerView>(R.id.related_videos_recycler_view)
        val channelInfo = view.findViewById<LinearLayout>(R.id.channel_info)

        val channelLogo = view.findViewById<ImageView>(R.id.channel_logo_video_data)
        val channelTitle = view.findViewById<TextView>(R.id.channel_name_video_data)
        val channelSubs = view.findViewById<TextView>(R.id.channel_subscribers_video_data)
        val channelTitleWithHiddenSubs = view.findViewById<TextView>(R.id.channel_name_if_subs_hidden)
        val commentsCount = view.findViewById<TextView>(R.id.comments_count)
        val goToComments = view.findViewById<ConstraintLayout>(R.id.go_to_comments)
        val progressBar = view.findViewById<ProgressBar>(R.id.related_videos_progress_bar)

        viewModel.recyclerViewVideo.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        if (it.items.isEmpty()) {
                            Log.e("videos", it.toString())
                        }
                        else {
                            video = it.items[0]
                        }

                        viewModel.getChannel(video.snippet.channelId)
                        viewModel.getCommentsForVideo(videoId)
                        viewModel.getVideosRelatedToCurrentVideo(videoId = listOf(video.snippet.title, video.snippet.channelTitle).random(), null)

                        // Setting up the initial like image and like text
                        viewModel.getLikedVideos().observe(viewLifecycleOwner, Observer { likedVideos ->
                            val videoIds = likedVideos.map {
                                it.videoId
                            }
                            this@VideoDataFragment.likedVideos = likedVideos
                            this@VideoDataFragment.likedVideoIds = videoIds
                            if (videoIds.contains(videoId)) {
                                videoLikeImage.setImageResource(R.drawable.ic_liked_already)
                                videoLikes.text = VideoViewsFormatter.viewsFormatter((video.statistics.likeCount + 1).toString())
                            }
                            else {
                                videoLikeImage.setImageResource(R.drawable.ic_like_video)
                                videoLikes.text = VideoViewsFormatter.viewsFormatter((video.statistics.likeCount).toString())
                            }
                        })

                        // Adding video to search history
                        val recentVideo = WatchHistoryItem(
                            videoId = videoId,
                            videoTitle = video.snippet.title,
                            videoThumbnailUrl = video.snippet.thumbnails.maxres?.url ?: video.snippet.thumbnails.standard?.url ?: video.snippet.thumbnails.high?.url ?: video.snippet.thumbnails.medium?.url ?: video.snippet.thumbnails.defaultThumb!!.url,
                            videoChannelName = video.snippet.channelTitle,
                            videoViews = video.statistics.viewCount,
                            videoWatchedTime = System.currentTimeMillis()
                        )
                        viewModel.insertVideoIntoWatchHistory(recentVideo)

                        // Adding video to watch later
                        saveToWatchLater.setOnClickListener {
                            saveText.text = resources.getString(R.string.saved)
                            saveImage.setImageResource(R.drawable.ic_added_to_playlist)
                            val watchLaterVideo = WatchLaterItem(
                                videoId = videoId,
                                videoTitle = video.snippet.title,
                                videoThumbnailUrl = video.snippet.thumbnails.maxres?.url ?: video.snippet.thumbnails.standard?.url ?: video.snippet.thumbnails.high?.url ?: video.snippet.thumbnails.medium?.url ?: video.snippet.thumbnails.defaultThumb!!.url,
                                videoChannelName = video.snippet.channelTitle,
                                videoViews = video.statistics.viewCount.toLong(),
                                videoAddedTime = System.currentTimeMillis(),
                                videoPublishedDate = VideoViewsFormatter.getMillisecondsFromLocalTime(video.snippet.publishedAt)
                            )
                            viewModel.insertVideoIntoWatchLater(watchLaterVideo)
                            Snackbar.make(view, "Video added to watch later!", LENGTH_LONG).show()
                        }

                        // Adding video to liked videos
                        videoLikeBox.setOnClickListener {
                            // If video is already liked
                            if (likedVideoIds.contains(videoId)) {
                                val position = likedVideoIds.indexOf(videoId)
                                viewModel.deleteVideoFromLikedVideos(likedVideos[position])
                                videoLikes.text = VideoViewsFormatter.viewsFormatter(video.statistics.likeCount.toString())
                                videoLikeImage.setImageResource(R.drawable.ic_like_video)
                                Snackbar.make(view, "Video removed from Liked Videos", LENGTH_SHORT).show()
                            }
                            else {
                                // If video is not liked
                                videoLikes.text = VideoViewsFormatter.viewsFormatter((video.statistics.likeCount + 1).toString())
                                videoLikeImage.setImageResource(R.drawable.ic_liked_already)
                                val likedVideo = LikedVideoItem(
                                    videoId = videoId,
                                    videoTitle = video.snippet.title,
                                    videoThumbnailUrl = video.snippet.thumbnails.maxres?.url ?: video.snippet.thumbnails.standard?.url ?: video.snippet.thumbnails.high?.url ?: video.snippet.thumbnails.medium?.url ?: video.snippet.thumbnails.defaultThumb!!.url,
                                    videoChannelName = video.snippet.channelTitle,
                                    videoAddedTime = System.currentTimeMillis(),
                                    likeCount = (video.statistics.likeCount + 1).toLong()
                                )
                                viewModel.insertVideoToLikedVideos(likedVideo)
                                Snackbar.make(view, "Video added to Liked Videos", LENGTH_SHORT).show()
                            }
                        }

                        videoTitle.text = video.snippet.title
                        videoViews.text = "${VideoViewsFormatter.viewsFormatter((video.statistics.viewCount).toString())} views . "
                        val videoPublishedTime = viewModel.findMillisDifference(video.snippet.publishedAt)
                        videoPublishedDate.text = VideoViewsFormatter.timeFormatter(videoPublishedTime, view.context).toString()
                        videoDislikes.text = VideoViewsFormatter.viewsFormatter((video.statistics.dislikeCount).toString())

                        val folder = File(Environment.DIRECTORY_DOWNLOADS)

                        relatedVideosAdapter = RelatedVideosAdapter(viewModel)
                        relatedVideos.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = relatedVideosAdapter
                            addOnScrollListener(scrollListener)
                        }
                        relatedVideos.setHasFixedSize(true)

                        // Use the nestedscrollview instead of recyclerview scrolling
//                        nestedScrollView.setOnScrollChangeListener(object: NestedScrollView.OnScrollChangeListener{
//                            override fun onScrollChange(
//                                v: NestedScrollView,
//                                scrollX: Int,
//                                scrollY: Int,
//                                oldScrollX: Int,
//                                oldScrollY: Int
//                            ) {
//                                if (v.getChildAt(v.childCount - 1) != null) {
//                                    if (scrollY >= (v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight) && scrollY > oldScrollY){
//                                        Log.d("relatedVideos", "scrolling is happening")
//                                        viewModel.getVideosRelatedToCurrentVideo(video.id, viewModel.nextPageToken)
//                                    }
//                                }
//                            }
//                        })

                        // going to the channel Page
                        channelInfo.setOnClickListener {
                            val intent = Intent(requireContext(), ChannelDetailsActivity::class.java)
                            intent.putExtra("channelId", video.snippet.channelId)
                            intent.putExtra("channelTitle", video.snippet.channelTitle)
                            startActivity(intent)
                        }

                        // Sharing the video
                        shareVideo.setOnClickListener {
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "text/plain"
                            intent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=${video.id}")
                            try {
//                 If sharing apps like whatsapp and gmail are available
                                startActivity(intent)
                            }
                            catch (e: ActivityNotFoundException) {
                                // else go to google playstore for downloading whatsapp in this case
                                val intent2 = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"))
                                startActivity(intent2)
                            }
                        }


                        /*
                        * Opening the description dropdown sheet
                        * */

                        val videoTitleAndDropdown = view.findViewById<ConstraintLayout>(R.id.video_title_and_description_dropdown)
                        videoTitleAndDropdown.setOnClickListener {
                            VideoDescriptionSheet(video).show(childFragmentManager, "videoDescription")
                        }
                        val videoViewsAndTime = view.findViewById<ConstraintLayout>(R.id.video_views_and_time)
                        videoViewsAndTime.setOnClickListener {
                            VideoDescriptionSheet(video).show(childFragmentManager, "videoDescription")
                        }

                        videoTitle.setOnClickListener {
                            VideoDescriptionSheet(video).show(childFragmentManager, "videoDescription")
                        }
                        dropdown.setOnClickListener {
                            VideoDescriptionSheet(video).show(childFragmentManager, "videoDescription")
                        }
                        videoPublishedDate.setOnClickListener {
                            VideoDescriptionSheet(video).show(childFragmentManager, "videoDescription")
                        }
                        videoViews.setOnClickListener {
                            VideoDescriptionSheet(video).show(childFragmentManager, "videoDescription")
                        }
                        goToComments.setOnClickListener {
                            val action = VideoDataFragmentDirections.actionVideoDataFragmentToCommentsFragment()
                            findNavController().navigate(action)
                        }
                    }
                }
                is Resource.Error -> {
                    Log.e("videos", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("videos", "Loading")
                }
            }
        })

        viewModel.relatedVideosList.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    hideProgressBar(progressBar)
                    resource.data?.let {
                        totalRelatedVideos = it.pageInfo.totalResults
                        if (viewModel.nextPageToken != it.nextPageToken) {
                            viewModel.nextPageToken = it.nextPageToken
                            viewModel.relatedVideos.addAll(it.items)
                            viewModel.relatedVideos.forEach { video ->
                                viewModel.getVideoDetails(video.id.videoId)
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar(progressBar)
                    Log.e("relatedSearchVideos", resource.message.toString())
                }
                is Resource.Loading -> {
                    showProgressBar(progressBar)
                }
            }
        })

        viewModel.singleVideo.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    hideProgressBar(progressBar)
                    resource.data?.let { videoResponse ->
                        if (!viewModel.relatedVideoIds.contains(videoResponse.items[0].id)){
                            viewModel.relatedVideoIds.add(videoResponse.items[0].id)
                            viewModel.relatedVideoDetails.add(videoResponse.items[0])
                        }
                        relatedVideosAdapter.differ.submitList(viewModel.relatedVideoDetails)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar(progressBar)
                    Log.e("relatedVideos", resource.message.toString())
                }
                is Resource.Loading -> {
                    showProgressBar(progressBar)
                }
            }
        })

        viewModel.channelResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let { channelResponse ->
                        Glide.with(this).load(channelResponse.items[0].snippet.thumbnails.medium?.url).into(channelLogo)
                        val subs = channelResponse.items[0].statistics.subscriberCount
                        val hideSubs = channelResponse.items[0].statistics.hiddenSubscriberCount
                        if (!hideSubs) {
                            channelTitle.text = channelResponse.items[0].snippet.title
                            channelSubs.text = resources.getString(
                                R.string.subscribers,
                                VideoViewsFormatter.viewsFormatter((subs).toString())
                            )
                        }
                        else {
                            channelTitleWithHiddenSubs.text = channelResponse.items[0].snippet.title
                        }
                    }
                }
                is Resource.Error -> {
                    Log.e("videoScreen", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("videoScreen", "Loading")
                }
            }
        })

        viewModel.relatedChannelResponse.observe(viewLifecycleOwner,
            androidx.lifecycle.Observer { resource ->
                when (resource) {
                    is Resource.Success -> {
                        resource.data?.let { channels ->
                            viewModel.relatedChannels.set(
                                channels.items[0].id,
                                channels.items[0]
                            )
                            Log.d("searched", viewModel.relatedChannelResponse.toString())
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

        viewModel.commentResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let { commentResponse ->
                        commentsCount.text = commentResponse.pageInfo.totalResults.toString()
                        viewModel.commentsForVideo.addAll(commentResponse.items)
                        Log.d("commentedDate", commentResponse.items.map { it.snippet.topLevelComment.snippet.updatedAt }.toString())
                        Log.d("comments", commentResponse.pageInfo.totalResults.toString())
                    }
                }
                is Resource.Error -> {
                    Log.e("comments", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("comments", "Loading")
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
            val totalItems = manager.itemCount
            val scrolledItems = manager.findFirstVisibleItemPosition()
            if (isScrolling && totalItems == currentItems + scrolledItems && !isLoading && viewModel.relatedVideos.size <= totalRelatedVideos) {
                Log.d("relatedVideos", "scrolled out completely")
                viewModel.getVideosRelatedToCurrentVideo(video.id, viewModel.nextPageToken)
                isScrolling = false
            } else {
                relatedVideosAdapter.differ.submitList(viewModel.relatedVideoDetails.toList())
                recyclerView.setPadding(0, 0, 0, 0)
            }
        }
    }
}