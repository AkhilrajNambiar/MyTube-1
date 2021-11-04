package com.example.mytube.adapters

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.ChannelViewModel
import com.example.mytube.UI.VideoActivity
import com.example.mytube.models.Equatable
import com.example.mytube.models.SinglePlaylist
import com.example.mytube.models.VideoInPlaylistDetail

class PlaylistDetailScreenAdapter(val viewModel: ChannelViewModel): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    class PlaylistDetailsOnTopViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        private val playlistTitle = itemView.findViewById<TextView>(R.id.playlist_title_in_detail_screen)
        private val playlistChannelName = itemView.findViewById<TextView>(R.id.playlist_channel_name_in_detail_screen)
        private val playlistVideoCount = itemView.findViewById<TextView>(R.id.playlist_video_count_in_detail_screen)
        private val shuffleAndStart = itemView.findViewById<ImageView>(R.id.shuffle_and_start)
        private val addToYourPlaylist = itemView.findViewById<ImageView>(R.id.add_to_your_playlists)
        private val downloadPlaylist = itemView.findViewById<ImageView>(R.id.download_entire_playlist)
        private val sharePlaylist = itemView.findViewById<ImageView>(R.id.share_playlist)
        fun bind(playlist: SinglePlaylist, viewModel: ChannelViewModel) {
            playlistTitle.text = playlist.snippet.title
            playlistChannelName.text = playlist.snippet.channelTitle
            playlistVideoCount.text = itemView.context.resources.getString(R.string.playlist_video_count, playlist.contentDetails.itemCount)

            shuffleAndStart.setOnClickListener {
                // start a random video from the playlist
                val videoIds =  viewModel.playlistItems.map {
                    it.contentDetails.videoId
                }
                val randomVideoId = videoIds.random()
                val intent = Intent(itemView.context, VideoActivity::class.java)
                intent.putExtra("videoId", randomVideoId)
                itemView.context.startActivity(intent)
            }

            sharePlaylist.setOnClickListener {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/playlist?list=${playlist.id}")
                try {
                    itemView.context.startActivity(intent)
                }
                catch (e: ActivityNotFoundException) {
                    val intent2 = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"))
                    itemView.context.startActivity(intent2)
                }
            }

            addToYourPlaylist.setOnClickListener {
                TODO("Implement after creating playlists database")
            }

            downloadPlaylist.setOnClickListener {
                TODO("Implement after creating the downloaded videos section")
            }
        }
    }

    class PlaylistVideosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val videoThumbnail = itemView.findViewById<ImageView>(R.id.video_thumbnail_playlist)
        private val videoTitle = itemView.findViewById<TextView>(R.id.video_title_playlist)
        private val videoChannelName = itemView.findViewById<TextView>(R.id.video_subtitle_playlist)
        private val videoCard = itemView.findViewById<ConstraintLayout>(R.id.playlist_video_card)
        fun bind(video: VideoInPlaylistDetail) {
            Glide.with(itemView)
                .load(video.videoThumbnailUrl)
                .into(videoThumbnail)
            videoTitle.text = video.videoTitle
            videoChannelName.text = video.channelTitle
            videoCard.setOnClickListener {
                val intent = Intent(itemView.context, VideoActivity::class.java)
                intent.putExtra("videoId", video.videoId)
                itemView.context.startActivity(intent)
            }
        }
    }

    val differCallback = object: DiffUtil.ItemCallback<Equatable>() {
        override fun areItemsTheSame(oldItem: Equatable, newItem: Equatable): Boolean {
            // Since both our classes are serializable we can do this
            // we return by checking if the items are instance of either classes
            return when {
                oldItem is SinglePlaylist && newItem is SinglePlaylist -> {
                    oldItem.id == newItem.id
                }
                oldItem is VideoInPlaylistDetail && newItem is VideoInPlaylistDetail -> {
                    oldItem.videoId == newItem.videoId
                }
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: Equatable, newItem: Equatable): Boolean {
            return when {
                oldItem is SinglePlaylist && newItem is SinglePlaylist -> {
                    oldItem == newItem
                }
                oldItem is VideoInPlaylistDetail && newItem is VideoInPlaylistDetail -> {
                    oldItem == newItem
                }
                else -> false
            }
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val layout = LayoutInflater.from(parent.context).inflate(R.layout.playlist_detail_heading_item, parent, false)
            return PlaylistDetailsOnTopViewHolder(layout)
        }
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.playlist_video_item, parent, false)
        return PlaylistVideosViewHolder(layout)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = differ.currentList[position]
        if (position == 0) {
            val headingHolder = holder as PlaylistDetailsOnTopViewHolder
            headingHolder.bind(item as SinglePlaylist, viewModel)
        }
        else {
            val bodyHolder = holder as PlaylistVideosViewHolder
            bodyHolder.bind(item as VideoInPlaylistDetail)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            0
        } else {
            1
        }
    }
}