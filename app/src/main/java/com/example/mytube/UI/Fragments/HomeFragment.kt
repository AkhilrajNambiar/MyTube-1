package com.example.mytube.UI.Fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.MainActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.adapters.VideosAdapter
import com.example.mytube.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class HomeFragment : Fragment(R.layout.fragment_home) {

    lateinit var viewModel: VideosViewModel
    lateinit var videosAdapter: VideosAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d("Videos", "onViewCreated called")
        viewModel = (activity as MainActivity).viewModel
        videosAdapter = VideosAdapter(viewModel)
        val recyclerView = view.findViewById<RecyclerView>(R.id.videos)
        val refresher = view.findViewById<SwipeRefreshLayout>(R.id.refreshLayout)

        refresher.setOnRefreshListener(object: SwipeRefreshLayout.OnRefreshListener{
            override fun onRefresh() {
                viewModel.videos.clear()
                viewModel.videoIds.clear()
                viewModel.getPopularVideos()
                refresher.isRefreshing = false
            }
        })

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = videosAdapter
            addOnScrollListener(this@HomeFragment.scrollListener)
        }
        val progressBar = view.findViewById<ProgressBar>(R.id.paginationProgressBar)
        val errorBox = view.findViewById<LinearLayout>(R.id.error_box)
        val errorText = view.findViewById<TextView>(R.id.home_page_error)
        val retryAfterObtainingInternet = view.findViewById<Button>(R.id.retry_homepage)
        val partialVideosLoadedNetworkError = view.findViewById<FrameLayout>(R.id.partially_loaded_network_error)

        retryAfterObtainingInternet.setOnClickListener {
            viewModel.getPopularVideos()
        }

        viewModel.videosList.observe(viewLifecycleOwner, Observer { resource ->
            when(resource) {
                is Resource.Success -> {
                    Log.d("searched", "Videos waala is running in HomeFrag")
                    hideProgressBar(progressBar)
                    hideErrorBox(errorBox)
                    hidePartialError(partialVideosLoadedNetworkError)
                    resource.data?.let { videoResponse ->
                        if (viewModel.nextPageId != videoResponse.nextPageToken) {
                            viewModel.nextPageId = videoResponse.nextPageToken
                            videoResponse.items.forEach {
                                if (!viewModel.videoIds.contains(it.id)){
                                    viewModel.videoIds.add(it.id)
                                    viewModel.videos.add(it)
                                }
                            }
                            Log.d("Channels", viewModel.channels.toString())
                            videosAdapter.differ.submitList(viewModel.videos.toList())
                            Log.d("Videos", viewModel.videos.toString())
                        }
                        else {
                            Log.d("Videos", "next token dekh ${viewModel.nextPageId}")
                            videosAdapter.differ.submitList(viewModel.videos.toList())
                        }
                    }
                }
                is Resource.Error -> {
                    Log.d("searched", "Videos waala is running in HomeFrag")
                    hideProgressBar(progressBar)
                    if (resource.message == "No Internet Connection for Next Videos!") {
                        showPartialError(partialVideosLoadedNetworkError)
                    }
                    else {
                        showErrorBox(errorBox)
                        errorText.text = resource.message
                    }
                    Log.e("Videos", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("searched", "Videos waala is running in HomeFrag")
                    showProgressBar(progressBar)
                    hideErrorBox(errorBox)
                    hidePartialError(partialVideosLoadedNetworkError)
                }
            }
        })

        viewModel.channelResponse.observe(viewLifecycleOwner, Observer { resource ->
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { channels ->
                        Log.d("searched", "Channel waala is running in HomeFrag")
                        viewModel.channels.set(channels.items[0].id, channels.items[0])
//                        videosAdapter.differ.submitList(viewModel.videos.toList())
                    }
                }
                is Resource.Error -> {
                    Log.d("searched", "Channel waala is running in HomeFrag")
                    Log.e("Channels", resource.message.toString())
                }
                is Resource.Loading -> {
                    Log.d("searched", "Channel waala is running in HomeFrag")
                    Log.d("Channels", "Waiting")
                }
            }
        })

    }


    private fun hideProgressBar(bar: ProgressBar) {
        bar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar(bar: ProgressBar){
        bar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun showErrorBox(errorBox: LinearLayout) {
        errorBox.visibility = View.VISIBLE
    }

    private fun hideErrorBox(errorBox: LinearLayout) {
        errorBox.visibility = View.INVISIBLE
    }
    private fun showPartialError(error: FrameLayout) {
        error.visibility = View.VISIBLE
    }
    private fun hidePartialError(error: FrameLayout) {
        error.visibility = View.INVISIBLE
    }

    var isScrolling: Boolean = false
    var isLoading = false
    var maxListSize = 200


    val scrollListener = object: RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val manager = recyclerView.layoutManager as LinearLayoutManager
            val currentItems = manager.childCount
            val totaltItems = manager.itemCount
            val scrolledItems = manager.findFirstVisibleItemPosition()
            if (isScrolling && totaltItems == currentItems + scrolledItems && !isLoading && viewModel.videos.size <= 200) {
                lifecycleScope.launch {
                    viewModel.getNextVideos()
                }
                isScrolling = false
            }
            else {
                videosAdapter.differ.submitList(viewModel.videos.toList())
                recyclerView.setPadding(0,0,0,0)
                Log.d("Videos", viewModel.videos.size.toString())
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("searched", "HomeFragment onResume called")
    }

    override fun onStart() {
        super.onStart()
        Log.d("searched", "HomeFragment Start called")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("searched", "HomeFragment onCreateView called")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onPause() {
        super.onPause()
        Log.d("searched", "HomeFragment onPause called")
    }

    override fun onStop() {
        super.onStop()
        Log.d("searched", "HomeFragment onStop called")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("searched", "HomeFragment onDestroyView called")
    }
}