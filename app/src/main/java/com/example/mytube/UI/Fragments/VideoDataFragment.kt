package com.example.mytube.UI.Fragments

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.VideoActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.models.AboutVideo
import com.example.mytube.util.Resource
import com.example.mytube.util.VideoViewsFormatter
import com.google.android.youtube.player.internal.c

class VideoDataFragment : Fragment(R.layout.fragment_video_data) {

    lateinit var viewModel: VideosViewModel
    lateinit var video: AboutVideo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as VideoActivity).viewModel

        return inflater.inflate(R.layout.fragment_video_data, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        video = (activity as VideoActivity).video
        Log.d("videoData", video.toString())

        val videoViews = view.findViewById<TextView>(R.id.video_views)
        val videoPublishedDate = view.findViewById<TextView>(R.id.video_published_date)
        val videoLikes = view.findViewById<TextView>(R.id.like_text)
        val videoDislikes = view.findViewById<TextView>(R.id.dislike_text)
        val videoTitle = view.findViewById<TextView>(R.id.video_title_in_video_screen)
        val shareVideo = view.findViewById<LinearLayout>(R.id.share_video)

        videoTitle.text = video.snippet.title
        videoViews.text = "${VideoViewsFormatter.viewsFormatter((video.statistics.viewCount).toString())} views . "
        val videoPublishedTime = viewModel.findMillisDifference(video.snippet.publishedAt)
        videoPublishedDate.text = VideoViewsFormatter.timeFormatter(videoPublishedTime, view.context).toString()
        videoLikes.text = VideoViewsFormatter.viewsFormatter((video.statistics.likeCount).toString())
        videoDislikes.text = VideoViewsFormatter.viewsFormatter((video.statistics.dislikeCount).toString())
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

        val channelLogo = view.findViewById<ImageView>(R.id.channel_logo)
        val channelTitle = view.findViewById<TextView>(R.id.channel_name)
        val channelSubs = view.findViewById<TextView>(R.id.channel_subscribers)
        val channelTitleWithHiddenSubs = view.findViewById<TextView>(R.id.channel_name_if_subs_hidden)
        val commentsCount = view.findViewById<TextView>(R.id.comments_count)
        val goToComments = view.findViewById<ConstraintLayout>(R.id.go_to_comments)

        goToComments.setOnClickListener {
            val action = VideoDataFragmentDirections.actionVideoDataFragmentToCommentsFragment()
            findNavController().navigate(action)
        }

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
}