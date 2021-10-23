package com.example.mytube.adapters

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.models.ItemX
import com.example.mytube.util.VideoViewsFormatter

class CommentRepliesAdapter(val viewModel: VideosViewModel): RecyclerView.Adapter<CommentRepliesAdapter.CommentReplyViewHolder>() {

    inner class CommentReplyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val userPicture = itemView.findViewById<ImageView>(R.id.comment_reply_user_picture)
        private val replyPostedDateAndName = itemView.findViewById<TextView>(R.id.user_name_and_posted_date_reply)
        private val body = itemView.findViewById<TextView>(R.id.comment_reply_body)
        private val likeCount = itemView.findViewById<TextView>(R.id.reply_like_count)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(reply: ItemX) {
            Glide.with(itemView.context).load(reply.snippet.authorProfileImageUrl).into(userPicture)
            replyPostedDateAndName.text = itemView.context.resources.getString(R.string.commenting_username_and_posted_date, reply.snippet.authorDisplayName, VideoViewsFormatter.timeFormatter(viewModel.findMillisDifference(reply.snippet.updatedAt), context = itemView.context))
            body.text = reply.snippet.textOriginal
            likeCount.text = VideoViewsFormatter.viewsFormatter(reply.snippet.likeCount.toString())
        }
    }

    private val differCallback = object: DiffUtil.ItemCallback<ItemX>() {
        override fun areItemsTheSame(oldItem: ItemX, newItem: ItemX): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ItemX, newItem: ItemX): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentReplyViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.comment_reply_item, parent, false)
        return CommentReplyViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CommentReplyViewHolder, position: Int) {
        val reply = differ.currentList[position]
        holder.bind(reply)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}