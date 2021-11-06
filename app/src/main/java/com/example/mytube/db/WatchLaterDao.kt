package com.example.mytube.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WatchLaterDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(video: WatchLaterItem)

    @Delete
    suspend fun delete(video: WatchLaterItem)

    @Update
    suspend fun update(video: WatchLaterItem)

    @Query("select * from watchLater order by videoViews desc")
    fun getMostPopularWatchLaterVideos(): LiveData<List<WatchLaterItem>>

    @Query("select * from watchLater order by videoPublishedDate desc")
    fun getMostRecentlyPublishedVideos(): LiveData<List<WatchLaterItem>>

    @Query("select * from watchLater order by videoPublishedDate")
    fun getOldestPublishedVideos(): LiveData<List<WatchLaterItem>>

    @Query("select * from watchLater order by videoAddedTime desc")
    fun getMostRecentlyAddedVideos(): LiveData<List<WatchLaterItem>>

    @Query("select * from watchLater order by videoAddedTime")
    fun getMosOldestAddedVideos(): LiveData<List<WatchLaterItem>>

    @Query("select count(*) from watchLater")
    fun getCountOfWatchLaterVideos(): LiveData<Int>

    @Query("select count(*) from watchLater where not videoWatchedAfterAddingToWatchLater")
    fun getCountOfVideosNotWatchedTillNow(): LiveData<Int>
}