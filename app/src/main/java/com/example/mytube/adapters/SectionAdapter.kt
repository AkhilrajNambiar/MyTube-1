package com.example.mytube.adapters

import android.opengl.Visibility
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytube.R
import com.example.mytube.UI.ChannelViewModel
import com.example.mytube.models.PlaylistItemsSection

class SectionAdapter(val viewModel: ChannelViewModel): RecyclerView.Adapter<SectionAdapter.SectionViewHolder>() {
    inner class SectionViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(section: PlaylistItemsSection){
            val sectionTitle = itemView.findViewById<TextView>(R.id.section_title)
            val sectionBox = itemView.findViewById<ConstraintLayout>(R.id.section_box)
            val sectionSubtitle = itemView.findViewById<TextView>(R.id.section_subtitle)
            val recyclerView = itemView.findViewById<RecyclerView>(R.id.section_items_recycler_view)
            val playlistAdapter = PlaylistVideosAdapter(viewModel)
            val loadMoreVideos = itemView.findViewById<ImageView>(R.id.show_more_videos)
            recyclerView.apply {
                adapter = playlistAdapter
                layoutManager = LinearLayoutManager(itemView.context)
            }
            // If there is section description then add it
            sectionTitle.text = section.title
            if (section.description.isNotEmpty()) {
                sectionSubtitle.text = section.description
            }
            else {
                // Remove the space that the sectionSubtitle takes
                sectionBox.removeView(sectionSubtitle)
            }
            // At first we load only 4 videos
            // on clicking we load 8 more videos and remove the loadMore image
            loadMoreVideos.setOnClickListener {
                playlistAdapter.differ.submitList(section.playlistItems)
                sectionBox.removeView(loadMoreVideos)
            }
            // show only the first 4 items
            if (section.playlistItems.size > 4) {
                playlistAdapter.differ.submitList(section.playlistItems.slice(0..3))
            }
            else {
                playlistAdapter.differ.submitList(section.playlistItems)
            }
        }
    }

    val differCallback = object: DiffUtil.ItemCallback<PlaylistItemsSection>(){
        override fun areItemsTheSame(oldItem: PlaylistItemsSection, newItem: PlaylistItemsSection): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: PlaylistItemsSection, newItem: PlaylistItemsSection): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SectionViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.section_recycler_view, parent, false)
        return SectionViewHolder(layout)
    }

    override fun onBindViewHolder(holder: SectionViewHolder, position: Int) {
        val section = differ.currentList[position]
        holder.bind(section)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}