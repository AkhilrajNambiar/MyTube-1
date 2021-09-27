package com.example.mytube.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [
    SearchItem::class
], version = 2, exportSchema = false)
abstract class SearchDatabase: RoomDatabase() {
    abstract fun getDao(): SearchHistoryDao

    companion object{
        private var INSTANCE: SearchDatabase? = null

        fun getSearchDatabase(context: Context): SearchDatabase {
            var instance = INSTANCE
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    SearchDatabase::class.java,
                    "search_history_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
            }
            return instance
        }
    }
}