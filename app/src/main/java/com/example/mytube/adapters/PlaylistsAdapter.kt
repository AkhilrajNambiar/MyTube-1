package com.example.mytube.adapters

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mytube.R
import com.example.mytube.UI.ChannelViewModel
import com.example.mytube.UI.PlaylistDetailActivity
import com.example.mytube.models.SinglePlaylist

class PlaylistsAdapter(viewModel: ChannelViewModel): RecyclerView.Adapter<PlaylistsAdapter.PlaylistViewHolder>() {

    inner class PlaylistViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val playlistThumbnail = itemView.findViewById<ImageView>(R.id.playlist_thumbnail)
        private val playlistVideoCountSpecial = itemView.findViewById<TextView>(R.id.playlist_video_count_special)
        private val playlistVideoCountSimple = itemView.findViewById<TextView>(R.id.playlist_video_count)
        private val playlistChannel = itemView.findViewById<TextView>(R.id.playlist_channel_name)
        private val playlistTitle = itemView.findViewById<TextView>(R.id.playlist_title)
        private val mainCard = itemView.findViewById<ConstraintLayout>(R.id.playlist_video_card)
        fun bind(playlist: SinglePlaylist) {
            Log.d("playlistsImage", playlist.snippet.thumbnails.high.url)
            Glide.with(itemView)
                .load(
                    if (playlist.snippet.thumbnails.maxres.url.isNotEmpty()) {
                        playlist.snippet.thumbnails.maxres.url
                    }
                    else if (playlist.snippet.thumbnails.standard.url.isNotEmpty()) {
                        playlist.snippet.thumbnails.standard.url
                    }
                    else if (playlist.snippet.thumbnails.high.url.isNotEmpty()) {
                        playlist.snippet.thumbnails.high.url
                    }
                    else if (playlist.snippet.thumbnails.medium.url.isNotEmpty()) {
                        playlist.snippet.thumbnails.medium.url
                    }
                    else {
                        playlist.snippet.thumbnails.default.url
                    }
                )
                .into(playlistThumbnail)
            playlistVideoCountSpecial.text = playlist.contentDetails.itemCount.toString()
            playlistVideoCountSimple.text = itemView.context.resources.getString(R.string.playlist_video_count, playlist.contentDetails.itemCount)
            playlistChannel.text = playlist.snippet.channelTitle
            playlistTitle.text = playlist.snippet.title
            mainCard.setOnClickListener {
                val intent = Intent(itemView.context, PlaylistDetailActivity::class.java)
                val bundle = Bundle().apply {
                    putSerializable("playlist", playlist)
                }
                intent.putExtra("playlist", bundle)
                itemView.context.startActivity(intent)
            }
        }
    }

    val differCallback = object: DiffUtil.ItemCallback<SinglePlaylist>() {
        override fun areItemsTheSame(oldItem: SinglePlaylist, newItem: SinglePlaylist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SinglePlaylist, newItem: SinglePlaylist): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item, parent, false)
        return PlaylistViewHolder(layout)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = differ.currentList[position]
        holder.bind(playlist)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}