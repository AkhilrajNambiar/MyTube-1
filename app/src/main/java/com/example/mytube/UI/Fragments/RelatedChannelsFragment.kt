package com.example.mytube.UI.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytube.R
import com.example.mytube.UI.ChannelDetailsActivity
import com.example.mytube.UI.ChannelViewModel
import com.example.mytube.adapters.RelatedChannelsAdapter
import com.example.mytube.util.Resource

class RelatedChannelsFragment : Fragment(R.layout.fragment_related_channels) {
    lateinit var viewModel: ChannelViewModel
    lateinit var channelId: String
    lateinit var recyclerView: RecyclerView
    lateinit var relatedAdapter: RelatedChannelsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as ChannelDetailsActivity).viewModel
        channelId = (activity as ChannelDetailsActivity).channelId
        viewModel.getChannelSections(channelId)
        return inflater.inflate(R.layout.fragment_related_channels, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        relatedAdapter = RelatedChannelsAdapter()
        recyclerView = view.findViewById(R.id.related_channels_recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = relatedAdapter
        }
        val noRelatedChannelsBox = view.findViewById<ConstraintLayout>(R.id.no_related_channels_box)

        viewModel.channelSectionsResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let { channelSections ->
                        val items = channelSections.items
                        if (items.isEmpty()) {
                            noRelatedChannelsBox.visibility = View.VISIBLE
                        }
                        else {
                            noRelatedChannelsBox.visibility = View.INVISIBLE
                            for (item in items) {
                                if (item.snippet.type == "multiplechannels") {
                                    viewModel.hasRelatedChannels = true
                                    for (channel in item.contentDetails.channels) {
                                        viewModel.getRelatedChannelsDetails(channel)
                                    }
                                }
                            }
                            if (!viewModel.hasRelatedChannels){
                                noRelatedChannelsBox.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                is Resource.Error -> {
                    Log.e("relatedChannels", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("relatedChannels", "Loading")
                }
            }
        })

        viewModel.relatedChannelResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let { channelFullDetails ->
                        if (!viewModel.relatedChannelIds.contains(channelFullDetails.items[0].id)) {
                            viewModel.relatedChannels.add(channelFullDetails)
                            viewModel.relatedChannelIds.add(channelFullDetails.items[0].id)
                        }
                        val channelList = viewModel.relatedChannels.map {
                            it.items[0]
                        }
                        relatedAdapter.differ.submitList(channelList)
                    }
                }
                is Resource.Error -> {
                    Log.e("relatedChannels", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("relatedChannels", "Loading")
                }
            }
        })
    }
}