package com.example.mytube.UI.Fragments

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.mytube.R
import com.example.mytube.UI.MainActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.adapters.PlaylistVideosAdapter
import com.example.mytube.adapters.RecentVideosAdapter
import org.w3c.dom.Text

class LibraryFragment : Fragment(R.layout.fragment_library) {
    lateinit var viewModel: VideosViewModel
    lateinit var recentVideosAdapter: RecentVideosAdapter
    lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as MainActivity).viewModel
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val historyTab = view.findViewById<ConstraintLayout>(R.id.history_tab)
        val watchLaterTab = view.findViewById<ConstraintLayout>(R.id.watch_later_tab)
        val likedVideosTab = view.findViewById<ConstraintLayout>(R.id.liked_videos_tab)
        val unwatchedVideosCount = view.findViewById<TextView>(R.id.unwatched_videos_count)

        viewModel.getCountOfUnwatchedVideos().observe(viewLifecycleOwner, Observer {
            unwatchedVideosCount.text = resources.getString(R.string.unwatched_videos_count, it)
        })

        historyTab.setOnClickListener {
            val action = LibraryFragmentDirections.actionLibraryFragmentToWatchHistoryFragment()
            findNavController().navigate(action)
        }
        watchLaterTab.setOnClickListener {
            val action = LibraryFragmentDirections.actionLibraryFragmentToWatchLaterFragment()
            findNavController().navigate(action)
        }
        likedVideosTab.setOnClickListener {
            val action = LibraryFragmentDirections.actionLibraryFragmentToLikedVideosFragment()
            findNavController().navigate(action)
        }

        recentVideosAdapter = RecentVideosAdapter(viewModel)
        recyclerView = view.findViewById(R.id.most_recent_videos_recycler_view)
        recyclerView.apply {
            adapter = recentVideosAdapter
        }
        viewModel.getRecentlyWatchedVideos().observe(viewLifecycleOwner, Observer {
            recentVideosAdapter.differ.submitList(it)
        })

    }
}