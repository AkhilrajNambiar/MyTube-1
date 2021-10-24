package com.example.mytube.UI.Fragments

import android.app.Dialog
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.VideoActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.models.AboutVideo
import com.example.mytube.util.Resource
import com.example.mytube.util.VideoViewsFormatter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.youtube.player.internal.v

class VideoDescriptionSheet(private val video: AboutVideo): BottomSheetDialogFragment() {

    lateinit var viewModel: VideosViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as VideoActivity).viewModel
        viewModel.getChannel(video.snippet.channelId)
        return inflater.inflate(R.layout.video_description_sheet, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val videoTitle = view.findViewById<TextView>(R.id.video_title_in_description_sheet)
        val likeCount = view.findViewById<TextView>(R.id.description_like_count)
        val viewCount = view.findViewById<TextView>(R.id.description_view_count)
        val videoAddedYear = view.findViewById<TextView>(R.id.added_year)
        val videoAddedDayAndMonth = view.findViewById<TextView>(R.id.added_date_and_month)
        val channelName = view.findViewById<TextView>(R.id.channel_name_in_description_box)
        val channelLogo = view.findViewById<ImageView>(R.id.channel_logo_in_description_box)
        val videoDescription = view.findViewById<TextView>(R.id.video_description)
        val closeDescription = view.findViewById<ImageView>(R.id.close_description)

        videoTitle.text = video.snippet.title
        likeCount.text = VideoViewsFormatter.viewsFormatter(video.statistics.likeCount.toString())
        viewCount.text = VideoViewsFormatter.viewsFormatter(video.statistics.viewCount.toString())
        videoAddedYear.text = VideoViewsFormatter.returnYearFromDate(video.snippet.publishedAt).toString()
        videoAddedDayAndMonth.text = "${VideoViewsFormatter.returnDayFromDate(video.snippet.publishedAt)} ${VideoViewsFormatter.returnMonthFromDate(video.snippet.publishedAt)}"
        channelName.text = video.snippet.channelTitle
        videoDescription.text = video.snippet.description

        closeDescription.setOnClickListener {
            // close the bottom sheet
            dismiss()
        }

        viewModel.channelResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        Glide.with(requireContext()).load(it.items[0].snippet.thumbnails.medium?.url).into(channelLogo)
                    }
                }
                is Resource.Error -> {
                    Log.e("description box", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("description box", "Loading")
                }
            }
        })
    }

}