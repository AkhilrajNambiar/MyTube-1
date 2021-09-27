package com.example.mytube.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface SearchHistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(searchedFor: SearchItem)

    @Delete
    suspend fun delete(searchedFor: SearchItem)

    @Query("select * from searchedItem")
    fun getAllSearchedItems(): LiveData<List<SearchItem>>
}