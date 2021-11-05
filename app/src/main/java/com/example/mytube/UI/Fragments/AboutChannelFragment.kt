package com.example.mytube.UI.Fragments

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import com.example.mytube.R
import com.example.mytube.UI.ChannelDetailsActivity
import com.example.mytube.UI.ChannelViewModel
import com.example.mytube.util.Resource
import com.example.mytube.util.VideoViewsFormatter
import org.w3c.dom.Text
import java.nio.channels.Channel
import java.text.NumberFormat
import java.util.*

class AboutChannelFragment : Fragment(R.layout.fragment_about_channel) {
    lateinit var viewModel: ChannelViewModel
    lateinit var channelId: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as ChannelDetailsActivity).viewModel
        channelId = (activity as ChannelDetailsActivity).channelId
        viewModel.getCompleteChannelDetails(channelId)
        return inflater.inflate(R.layout.fragment_about_channel, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val description = view.findViewById<TextView>(R.id.channel_description_full)
        val descriptionLabel = view.findViewById<TextView>(R.id.description_label)
        val channelLink = view.findViewById<TextView>(R.id.channel_link)
        val channelLocation = view.findViewById<TextView>(R.id.channel_location)
        val channelLocationImage = view.findViewById<ImageView>(R.id.channel_location_image)
        val channelViews = view.findViewById<TextView>(R.id.channel_views)
        val mainView = view.findViewById<ConstraintLayout>(R.id.main_view)
        val joinDate = view.findViewById<TextView>(R.id.join_date)
        viewModel.channelResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        val channel = it.items[0]
                        if (channel.snippet.description.isNotEmpty()) {
                            description.text = channel.snippet.description
                        }
                        else {
                            mainView.removeView(descriptionLabel)
                            mainView.removeView(description)
                        }
                        if (channel.snippet.customUrl.isNotEmpty()) {
                            channelLink.text = resources.getString(R.string.custom_channel_link, channel.snippet.customUrl)
                        }
                        else {
                            channelLink.text = resources.getString(R.string.default_channel_link, channel.id)
                        }
                        if (channel.snippet.country.isNotEmpty()) {
                            channelLocation.text = Locale("",channel.snippet.country).displayCountry
                        }
                        else {
                            mainView.removeView(channelLocation)
                            mainView.removeView(channelLocationImage)
                        }
                        val formatter = NumberFormat.getNumberInstance()
                        val views = formatter.format(channel.statistics.viewCount.toLong())
                        channelViews.text = resources.getString(R.string.view_count, views)
                        val joiningDate = "${VideoViewsFormatter.returnDayFromDate(channel.snippet.publishedAt)}-${VideoViewsFormatter.returnMonthFromDate(channel.snippet.publishedAt)}-${VideoViewsFormatter.returnYearFromDate(channel.snippet.publishedAt)}"
                        joinDate.text = resources.getString(R.string.joined_on, joiningDate)
                    }
                }
            }
        })
    }
}