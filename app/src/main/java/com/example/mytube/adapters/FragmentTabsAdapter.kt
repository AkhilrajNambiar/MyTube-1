package com.example.mytube.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.mytube.UI.ChannelDetailsActivity
import com.example.mytube.UI.Fragments.*

class FragmentTabsAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            1 -> {
                ChannelVideosFragment()
            }
            2 -> {
                ChannelPlaylistsFragment()
            }
            3 -> {
                RelatedChannelsFragment()
            }
            4 -> {
                AboutChannelFragment()
            }
            else -> ChannelHomeFragment()
        }
    }
}