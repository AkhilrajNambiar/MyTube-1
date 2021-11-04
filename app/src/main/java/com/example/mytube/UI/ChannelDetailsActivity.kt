package com.example.mytube.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.mytube.R
import com.example.mytube.adapters.FragmentTabsAdapter
import com.example.mytube.db.SearchDatabase
import com.example.mytube.models.ItemXX
import com.example.mytube.repository.VideosRepository
import com.example.mytube.util.Resource
import com.google.android.material.tabs.TabLayout

class ChannelDetailsActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var fragmentAdapter: FragmentTabsAdapter
    lateinit var viewModel: ChannelViewModel
    lateinit var channelId: String
    lateinit var channelTitle: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_channel_details)

        tabLayout = findViewById(R.id.channel_tabs)
        viewPager2 = findViewById(R.id.view_pager_2)

        // Initialising our adapter and setting it to the viewPager
        fragmentAdapter = FragmentTabsAdapter(supportFragmentManager, lifecycle)
        viewPager2.adapter = fragmentAdapter

        // Adding tabs
        tabLayout.addTab(tabLayout.newTab().setText("Home"))
        tabLayout.addTab(tabLayout.newTab().setText("Videos"))
        tabLayout.addTab(tabLayout.newTab().setText("Playlists"))
        tabLayout.addTab(tabLayout.newTab().setText("Channels"))
        tabLayout.addTab(tabLayout.newTab().setText("About"))

        // Adding a on Tab selected listener
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                // If tab is selected we also update the viewPager
                viewPager2.setCurrentItem(tab!!.position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                Log.d("tab", "${tab!!.text} is unselected")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Log.d("tab", "${tab!!.text} is reselected")
            }

        })

        // This is done so that we run a callback whenever there is a page change
        // Over here we want the underline on the tabs to change whenever there
        // is a change of tabs
        viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        val repository = VideosRepository(SearchDatabase.getSearchDatabase(applicationContext))
        val viewModelFactory = ChannelViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(ChannelViewModel::class.java)


        channelId = intent.getStringExtra("channelId")!!
        channelTitle = intent.getStringExtra("channelTitle")!!

        val channelName = findViewById<TextView>(R.id.channel_name)
        val goBack = findViewById<ImageView>(R.id.go_back)
        val search = findViewById<ImageView>(R.id.search)

        channelName.text = channelTitle

        goBack.setOnClickListener {
            finish()
        }

        search.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        viewModel.recyclerViewVideo.observe(this, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    resource.data?.let {
                        Log.d("recyclerViewVideo", it.toString())
//                        val intent = Intent(requireContext(), VideoActivity::class.java)
//                        val bundle = Bundle().apply {
//                            putSerializable("video", videoData)
//                        }
//                        intent.putExtra("video", bundle)
//                        startActivity(intent)
                    }
                }
                is Resource.Error -> {
                    Log.e("recyclerViewVideo", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("recyclerViewVideo", "Loading")
                }
            }
        })
    }
}