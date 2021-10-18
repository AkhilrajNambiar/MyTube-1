package com.example.mytube.UI

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.SeekBar
import androidx.lifecycle.MutableLiveData
import com.example.mytube.R
import com.example.mytube.models.AboutVideo
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.menu.MenuItem
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBar

class VideoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        var startTime = 0f

        val video: AboutVideo = intent.getBundleExtra("video")?.getSerializable("video") as AboutVideo
        Log.d("videoPlayer", video.toString())



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
}


class AbstractPlayerListener(videoId: String, startTime: Float, val context: Context): AbstractYouTubePlayerListener() {
    val id = videoId
    val time = startTime
    override fun onReady(youTubePlayer: YouTubePlayer) {
        youTubePlayer.loadVideo(id, time)
    }
}