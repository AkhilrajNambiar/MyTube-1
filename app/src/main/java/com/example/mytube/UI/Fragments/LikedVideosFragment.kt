package com.example.mytube.UI.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytube.R
import com.example.mytube.UI.MainActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.adapters.LikedVideosAdapter
import com.example.mytube.models.Equatable
import com.example.mytube.models.LikedVideosCount


class LikedVideosFragment : Fragment(R.layout.fragment_liked_videos) {
    lateinit var viewModel: VideosViewModel
    lateinit var recyclerView: RecyclerView
    lateinit var likedVideosAdapter: LikedVideosAdapter
    val equatableList: MutableSet<Equatable> = mutableSetOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = (activity as MainActivity).viewModel
        return inflater.inflate(R.layout.fragment_liked_videos, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        likedVideosAdapter = LikedVideosAdapter(viewModel)
        recyclerView = view.findViewById(R.id.liked_videos_recycler_view)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = likedVideosAdapter
        }
        viewModel.getLikedVideos().observe(viewLifecycleOwner, androidx.lifecycle.Observer { videosList ->
            viewModel.equatableLikedVideos.clear()
            viewModel.equatableLikedVideos.add(LikedVideosCount(videosList.size))
            viewModel.equatableLikedVideos.addAll(videosList)
            likedVideosAdapter.differ.submitList(viewModel.equatableLikedVideos.toList())
        })
    }
}