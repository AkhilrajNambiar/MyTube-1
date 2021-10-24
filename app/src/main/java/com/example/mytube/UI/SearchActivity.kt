package com.example.mytube.UI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytube.R
import com.example.mytube.adapters.SearchedHistoryAdapter
import com.example.mytube.db.SearchDatabase
import com.example.mytube.repository.VideosRepository

class SearchActivity : AppCompatActivity() {

    lateinit var searchAdapter: SearchedHistoryAdapter
    lateinit var viewModel: VideosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val repository = VideosRepository(SearchDatabase.getSearchDatabase(this))
        val viewModelFactory = VideosViewModelProviderFactory(application, repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(VideosViewModel::class.java)

        val recyclerView = findViewById<RecyclerView>(R.id.searched_terms)
        searchAdapter = SearchedHistoryAdapter(viewModel)
        recyclerView.apply {
            adapter = searchAdapter
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }

        val backButton = findViewById<ImageView>(R.id.ivGoback)
        backButton.apply {
            setOnClickListener {
                finish()
            }
        }

        val searchBar = findViewById<EditText>(R.id.search_box)
        val searchBtn = findViewById<ImageView>(R.id.ivSearch)
        // To allow searching when the user clicks on the search button
        searchBtn.setOnClickListener {
            if (searchBar.text.toString().isNotEmpty()) {
                viewModel.insertSearchItem(searchBar.text.toString())
                val intent = Intent(applicationContext, SearchResultsActivity::class.java)
                intent.putExtra("searchQuery", searchBar.text.toString())
                startActivity(intent)
            }
        }
        // To allow searching when the user clicks on the enter button
        searchBar.setOnKeyListener(View.OnKeyListener{v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                if (searchBar.text.toString().isNotEmpty()) {
                    viewModel.insertSearchItem(searchBar.text.toString())
                    val intent = Intent(applicationContext, SearchResultsActivity::class.java)
                    intent.putExtra("searchQuery", searchBar.text.toString())
                    startActivity(intent)
                }
                return@OnKeyListener true
            }
            return@OnKeyListener false
        })

        viewModel.getSearchHistory().observe(this, Observer { searchItems ->
            searchAdapter.differ.submitList(searchItems)
        })

    }
    override fun onPause() {
        super.onPause()
        Log.d("searched", "Search box activity paused")
    }

    override fun onStop() {
        super.onStop()
        Log.d("searched", "Search box activity stopped")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("searched", "Search box activity destroyed")
    }
}