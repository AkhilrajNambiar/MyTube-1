package com.example.mytube.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "searchedItem")
data class SearchItem(
    @PrimaryKey(autoGenerate = false) val searchQuery: String
)
