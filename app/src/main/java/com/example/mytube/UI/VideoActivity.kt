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
import android.widget.MediaController
import android.widget.SeekBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.Fragments.VideoDataFragment
import com.example.mytube.adapters.VideosAdapter
import com.example.mytube.db.SearchDatabase
import com.example.mytube.models.AboutVideo
import com.example.mytube.repository.VideosRepository
import com.example.mytube.util.Constants.Companion.API_KEY
import com.example.mytube.util.Resource
import com.example.mytube.util.VideoViewsFormatter
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayerFragment
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerCallback
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.menu.MenuItem
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBar
import java.lang.Exception

class VideoActivity : AppCompatActivity(), com.google.android.youtube.player.YouTubePlayer.OnInitializedListener{

    lateinit var viewModel: VideosViewModel
    lateinit var videosAdapter: VideosAdapter
    lateinit var video: AboutVideo
    lateinit var navController: NavController
    lateinit var videoId: String
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)

//        video= intent.getBundleExtra("video")?.getSerializable("video") as AboutVideo
//        Log.d("videoPlayer", video.toString())

        videoId = intent.getStringExtra("videoId")!!
        Log.d("videoPlayer", videoId)


        val repository: VideosRepository = VideosRepository(SearchDatabase.getSearchDatabase(this))
        val viewModelFactory = VideosViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(VideosViewModel::class.java)

        videosAdapter = VideosAdapter(viewModel)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.video_data_nav_host) as NavHostFragment
        navController = navHostFragment.navController

        // The youtube player initialization and customization
        val youtubePlayerFragment: YouTubePlayerFragment = fragmentManager.findFragmentById(R.id.youtube_player_view) as YouTubePlayerFragment
//        lifecycle.addObserver(youtubePlayerView)
        youtubePlayerFragment.initialize(API_KEY, this)




//        youtubePlayerView.addYouTubePlayerListener(AbstractPlayerListener(video.id, startTime, this))
//        val playerUIController = youtubePlayerView.getPlayerUiController()
//        playerUIController.showMenuButton(true)
//        val menu = playerUIController.getMenu()
//        menu?.addItem(MenuItem("Save to PlayList", onClickListener = View.OnClickListener {
//            TODO()
//        }))
//        menu?.addItem(MenuItem("Save to Watch Later", onClickListener = View.OnClickListener {
//            TODO()
//        }))
//        menu?.addItem(MenuItem("Download Video", onClickListener = View.OnClickListener {
//            TODO()
//        }))
//        menu?.addItem(MenuItem("Share", onClickListener = View.OnClickListener {
//            val intent = Intent(Intent.ACTION_SEND)
//            intent.type = "text/plain"
//            intent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=${video.id}")
//            try {
////                 If sharing apps like whatsapp and gmail are available
//                startActivity(intent)
//            }
//            catch (e: ActivityNotFoundException) {
//                // else go to google playstore for downloading whatsapp in this case
//                val intent2 = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp"))
//                startActivity(intent2)
//            }
//        }))
    }

    override fun onInitializationSuccess(
        provider: com.google.android.youtube.player.YouTubePlayer.Provider?,
        player: com.google.android.youtube.player.YouTubePlayer?,
        wasRestored: Boolean
    ) {
        player?.loadVideo(videoId)
    }

    override fun onInitializationFailure(
        p0: com.google.android.youtube.player.YouTubePlayer.Provider?,
        p1: YouTubeInitializationResult?
    ) {
        TODO("Not yet implemented")
    }
}


class AbstractPlayerListener(videoId: String, startTime: Float, val context: Context): AbstractYouTubePlayerListener() {
    val id = videoId
    val time = startTime
    override fun onReady(youTubePlayer: YouTubePlayer) {
        youTubePlayer.loadVideo(id, time)
    }
}
