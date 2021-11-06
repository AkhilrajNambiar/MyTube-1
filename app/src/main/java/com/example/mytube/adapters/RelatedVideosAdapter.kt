package com.example.mytube.adapters

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.SearchedVideosViewModel
import com.example.mytube.UI.VideoActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.models.AboutVideo
import com.example.mytube.util.VideoViewsFormatter.Companion.viewsFormatter

class RelatedVideosAdapter(val viewModel: VideosViewModel): RecyclerView.Adapter<RelatedVideosAdapter.VideosViewHolder>() {

    inner class VideosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val image = itemView.findViewById<ImageView>(R.id.video_thumbnail)
        private val title = itemView.findViewById<TextView>(R.id.video_title)
        private val subtitle = itemView.findViewById<TextView>(R.id.video_subtitle)
        private val channelLogo = itemView.findViewById<ImageView>(R.id.channel_logo)
        private val videoCard = itemView.findViewById<LinearLayout>(R.id.video_card)
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(video: AboutVideo) {
            Glide.with(itemView).load(video.snippet.thumbnails.maxres?.url ?: video.snippet.thumbnails.standard?.url ?: video.snippet.thumbnails.high?.url ?: video.snippet.thumbnails.medium?.url ?: video.snippet.thumbnails.defaultThumb?.url).into(image)
            title.text = video.snippet.title
            val context = itemView.context
            val time = viewModel.findMillisDifference(video.snippet.publishedAt)
            val views = viewsFormatter(video.statistics.viewCount.toString())
            videoCard.setOnClickListener {
                val intent = Intent(itemView.context, VideoActivity::class.java)
                intent.putExtra("videoId", video.id)
                itemView.context.startActivity(intent)
            }
            if (time.containsKey("seconds")){
                if (time["seconds"] == 0) subtitle.text = context.getString(R.string.recent)
                else {
                    subtitle.text = context.getString(R.string.video_subtitle, video.snippet.channelTitle, views, time.get("seconds"), context.resources.getQuantityString(R.plurals.secondCount, time.get("seconds")!!.toInt()))
                }
            }
            else if (time.containsKey("minutes")) {
                subtitle.text = context.getString(R.string.video_subtitle, video.snippet.channelTitle, views, time.get("minutes"), context.resources.getQuantityString(R.plurals.minCount, time.get("minutes")!!.toInt()))
            }
            else if (time.containsKey("hours")) {
                subtitle.text = context.getString(R.string.video_subtitle, video.snippet.channelTitle, views, time.get("hours"), context.resources.getQuantityString(R.plurals.hourCount, time.get("hours")!!.toInt()))
            }
            else if (time.containsKey("days")) {
                subtitle.text = context.getString(R.string.video_subtitle, video.snippet.channelTitle, views, time.get("days"), context.resources.getQuantityString(R.plurals.dayCount, time.get("days")!!.toInt()))
            }
            else if (time.containsKey("weeks")) {
                subtitle.text = context.getString(R.string.video_subtitle, video.snippet.channelTitle, views, time.get("weeks"), context.resources.getQuantityString(R.plurals.weekCount, time.get("weeks")!!.toInt()))
            }
            else if (time.containsKey("months")) {
                subtitle.text = context.getString(R.string.video_subtitle, video.snippet.channelTitle, views, time.get("months"), context.resources.getQuantityString(R.plurals.monthCount, time.get("months")!!.toInt()))
            }
            else if (time.containsKey("years")) {
                subtitle.text = context.getString(R.string.video_subtitle, video.snippet.channelTitle, views, time.get("years"), context.resources.getQuantityString(R.plurals.yearCount, time.get("years")!!.toInt()))
            }
            Log.d("Channels", viewModel.relatedChannels[video.snippet.channelId]?.snippet?.thumbnails?.medium?.url.toString())
            if (viewModel.relatedChannels.containsKey(video.snippet.channelId)) {
                Glide.with(itemView)
                    .load(viewModel.relatedChannels[video.snippet.channelId]?.snippet?.thumbnails?.medium?.url)
                    .into(channelLogo)
            }
            else {
                viewModel.getRelatedChannels(video.snippet.channelId)
                Glide.with(itemView)
                    .load(viewModel.relatedChannels[video.snippet.channelId]?.snippet?.thumbnails?.medium?.url)
                    .placeholder(R.drawable.loading)
                    .into(channelLogo)
            }
        }
    }

    val differCallback = object: DiffUtil.ItemCallback<AboutVideo>() {
        override fun areItemsTheSame(oldItem: AboutVideo, newItem: AboutVideo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AboutVideo, newItem: AboutVideo): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideosViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.video_item, parent, false)
        return VideosViewHolder(layout)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: VideosViewHolder, position: Int) {
        val video = differ.currentList.get(position)
        holder.bind(video)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}