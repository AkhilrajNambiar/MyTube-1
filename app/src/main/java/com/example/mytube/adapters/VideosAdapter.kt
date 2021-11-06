package com.example.mytube.adapters

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.customview.customView
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.SearchResultsActivity
import com.example.mytube.UI.VideoActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.db.WatchLaterItem
import com.example.mytube.models.AboutVideo
import com.example.mytube.util.VideoViewsFormatter
import com.example.mytube.util.VideoViewsFormatter.Companion.viewsFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.dialog.MaterialDialogs
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlin.math.absoluteValue

class VideosAdapter(val viewModel: VideosViewModel): RecyclerView.Adapter<VideosAdapter.VideosViewHolder>() {

    inner class VideosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val image = itemView.findViewById<ImageView>(R.id.video_thumbnail)
        private val title = itemView.findViewById<TextView>(R.id.video_title)
        private val subtitle = itemView.findViewById<TextView>(R.id.video_subtitle)
        private val channelLogo = itemView.findViewById<ImageView>(R.id.channel_logo)
        private val videoCard = itemView.findViewById<LinearLayout>(R.id.video_card)
        private val videoOptions = itemView.findViewById<ImageView>(R.id.video_options)

        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(video: AboutVideo) {
            Glide.with(itemView).load(video.snippet.thumbnails.maxres?.url ?: video.snippet.thumbnails.standard?.url ?: video.snippet.thumbnails.high?.url ?: video.snippet.thumbnails.medium?.url ?: video.snippet.thumbnails.defaultThumb?.url).into(image)
            title.text = video.snippet.title
            val context = itemView.context
            val time = viewModel.findMillisDifference(video.snippet.publishedAt)
            val views = viewsFormatter(video.statistics.viewCount.toString())

            videoCard.setOnClickListener {
                val intent = Intent(itemView.context, VideoActivity::class.java)
//                val bundle = Bundle().apply {
//                    putSerializable("video", video)
//                }
//                intent.putExtra("video", bundle)
//                itemView.context.startActivity(intent)
                intent.putExtra("videoId", video.id)
                itemView.context.startActivity(intent)
            }

            videoOptions.setOnClickListener {
                val popUpMenu = PopupMenu(itemView.context, image, Gravity.END)
                popUpMenu.inflate(R.menu.video_options_menu)
                Log.d("popup", "Gravity is ${popUpMenu.gravity}")
                popUpMenu.setOnMenuItemClickListener {
                    when(it.itemId) {
                        R.id.save_to_watch_later -> {
                            val watchLaterVideo = WatchLaterItem(
                                videoId = video.id,
                                videoTitle = video.snippet.title,
                                videoThumbnailUrl = video.snippet.thumbnails.maxres?.url ?: video.snippet.thumbnails.standard?.url ?: video.snippet.thumbnails.high?.url ?: video.snippet.thumbnails.medium?.url ?: video.snippet.thumbnails.defaultThumb!!.url,
                                videoChannelName = video.snippet.channelTitle,
                                videoViews = video.statistics.viewCount.toLong(),
                                videoAddedTime = System.currentTimeMillis(),
                                videoPublishedDate = VideoViewsFormatter.getMillisecondsFromLocalTime(video.snippet.publishedAt)
                            )
                            viewModel.insertVideoIntoWatchLater(watchLaterVideo)
                            Snackbar.make(itemView, "Video added to watch later!",
                                BaseTransientBottomBar.LENGTH_LONG
                            ).show()
                            true
                        }
                        R.id.save_to_playlist -> {
                            TODO("Yet to be implemented")
                        }
                        R.id.download_video -> {
                            TODO("Yet to be implemented")
                        }
                        R.id.share_video_option -> {
                            val intent = Intent(Intent.ACTION_SEND)
                            intent.type = "text/plain"
                            intent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=${video.id}")
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
                        else -> true
                    }
                }

                popUpMenu.show()
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
            Log.d("Channels", viewModel.channels.get(video.snippet.channelId)?.snippet?.thumbnails?.medium?.url.toString())
            if (viewModel.channels.containsKey(video.snippet.channelId)) {
                Glide.with(itemView)
                    .load(viewModel.channels.get(video.snippet.channelId)?.snippet?.thumbnails?.medium?.url)
                    .into(channelLogo)
            }
            else {
                try {
                    viewModel.getChannel(video.snippet.channelId)
                }
                catch (e:Exception) {
                    Log.e("MainVideoScreen", e.stackTraceToString())
                }
                Glide.with(itemView)
                    .load(viewModel.channels.get(video.snippet.channelId)?.snippet?.thumbnails?.medium?.url)
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