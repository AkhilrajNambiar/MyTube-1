package com.example.mytube.adapters

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.ChannelViewModel
import com.example.mytube.UI.VideoActivity
import com.example.mytube.models.AboutVideo
import com.example.mytube.models.ChannelHomePlaylistItem
import com.example.mytube.models.PlaylistItem
import com.example.mytube.util.Resource
import com.example.mytube.util.VideoViewsFormatter

class PlaylistVideosAdapter(val viewModel: ChannelViewModel): RecyclerView.Adapter<PlaylistVideosAdapter.PlaylistItemViewHolder>() {
    inner class PlaylistItemViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(video: ChannelHomePlaylistItem) {
            val videoThumbnail = itemView.findViewById<ImageView>(R.id.video_thumbnail_playlist)
            val videoTitle = itemView.findViewById<TextView>(R.id.video_title_playlist)
            val videoSubtitle = itemView.findViewById<TextView>(R.id.video_subtitle_playlist)
            val videoCard = itemView.findViewById<ConstraintLayout>(R.id.playlist_video_card)

            videoTitle.text = video.videoTitle.toString()
            videoSubtitle.text = VideoViewsFormatter.timeFormatter(viewModel.findMillisDifference(video.videoPostedTime),itemView.context)
            Glide.with(itemView).load(video.videoThumbnailUrl).into(videoThumbnail)
            videoCard.setOnClickListener {
                Log.e("videos", video.toString())
                val intent = Intent(itemView.context, VideoActivity::class.java)
                intent.putExtra("videoId", video.videoId)
                itemView.context.startActivity(intent)
            }
        }
    }

    val differCallback = object: DiffUtil.ItemCallback<ChannelHomePlaylistItem>(){
        override fun areItemsTheSame(oldItem: ChannelHomePlaylistItem, newItem: ChannelHomePlaylistItem): Boolean {
            return oldItem.videoId == newItem.videoId
        }

        override fun areContentsTheSame(oldItem: ChannelHomePlaylistItem, newItem: ChannelHomePlaylistItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistItemViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.playlist_video_item, parent, false)
        return PlaylistItemViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: PlaylistItemViewHolder, position: Int) {
        val video = differ.currentList[position]
        holder.bind(video)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}