package com.example.mytube.adapters

import android.content.Intent
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
import com.example.mytube.UI.ChannelDetailsActivity
import com.example.mytube.models.ChannelFullDetails
import com.example.mytube.models.ItemXX
import com.example.mytube.util.VideoViewsFormatter

class RelatedChannelsAdapter: RecyclerView.Adapter<RelatedChannelsAdapter.RelatedChannelViewHolder>() {

    inner class RelatedChannelViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val channelLogo = itemView.findViewById<ImageView>(R.id.channel_image)
        private val channelTitle = itemView.findViewById<TextView>(R.id.channel_title)
        private val channelSubscribers = itemView.findViewById<TextView>(R.id.channel_subscribers)
        private val channelVideoCount = itemView.findViewById<TextView>(R.id.channel_video_count)
        private val channelBox = itemView.findViewById<ConstraintLayout>(R.id.channel_brief_box)
        fun bind(channel: ItemXX) {
            Glide.with(itemView)
                .load(channel.snippet.thumbnails.medium.url)
                .into(channelLogo)
            channelTitle.text = channel.snippet.title
            if (!channel.statistics.hiddenSubscriberCount) {
                channelSubscribers.text = itemView.context.getString(R.string.subscribers, VideoViewsFormatter.viewsFormatter(channel.statistics.subscriberCount))
            }
            else {
                channelBox.removeView(channelSubscribers)
            }
            channelVideoCount.text = if (channel.statistics.videoCount.isNotEmpty()) {
                itemView.context.getString(R.string.channel_video_count, VideoViewsFormatter.viewsFormatter(channel.statistics.videoCount))
            }
            else {
                "0 videos"
            }
            channelBox.setOnClickListener {
                val intent = Intent(itemView.context, ChannelDetailsActivity::class.java)
                intent.putExtra("channelId", channel.id)
                intent.putExtra("channelTitle", channel.snippet.title)
                itemView.context.startActivity(intent)
            }
        }
    }

    val differCallback = object: DiffUtil.ItemCallback<ItemXX>() {
        override fun areItemsTheSame(oldItem: ItemXX, newItem: ItemXX): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ItemXX, newItem: ItemXX): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RelatedChannelViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.playlist_channel_item, parent, false)
        return RelatedChannelViewHolder(layout)
    }

    override fun onBindViewHolder(holder: RelatedChannelViewHolder, position: Int) {
        val channel = differ.currentList[position]
        holder.bind(channel)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}