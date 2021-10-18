package com.example.mytube.UI

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.adapters.VideosAdapter
import com.example.mytube.db.SearchDatabase
import com.example.mytube.models.AboutVideo
import com.example.mytube.repository.VideosRepository
import com.example.mytube.util.Resource
import com.example.mytube.util.VideoViewsFormatter
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.menu.MenuItem
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBar

class VideoActivity : AppCompatActivity() {

    lateinit var viewModel: VideosViewModel
    lateinit var videosAdapter: VideosAdapter

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        var startTime = 0f

        val video: AboutVideo = intent.getBundleExtra("video")?.getSerializable("video") as AboutVideo
        Log.d("videoPlayer", video.toString())

        val repository: VideosRepository = VideosRepository(SearchDatabase.getSearchDatabase(this))
        val viewModelFactory = VideosViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(VideosViewModel::class.java)
        videosAdapter = VideosAdapter(viewModel)

        viewModel.getChannel(video.snippet.channelId)

        val videoViews = findViewById<TextView>(R.id.video_views)
        val videoPublishedDate = findViewById<TextView>(R.id.video_published_date)
        val videoLikes = findViewById<TextView>(R.id.like_text)
        val videoDislikes = findViewById<TextView>(R.id.dislike_text)
        val videoTitle = findViewById<TextView>(R.id.video_title_in_video_screen)

        videoTitle.text = video.snippet.title
        videoViews.text = "${VideoViewsFormatter.viewsFormatter((video.statistics.viewCount).toString())} views . "
        val videoPublishedTime = viewModel.findMillisDifference(video.snippet.publishedAt)
        VideoViewsFormatter.timeFormatter(videoPublishedTime, videoPublishedDate, this).toString()
        videoLikes.text = VideoViewsFormatter.viewsFormatter((video.statistics.likeCount).toString())
        videoDislikes.text = VideoViewsFormatter.viewsFormatter((video.statistics.dislikeCount).toString())

        // The youtube player initialization and customization
        val youtubePlayerView: YouTubePlayerView = findViewById(R.id.youtube_player_view)
        lifecycle.addObserver(youtubePlayerView)

        youtubePlayerView.addYouTubePlayerListener(AbstractPlayerListener(video.id, startTime, this))
        val playerUIController = youtubePlayerView.getPlayerUiController()
        playerUIController.showMenuButton(true)
        val menu = playerUIController.getMenu()
        menu?.addItem(MenuItem("Save to PlayList", onClickListener = View.OnClickListener {
            TODO()
        }))
        menu?.addItem(MenuItem("Save to Watch Later", onClickListener = View.OnClickListener {
            TODO()
        }))
        menu?.addItem(MenuItem("Download Video", onClickListener = View.OnClickListener {
            TODO()
        }))
        menu?.addItem(MenuItem("Share", onClickListener = View.OnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=${video.id}")
            try {
//                 If sharing apps like whatsapp and gmail are available
                startActivity(intent)
            }
            catch (e: ActivityNotFoundException) {
                // else go to google playstore for downloading whatsapp in this case
                val intent2 = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"))
                startActivity(intent2)
            }
        }))
    }

    override fun onResume() {
        super.onResume()

        val channelLogo = findViewById<ImageView>(R.id.channel_logo)
        val channelTitle = findViewById<TextView>(R.id.channel_name)
        val channelSubs = findViewById<TextView>(R.id.channel_subscribers)

        viewModel.channelResponse.observe(this, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let { channelResponse ->
                        Glide.with(this).load(channelResponse.items[0].snippet.thumbnails.medium?.url).into(channelLogo)
                        channelTitle.text = channelResponse.items[0].snippet.title
                        val subs = channelResponse.items[0].statistics.subscriberCount
                        if (subs > 0) {
                            channelSubs.text = resources.getString(
                                R.string.subscribers,
                                VideoViewsFormatter.viewsFormatter((subs).toString())
                            )
                        }
                        else {
                            channelSubs.text = ""
                        }
                    }
                }
                is Resource.Error -> {
                    Log.e("videoScreen", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("videoScreen", "Loading")
                }
            }
        })
    }
}


class AbstractPlayerListener(videoId: String, startTime: Float, val context: Context): AbstractYouTubePlayerListener() {
    val id = videoId
    val time = startTime
    override fun onReady(youTubePlayer: YouTubePlayer) {
        youTubePlayer.loadVideo(id, time)
    }
}
