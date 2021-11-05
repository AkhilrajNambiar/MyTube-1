package com.example.mytube.adapters

import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.example.mytube.util.VideoViewsFormatter

class WatchHistoryAdapter(val viewModel: VideosViewModel): RecyclerView.Adapter<WatchHistoryAdapter.VideoViewHolder>() {
    inner class VideoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val videoThumbnail = itemView.findViewById<ImageView>(R.id.video_thumbnail_watch_history)
        private val videoTitle = itemView.findViewById<TextView>(R.id.video_title_watch_history)
        private val videoChannel = itemView.findViewById<TextView>(R.id.video_channel_watch_history)
        private val videoViews = itemView.findViewById<TextView>(R.id.video_views_watch_history)
        private val videoCard = itemView.findViewById<LinearLayout>(R.id.video_card_watch_history)
        private val videoOptions = itemView.findViewById<ImageView>(R.id.video_options_watch_history)
        fun bind(video: WatchHistoryItem, viewModel: VideosViewModel) {
            Glide.with(itemView)
                .load(video.videoThumbnailUrl)
                .into(videoThumbnail)
            videoTitle.text = video.videoTitle
            videoChannel.text = video.videoChannelName
            videoViews.text = itemView.context.resources.getString(R.string.view_count, VideoViewsFormatter.viewsFormatter(video.videoViews))
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.watch_history_item, parent, false)
        return VideoViewHolder(layout)
    }

    override fun onBindViewHolder(holder: VideoViewHolder, position: Int) {
        val video = differ.currentList[position]
        holder.bind(video, viewModel)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}