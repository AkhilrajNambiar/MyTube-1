package com.example.mytube.db

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "searchedItem")
data class SearchItem(
    @PrimaryKey(autoGenerate = false) val searchQuery: String
)
