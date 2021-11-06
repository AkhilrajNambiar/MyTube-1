package com.example.mytube.UI.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mytube.R
import com.example.mytube.UI.MainActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.adapters.WatchLaterAdapter
import com.example.mytube.models.Equatable
import com.example.mytube.models.WatchLaterVideoCount
import kotlinx.coroutines.delay

class WatchLaterFragment : Fragment(R.layout.fragment_watch_later) {
    lateinit var viewModel: VideosViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var watchLaterAdapter: WatchLaterAdapter
    lateinit var watchLaterVideoCount: WatchLaterVideoCount
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as MainActivity).viewModel
        return inflater.inflate(R.layout.fragment_watch_later, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        watchLaterAdapter = WatchLaterAdapter(viewModel, viewLifecycleOwner)
        recyclerView = view.findViewById(R.id.watch_later_recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = watchLaterAdapter
        }

        viewModel.getRecentlyAddedWatchLaterVideos().observe(viewLifecycleOwner, Observer {  videosList ->
            viewModel.equatableWatchLaterVideos.clear()
            viewModel.equatableWatchLaterVideos.add(WatchLaterVideoCount(videosList.size))
            viewModel.equatableWatchLaterVideos.addAll(videosList)
            watchLaterAdapter.differ.submitList(viewModel.equatableWatchLaterVideos.toList())
        })
    }
}