package com.example.mytube.db

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.sql.Date
import java.time.LocalDateTime

class DateConvertors {
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun getDateFromString(date: String): LocalDateTime {
        return LocalDateTime.parse(date)
    }

    @TypeConverter
    fun convertDateToString(date: LocalDateTime): String {
        return date.toString()
    }
}