package com.example.mytube.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.VideoActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.db.WatchHistoryItem

class RecentVideosAdapter(val viewModel:VideosViewModel): RecyclerView.Adapter<RecentVideosAdapter.RecentVideoViewHolder>(){
    inner class RecentVideoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val videoThumbnail = itemView.findViewById<ImageView>(R.id.recent_video_thumbnail)
        private val videoTitle = itemView.findViewById<TextView>(R.id.recent_video_title)
        private val videoChannelName = itemView.findViewById<TextView>(R.id.recent_video_channel)
        private val videoCard = itemView.findViewById<ConstraintLayout>(R.id.recent_video_card)
        private val videoOptions = itemView.findViewById<ImageView>(R.id.more_video_options)
        fun bind(video: WatchHistoryItem, viewModel: VideosViewModel){
            Glide.with(itemView)
                .load(video.videoThumbnailUrl)
                .into(videoThumbnail)
            videoTitle.text = video.videoTitle
            videoChannelName.text = video.videoChannelName
            videoCard.setOnClickListener {
                val intent = Intent(itemView.context, VideoActivity::class.java)
                intent.putExtra("videoId", video.videoId)
                itemView.context.startActivity(intent)
            }
            videoOptions.setOnClickListener {
                val popup = PopupMenu(itemView.context, videoThumbnail)
                popup.inflate(R.menu.video_history_options)
                popup.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.remove_from_watch_history -> {
                            viewModel.deleteVideoFromWatchHistory(video)
                            true
                        }
                        R.id.share_video_option -> {
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "text/plain"
                            intent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=${video.videoId}")
                            itemView.context.startActivity(intent)
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }

    private val differCallback = object: DiffUtil.ItemCallback<WatchHistoryItem>() {
        override fun areItemsTheSame(
            oldItem: WatchHistoryItem,
            newItem: WatchHistoryItem
        ): Boolean {
            return oldItem.videoId == newItem.videoId
        }

        override fun areContentsTheSame(
            oldItem: WatchHistoryItem,
            newItem: WatchHistoryItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentVideoViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.recent_video_item, parent, false)
        return RecentVideoViewHolder(layout)
    }

    override fun onBindViewHolder(holder: RecentVideoViewHolder, position: Int) {
        val video = differ.currentList[position]
        holder.bind(video, viewModel)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}