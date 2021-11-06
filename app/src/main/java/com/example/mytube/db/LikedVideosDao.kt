package com.example.mytube.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface LikedVideosDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(video: LikedVideoItem)

    @Delete
    suspend fun delete(video: LikedVideoItem)

    @Update
    suspend fun update(video: LikedVideoItem)

    @Query("select * from likedVideos order by videoAddedTime desc")
    fun getLikedVideos(): LiveData<List<LikedVideoItem>>
}