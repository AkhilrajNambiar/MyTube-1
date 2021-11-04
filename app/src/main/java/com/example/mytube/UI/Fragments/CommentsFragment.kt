package com.example.mytube.UI.Fragments

import android.media.Image
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytube.R
import com.example.mytube.UI.VideoActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.adapters.CommentsAdapter
import com.example.mytube.util.Resource


class CommentsFragment : Fragment(R.layout.fragment_comments) {

    lateinit var commentsAdapter: CommentsAdapter
    lateinit var viewModel: VideosViewModel
    lateinit var onBackPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // to close only the comments when we press back rather than going back to home screen
        // OnBackPressedCallback(enabled:Boolean) is not lifecycle aware
        // so we need to remove this callback in the onDestroyView
        // else it will be called when we press back some other time
        // and we crash as the associated fragmentManager has already been destroyed
        onBackPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val recyclerView = view.findViewById<RecyclerView>(R.id.comments_list)
        val closeComments = view.findViewById<ImageView>(R.id.close_comments)

        viewModel = (activity as VideoActivity).viewModel
        commentsAdapter = CommentsAdapter(viewModel)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentsAdapter
        }
        commentsAdapter.differ.submitList(viewModel.commentsForVideo)
        closeComments.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.isEnabled = false
        onBackPressedCallback.remove()
    }
}