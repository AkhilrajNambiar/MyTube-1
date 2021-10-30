package com.example.mytube.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.mytube.API.RetrofitInstance.Companion.api
import com.example.mytube.db.SearchDatabase
import com.example.mytube.db.SearchItem
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class VideosRepository(val db: SearchDatabase) {

    suspend fun getMostPopularVideos() = api.getMostPopularVideosList()

    suspend fun getNextVideos(nextPageId: String) = api.getNextVideosList(nextPageId = nextPageId)

    suspend fun getChannel(channelId: String) = api.getChannelFromId(id = channelId)

    suspend fun getSearchResults(query: String) = api.getSearchResults(searchQuery = query)

    suspend fun getNextSearchResults(query: String, nextPageId: String) = api.getNextSearchResults(searchQuery = query, pageToken = nextPageId)

    suspend fun getVideoDetails(id: String) = api.getVideoDetails(id = id)

    suspend fun getCommentsForVideo(videoId: String) = api.getCommentsForVideo(videoId = videoId)

    suspend fun getCommentReplies(parentComment: String) = api.getCommentReplies(parentCommentId = parentComment)

    suspend fun getCompleteChannelDetails(channelId: String) = api.getCompleteChannelDetails(id = channelId)

    suspend fun getChannelSections(channelId: String) = api.getChannelSections(channelId = channelId)

    suspend fun getChannelPlaylists(channelId: String) = api.getChannelPlaylists(channelId = channelId)

    suspend fun getPlaylistItems(id: String) = api.getPlaylistItems(id = id)

    suspend fun getVideosRelatedToCurrentVideo(videoId: String, nextPageId: String?) = api.getVideosRelatedToCurrentVideo(relatedToVideoId = videoId, pageToken = nextPageId)

    suspend fun getPoplarVideosOfChannel(channelId: String, pageToken: String?) = api.getPopularVideosOfChannel(channelId = channelId, pageToken = pageToken)

    suspend fun insertSearchedItem(item: String) = db.getDao().insert(SearchItem(item))

    suspend fun deleteSearchedItem(item: String) = db.getDao().delete(SearchItem(item))

    fun getSearchHistory() = db.getDao().getAllSearchedItems()

    @RequiresApi(Build.VERSION_CODES.O)
    fun findMillisDifference(str1: String) : Map<String, Int>{
        // Converting the Time in string form to Time in LocalDateTime form
        val publishedTime = LocalDateTime.parse(str1.slice(0 until str1.length - 1))
        // Getting the current Time
        val time1 = Calendar.getInstance()
        // Converting the current time to Milliseconds
        val milliseconds = time1.timeInMillis
        // Converting the publishedTime to Milliseconds
        val millis2 = publishedTime.atOffset(ZoneOffset.UTC).toInstant().toEpochMilli()
        val difference = (milliseconds - millis2).toString()
        if(difference.length <= 3) return mapOf("seconds" to 0)
        else if(difference.length == 4) return mapOf("seconds" to difference[0].code)
        val milliStart = difference.length - 3
        val seconds = difference.slice(0 until milliStart).toLong()
        val sec = seconds % 60
        val mins = (seconds / 60) % 60
        val hrs = (seconds / 3600) % 24
        val days = (seconds / (3600 * 24)) % 7
        val weeks = (seconds / (3600 * 24 * 7)) % 4
        val months = (seconds / (3600 * 24 * 30)) % 12
        val years = seconds / (3600 * 24 * 30 * 12)
        if(years.toInt() == 0) {
            if(months.toInt() == 0) {
                if(weeks.toInt() == 0) {
                    if(days.toInt() == 0) {
                        if(hrs.toInt() == 0) {
                            if(mins.toInt() == 0) {
                                return mapOf("seconds" to seconds.toInt())
                            }
                            else {
                                return mapOf("minutes" to mins.toInt())
                            }
                        }
                        else {
                            return mapOf("hours" to hrs.toInt())
                        }
                    }
                    else {
                        return mapOf("days" to days.toInt())
                    }
                }
                else {
                    return mapOf("weeks" to weeks.toInt())
                }
            }
            else {
                return mapOf("months" to months.toInt())
            }
        }
        else {
            return mapOf("years" to years.toInt())
        }
    }
}