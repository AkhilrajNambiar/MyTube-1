package com.example.mytube.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface WatchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(video: WatchHistoryItem)

    @Delete
    suspend fun delete(video: WatchHistoryItem)

    @Query("select * from watchHistory order by videoWatchedTime desc limit 15")
    fun getMostRecentWatchItems(): LiveData<List<WatchHistoryItem>>

    @Query("select * from watchHistory order by videoWatchedTime desc")
    fun getCompleteWatchHistory(): LiveData<List<WatchHistoryItem>>
}