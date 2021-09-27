package com.example.mytube.UI

import android.content.Intent
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mytube.R
import com.example.mytube.db.SearchDatabase
import com.example.mytube.repository.VideosRepository
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: VideosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNavBar = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        bottomNavBar.setupWithNavController(navHostFragment.findNavController())

        val searchButton = findViewById<ImageView>(R.id.search)
        searchButton.setOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
        }

        val repository: VideosRepository = VideosRepository(SearchDatabase.getSearchDatabase(this))
        val viewModelFactory = VideosViewModelProviderFactory(repository)
        viewModel = ViewModelProvider(this,viewModelFactory).get(VideosViewModel::class.java)
    }
}