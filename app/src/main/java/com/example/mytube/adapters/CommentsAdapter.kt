package com.example.mytube.adapters

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.Fragments.CommentsFragmentDirections
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.models.Item
import com.example.mytube.models.ItemX
import com.example.mytube.util.VideoViewsFormatter

class CommentsAdapter(private val viewModel: VideosViewModel): RecyclerView.Adapter<CommentsAdapter.CommentViewHolder>() {
    inner class CommentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val userImage = itemView.findViewById<ImageView>(R.id.comment_user_picture)
        private val userNameAndPostedDate = itemView.findViewById<TextView>(R.id.user_name_and_posted_date)
        private val commentBody = itemView.findViewById<TextView>(R.id.comment_body)
        private val commentLikes = itemView.findViewById<TextView>(R.id.like_count)
        private val commentReplies1 = itemView.findViewById<TextView>(R.id.replies_count)
        private val commentReplies2 = itemView.findViewById<TextView>(R.id.move_to_replies)
        private val commentCard = itemView.findViewById<ConstraintLayout>(R.id.comment_card)
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(comment: Item) {
            Glide.with(itemView).load(comment.snippet.topLevelComment.snippet.authorProfileImageUrl).into(userImage)
//            userNameAndPostedDate.text = "${comment.playlistItemSnippet.topLevelComment.playlistItemSnippet.authorDisplayName} . ${viewModel.findMillisDifference(comment.playlistItemSnippet.topLevelComment.updatedAt)}"
            userNameAndPostedDate.text = itemView.context.resources.getString(R.string.commenting_username_and_posted_date, comment.snippet.topLevelComment.snippet.authorDisplayName, VideoViewsFormatter.timeFormatter(viewModel.findMillisDifference(comment.snippet.topLevelComment.snippet.updatedAt), context = itemView.context))
            commentBody.text = comment.snippet.topLevelComment.snippet.textOriginal
            commentLikes.text = VideoViewsFormatter.viewsFormatter(comment.snippet.topLevelComment.snippet.likeCount.toString())
            commentReplies1.text = VideoViewsFormatter.viewsFormatter(comment.snippet.totalReplyCount.toString())
            commentReplies2.text = itemView.context.resources.getString(R.string.replies, VideoViewsFormatter.viewsFormatter(comment.snippet.totalReplyCount.toString()))
            commentCard.setOnClickListener {
                viewModel.getCommentsReplies(comment.snippet.topLevelComment.id)
                val action = CommentsFragmentDirections.actionCommentsFragmentToRepliesFragment(comment = comment)
                itemView.findNavController().navigate(action)
            }
            commentReplies2.setOnClickListener {
                viewModel.getCommentsReplies(comment.snippet.topLevelComment.id)
                val action = CommentsFragmentDirections.actionCommentsFragmentToRepliesFragment(comment = comment)
                itemView.findNavController().navigate(action)
            }
        }
    }

    private val differCallback = object: DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.comment_item, parent, false)
        return CommentViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = differ.currentList[position]
        holder.bind(comment)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}