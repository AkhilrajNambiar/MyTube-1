package com.example.mytube.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.VideoActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.db.WatchLaterItem
import com.example.mytube.models.Equatable
import com.example.mytube.models.SinglePlaylist
import com.example.mytube.models.VideoInPlaylistDetail
import com.example.mytube.models.WatchLaterVideoCount

class WatchLaterAdapter(val viewModel: VideosViewModel, val lifecycleOwner: LifecycleOwner): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class HeadingViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val sorter = itemView.findViewById<ConstraintLayout>(R.id.sort_watch_later_videos)
        private val grayPart = itemView.findViewById<ConstraintLayout>(R.id.gray_part_watch_later)
        private val shuffleAndStart = itemView.findViewById<ImageView>(R.id.shuffle_and_start_watch_later)
        fun bind(head: WatchLaterVideoCount, viewModel: VideosViewModel, lifecycleOwner: LifecycleOwner) {
            val videoCount = itemView.findViewById<TextView>(R.id.watch_later_video_count)
            videoCount.text = itemView.context.resources.getString(R.string.playlist_video_count, head.videoCount)

            // shuffling and starting a video
            shuffleAndStart.setOnClickListener {
                viewModel.getRecentlyPublishedWatchLaterVideos().observe(lifecycleOwner, Observer {
                    val videoIds = it.map { item ->
                        item.videoId
                    }
                    val randomVideoId = videoIds.random()
                    val intent = Intent(itemView.context, VideoActivity::class.java)
                    intent.putExtra("videoId", randomVideoId)
                    itemView.context.startActivity(intent)
                })
            }

            // Sorting the watch later videos based on options selected
            sorter.setOnClickListener {
                val popup = PopupMenu(itemView.context, grayPart)
                popup.inflate(R.menu.watch_later_video_options)
                popup.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.most_popular_watch_later -> {
                            val equatableList: MutableList<Equatable> = mutableListOf()
                            viewModel.getMostPopularWatchLaterVideos().observe(lifecycleOwner, Observer { videoItems ->
                                equatableList.add(WatchLaterVideoCount(videoItems.size))
                                equatableList.addAll(videoItems)
                                differ.submitList(equatableList)
                            })
                            true
                        }
                        R.id.date_added_ascending -> {
                            val equatableList: MutableList<Equatable> = mutableListOf()
                            viewModel.getOldestAddedWatchLaterVideos().observe(lifecycleOwner, Observer { videoItems ->
                                equatableList.add(WatchLaterVideoCount(videoItems.size))
                                equatableList.addAll(videoItems)
                                differ.submitList(equatableList)
                            })
                            true
                        }
                        R.id.date_added_descending -> {
                            val equatableList: MutableList<Equatable> = mutableListOf()
                            viewModel.getRecentlyAddedWatchLaterVideos().observe(lifecycleOwner, Observer { videoItems ->
                                equatableList.add(WatchLaterVideoCount(videoItems.size))
                                equatableList.addAll(videoItems)
                                differ.submitList(equatableList)
                            })
                            true
                        }
                        R.id.date_published_descending -> {
                            val equatableList: MutableList<Equatable> = mutableListOf()
                            viewModel.getRecentlyPublishedWatchLaterVideos().observe(lifecycleOwner, Observer { videoItems ->
                                equatableList.add(WatchLaterVideoCount(videoItems.size))
                                equatableList.addAll(videoItems)
                                differ.submitList(equatableList)
                            })
                            true
                        }
                        R.id.date_published_ascending -> {
                            val equatableList: MutableList<Equatable> = mutableListOf()
                            viewModel.getOldestPublishedWatchLaterVideos().observe(lifecycleOwner, Observer { videoItems ->
                                equatableList.add(WatchLaterVideoCount(videoItems.size))
                                equatableList.addAll(videoItems)
                                differ.submitList(equatableList)
                            })
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
            }
        }
    }
    inner class BodyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val videoThumbnail = itemView.findViewById<ImageView>(R.id.video_thumbnail_watch_later)
        private val videoTitle = itemView.findViewById<TextView>(R.id.video_title_watch_later)
        private val videoChannelName = itemView.findViewById<TextView>(R.id.video_channel_watch_later)
        private val videoCard = itemView.findViewById<ConstraintLayout>(R.id.watch_later_video_card)
        private val options = itemView.findViewById<ImageView>(R.id.video_options_watch_later)
        fun bind(video: WatchLaterItem) {
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
        }
    }

    val differCallback = object: DiffUtil.ItemCallback<Equatable>() {
        override fun areItemsTheSame(oldItem: Equatable, newItem: Equatable): Boolean {
            // Since both our classes are equatable we can do this
            // we return by checking if the items are instance of either classes
            return when {
                oldItem is WatchLaterVideoCount && newItem is WatchLaterVideoCount -> {
                    oldItem.videoCount == newItem.videoCount
                }
                oldItem is WatchLaterItem && newItem is WatchLaterItem -> {
                    oldItem.videoId == newItem.videoId
                }
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: Equatable, newItem: Equatable): Boolean {
            return when {
                oldItem is WatchLaterVideoCount && newItem is WatchLaterVideoCount -> {
                    oldItem == newItem
                }
                oldItem is WatchLaterItem && newItem is WatchLaterItem -> {
                    oldItem == newItem
                }
                else -> false
            }
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val layout = LayoutInflater.from(parent.context).inflate(R.layout.watch_later_heading, parent, false)
            return HeadingViewHolder(layout)
        }
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.watch_later_video_item, parent, false)
        return BodyViewHolder(layout)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = differ.currentList[position]
        if (item is WatchLaterVideoCount) {
            val headHolder = holder as HeadingViewHolder
            headHolder.bind(item, viewModel = viewModel, lifecycleOwner = lifecycleOwner)
        }
        else {
            val bodyHolder = holder as BodyViewHolder
            bodyHolder.bind(item as WatchLaterItem)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (differ.currentList[position] is WatchLaterVideoCount) {
            0
        }
        else {
            1
        }
    }
}