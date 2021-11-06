package com.example.mytube.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [
    SearchItem::class,
    WatchHistoryItem::class,
    WatchLaterItem::class,
    LikedVideoItem::class
], version = 8, exportSchema = false)
abstract class SearchDatabase: RoomDatabase() {
    abstract fun getDao(): SearchHistoryDao

    abstract fun getWatchHistoryDao(): WatchHistoryDao

    abstract fun getWatchLaterDao(): WatchLaterDao

    abstract fun getLikedVideosDao(): LikedVideosDao

    companion object{
        private var INSTANCE: SearchDatabase? = null

        fun getSearchDatabase(context: Context): SearchDatabase {
            var instance = INSTANCE
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    SearchDatabase::class.java,
                    "history_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
            }
            return instance
        }
    }
}