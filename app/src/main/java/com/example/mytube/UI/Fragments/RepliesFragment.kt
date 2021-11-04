package com.example.mytube.UI.Fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.VideoActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.adapters.CommentRepliesAdapter
import com.example.mytube.adapters.CommentsAdapter
import com.example.mytube.models.Item
import com.example.mytube.models.ItemX
import com.example.mytube.models.TopLevelComment
import com.example.mytube.util.Resource
import com.example.mytube.util.VideoViewsFormatter


class RepliesFragment : Fragment(R.layout.fragment_replies) {
    private lateinit var commentsAdapter: CommentRepliesAdapter
    lateinit var viewModel: VideosViewModel
    lateinit var comment: Item
    private lateinit var  onBackPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedCallback = object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_replies, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val recyclerView = view.findViewById<RecyclerView>(R.id.replies_list)

        arguments?.let {
            comment = it.getSerializable("comment") as Item
        }

        viewModel = (activity as VideoActivity).viewModel
        commentsAdapter = CommentRepliesAdapter(viewModel)
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentsAdapter
        }

        val closeComments = view.findViewById<ImageView>(R.id.close_comment_replies)
        val goBackToTopLevelComment = view.findViewById<ImageView>(R.id.back_to_comments)
        val mainCommentUserImage = view.findViewById<ImageView>(R.id.comment_user_picture)
        val userNameAndPostedDate = view.findViewById<TextView>(R.id.user_name_and_posted_date)
        val mainCommentBody = view.findViewById<TextView>(R.id.comment_body)
        val likesCount = view.findViewById<TextView>(R.id.like_count)
        val repliesCount1 = view.findViewById<TextView>(R.id.replies_count)
        val repliesCount2 = view.findViewById<TextView>(R.id.move_to_replies)
        val progressBar = view.findViewById<ProgressBar>(R.id.replies_loading)

        Glide.with(this).load(comment.snippet.topLevelComment.snippet.authorProfileImageUrl).into(mainCommentUserImage)
        userNameAndPostedDate.text = resources.getString(R.string.commenting_username_and_posted_date, comment.snippet.topLevelComment.snippet.authorDisplayName, VideoViewsFormatter.timeFormatter(viewModel.findMillisDifference(comment.snippet.topLevelComment.snippet.updatedAt), context = requireContext()))
        mainCommentBody.text = comment.snippet.topLevelComment.snippet.textOriginal
        likesCount.text = VideoViewsFormatter.viewsFormatter(comment.snippet.topLevelComment.snippet.likeCount.toString())
        repliesCount1.text = VideoViewsFormatter.viewsFormatter(comment.snippet.totalReplyCount.toString())
        repliesCount2.text = resources.getString(R.string.replies, VideoViewsFormatter.viewsFormatter(comment.snippet.totalReplyCount.toString()))

        goBackToTopLevelComment.setOnClickListener {
            findNavController().navigateUp()
        }

        closeComments.setOnClickListener {
            findNavController().popBackStack(R.id.videoDataFragment, false)
        }

        viewModel.commentRepliesResponse.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    hideProgressBar(progressBar)
                    resource.data?.let { replies ->
                        viewModel.commentReplies.value = replies.items.toMutableList()
                        Log.d("commentReplies", replies.items.toString())
                    }
                }
                is Resource.Error -> {
                    hideProgressBar(progressBar)
                    Log.e("commentReplies", resource.message.toString())
                }
                is Resource.Loading -> {
                    showProgressBar(progressBar)
                    Log.d("commentReplies", "Loading")
                }
            }
        })
        viewModel.commentReplies.observe(viewLifecycleOwner, Observer {
            Log.d("commentReplies", "THe replies list changed")
            commentsAdapter.differ.submitList(it)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onBackPressedCallback.isEnabled = false
        onBackPressedCallback.remove()
    }

    private fun showProgressBar(bar: ProgressBar) {
        bar.visibility = View.VISIBLE
    }

    private fun hideProgressBar(bar: ProgressBar) {
        bar.visibility = View.INVISIBLE
    }
}








