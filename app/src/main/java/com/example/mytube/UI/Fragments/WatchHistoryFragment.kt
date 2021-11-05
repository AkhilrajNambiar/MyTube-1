package com.example.mytube.UI.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytube.R
import com.example.mytube.UI.MainActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.adapters.WatchHistoryAdapter


class WatchHistoryFragment : Fragment(R.layout.fragment_watch_history) {
    lateinit var viewModel: VideosViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var watchAdapter: WatchHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as MainActivity).viewModel
        return inflater.inflate(R.layout.fragment_watch_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        watchAdapter = WatchHistoryAdapter(viewModel)
        recyclerView = view.findViewById(R.id.watch_history_videos)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = watchAdapter
        }
        viewModel.getCompleteWatchHistory().observe(viewLifecycleOwner, Observer {
            watchAdapter.differ.submitList(it)
        })
    }
}