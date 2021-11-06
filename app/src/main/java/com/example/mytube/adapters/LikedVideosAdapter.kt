package com.example.mytube.adapters

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.Gravity
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
import com.example.mytube.db.LikedVideoItem
import com.example.mytube.models.Equatable
import com.example.mytube.models.LikedVideosCount

class LikedVideosAdapter(val viewModel: VideosViewModel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class HeadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val shuffleAndStart = itemView.findViewById<ImageView>(R.id.shuffle_and_start_liked_videos)
        fun bind(heading: LikedVideosCount) {
            val videoCount = itemView.findViewById<TextView>(R.id.liked_videos_video_count)
            videoCount.text = itemView.context.resources.getString(R.string.playlist_video_count, heading.videoCount)
            shuffleAndStart.setOnClickListener {
                val videoIds = differ.currentList.slice(1 until differ.currentList.size).map {
                    val video = it as LikedVideoItem
                    video.videoId
                }
                val randomVideoId = videoIds.random()
                val intent = Intent(itemView.context, VideoActivity::class.java)
                intent.putExtra("videoId", randomVideoId)
                itemView.context.startActivity(intent)
            }
        }
    }
    inner class BodyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val videoTitle = itemView.findViewById<TextView>(R.id.video_title_liked_videos)
        private val videoThumbnail = itemView.findViewById<ImageView>(R.id.video_thumbnail_liked_videos)
        private val videoChannel = itemView.findViewById<TextView>(R.id.video_channel_liked_videos)
        private val videoCard = itemView.findViewById<ConstraintLayout>(R.id.liked_videos_video_card)
        private val options = itemView.findViewById<ImageView>(R.id.video_options_liked_videos)
        fun bind(video: LikedVideoItem, viewModel: VideosViewModel) {
            Glide.with(itemView)
                .load(video.videoThumbnailUrl)
                .into(videoThumbnail)
            videoTitle.text = video.videoTitle
            videoChannel.text = video.videoChannelName
            videoCard.setOnClickListener {
                val intent = Intent(itemView.context, VideoActivity::class.java)
                intent.putExtra("videoId", video.videoId)
                itemView.context.startActivity(intent)
            }
            options.setOnClickListener {
                val popup = PopupMenu(itemView.context, options, Gravity.END)
                popup.inflate(R.menu.liked_videos_options)
                popup.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.remove_from_liked_videos -> {
                            viewModel.deleteVideoFromLikedVideos(video)
                            true
                        }
                        R.id.share_video_option -> {
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "text/plain"
                            intent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=${video.videoId}")
                            try {
                                //If sharing apps like whatsapp and gmail are available
                                itemView.context.startActivity(intent)
                            }
                            catch (e: ActivityNotFoundException) {
                                // else go to google playstore for downloading whatsapp in this case
                                val intent2 = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"))
                                itemView.context.startActivity(intent2)
                            }
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }
    private val differCallback = object: DiffUtil.ItemCallback<Equatable>() {
        override fun areItemsTheSame(oldItem: Equatable, newItem: Equatable): Boolean {
            return when{
                oldItem is LikedVideosCount &&  newItem is LikedVideosCount -> {
                    oldItem.videoCount == newItem.videoCount
                }
                oldItem is LikedVideoItem && newItem is LikedVideoItem -> {
                    oldItem.videoId == newItem.videoId
                }
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: Equatable, newItem: Equatable): Boolean {
            return when{
                oldItem is LikedVideosCount &&  newItem is LikedVideosCount -> {
                    oldItem == newItem
                }
                oldItem is LikedVideoItem && newItem is LikedVideoItem -> {
                    oldItem == newItem
                }
                else -> false
            }
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val layout = LayoutInflater.from(parent.context).inflate(R.layout.liked_videos_heading, parent, false)
            return HeadingViewHolder(layout)
        }
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.liked_videos_video_item, parent, false)
        return BodyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = differ.currentList[position]
        if (item is LikedVideosCount) {
            val headHolder = holder as HeadingViewHolder
            headHolder.bind(item)
        }
        else {
            val bodyHolder = holder as BodyViewHolder
            bodyHolder.bind(item as LikedVideoItem, viewModel)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (differ.currentList[position] is LikedVideosCount) {
            0
        }
        else {
            1
        }
    }
}

















