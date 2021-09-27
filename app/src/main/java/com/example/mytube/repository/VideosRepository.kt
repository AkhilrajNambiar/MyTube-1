package com.example.mytube.repository

import com.example.mytube.API.RetrofitInstance.Companion.api
import com.example.mytube.db.SearchDatabase
import com.example.mytube.db.SearchItem

class VideosRepository(val db: SearchDatabase) {

    suspend fun getMostPopularVideos() = api.getMostPopularVideosList()

    suspend fun getNextVideos(nextPageId: String) = api.getNextVideosList(nextPageId = nextPageId)

    suspend fun getChannel(channelId: String) = api.getChannelFromId(id = channelId)

    suspend fun getSearchResults(query: String) = api.getSearchResults(searchQuery = query)

    suspend fun getNextSearchResults(query: String, nextPageId: String) = api.getNextSearchResults(searchQuery = query, pageToken = nextPageId)

    suspend fun insertSearchedItem(item: String) = db.getDao().insert(SearchItem(item))

    suspend fun deleteSearchedItem(item: String) = db.getDao().delete(SearchItem(item))

    fun getSearchHistory() = db.getDao().getAllSearchedItems()

}