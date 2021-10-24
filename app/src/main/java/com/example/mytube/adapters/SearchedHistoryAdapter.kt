package com.example.mytube.adapters

import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mytube.R
import com.example.mytube.UI.MainActivity
import com.example.mytube.UI.SearchActivity
import com.example.mytube.UI.SearchResultsActivity
import com.example.mytube.UI.VideosViewModel
import com.example.mytube.db.SearchItem
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.youtube.player.internal.e

class SearchedHistoryAdapter(val viewModel: VideosViewModel): RecyclerView.Adapter<SearchedHistoryAdapter.SearchedItemViewHolder>() {
    inner class SearchedItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val searched = itemView.findViewById<TextView>(R.id.searched_for)
        val searchFromCard = itemView.findViewById<ConstraintLayout>(R.id.searched_item)
        fun bind(item: SearchItem) {
            searched.text = item.searchQuery
            searchFromCard.setOnClickListener {
                val intent = Intent(itemView.context, SearchResultsActivity::class.java)
                intent.putExtra("searchQuery", item.searchQuery)
                itemView.context.startActivity(intent)
            }
            searchFromCard.setOnLongClickListener {
                try {
                    MaterialAlertDialogBuilder(itemView.context)
                        .setTitle(searched.text)
                        .setMessage("Remove from search history?")
                        .setPositiveButton("Remove", object: DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                viewModel.deleteSearchItem(searched.text.toString())
                            }
                        })
                        .setNegativeButton("Cancel", object: DialogInterface.OnClickListener{
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                p0?.dismiss()
                            }
                        })
                        .show()
                }
                catch (e: Exception) {
                    Log.e("Delete search item", e.stackTraceToString())
                }
                return@setOnLongClickListener true
            }
        }

    }

    val differCallback = object: DiffUtil.ItemCallback<SearchItem>() {
        override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
            return oldItem.searchQuery == newItem.searchQuery
        }

        override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchedHistoryAdapter.SearchedItemViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.search_history_item, parent, false)
        return SearchedItemViewHolder(layout)
    }

    override fun onBindViewHolder(holder: SearchedHistoryAdapter.SearchedItemViewHolder, position: Int) {
        val searchItem = differ.currentList.get(position)
        holder.bind(searchItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}