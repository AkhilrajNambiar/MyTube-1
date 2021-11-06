package com.example.mytube.util

import android.content.Context
import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.mytube.R
import com.example.mytube.models.Thumbnails
import com.example.mytube.models.ThumbnailsXX
import java.time.LocalDateTime
import java.time.ZoneId

class VideoViewsFormatter {
    companion object {
        fun viewsFormatter(views: String): String{
            val view = views.toLong()
            val kilos = view / 1000L
            val millions = view / 1_000_000L
            val billions = view / 1_000_000_000L
            if (billions == 0L) {
                if (millions == 0L) {
                    if (kilos == 0L) {
                        return views
                    }
                    else {
                        if (views.length == 4) {
                            if (views[1].equals('0')) return "${views[0]}K"
                            else return "${views[0]}.${views[1]}K"
                        }
                        else return "${kilos}K"
                    }
                }
                else {
                    if (views.length == 7) {
                        if (views[1].equals('0')) return "${views[0]}M"
                        else return "${views[0]}.${views[1]}M"
                    }
                    else return "${millions}M"
                }
            }
            else {
                if (views.length == 10) {
                    if (views[1].equals('0')) return "${views[0]}B"
                    else return "${views[0]}.${views[1]}B"
                }
                else return "${billions}B"
            }
        }

        fun timeFormatter(time: Map<String, Int>, context: Context): String {
            if (time.containsKey("seconds")){
                if (time["seconds"] == 0)  return context.resources.getString(
                    R.string.recent
                )
                else {
                    return context.getString(
                        R.string.video_published_time,
                        time["seconds"],
                        context.resources.getQuantityString(
                            R.plurals.secondCount, time["seconds"]!!.toInt()
                        )
                    )
                }
            }
            else if (time.containsKey("minutes")) {
                return context.getString(
                    R.string.video_published_time,
                    time["minutes"],
                    context.resources.getQuantityString(
                        R.plurals.minCount, time["minutes"]!!.toInt()
                    )
                )
            }
            else if (time.containsKey("hours")) {
                return context.getString(
                    R.string.video_published_time,
                    time["hours"],
                    context.resources.getQuantityString(
                        R.plurals.hourCount, time["hours"]!!.toInt())
                )
            }
            else if (time.containsKey("days")) {
                return context.getString(
                    R.string.video_published_time,
                    time["days"],
                    context.resources.getQuantityString(
                        R.plurals.dayCount, time["days"]!!.toInt())
                )
            }
            else if (time.containsKey("weeks")) {
                return context.getString(
                    R.string.video_published_time,
                    time["weeks"],
                    context.resources.getQuantityString(
                        R.plurals.weekCount, time["weeks"]!!.toInt())
                )
            }
            else if (time.containsKey("months")) {
                return context.getString(
                    R.string.video_published_time,
                    time["months"],
                    context.resources.getQuantityString(
                        R.plurals.monthCount, time["months"]!!.toInt())
                )
            }
            else if (time.containsKey("years")) {
                return context.getString(
                    R.string.video_published_time,
                    time["years"],
                    context.resources.getQuantityString(
                        R.plurals.yearCount, time["years"]!!.toInt())
                )
            }
            return ""
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun returnDayFromDate(string: String): Int {
            val date = LocalDateTime.parse(string.slice(0 until string.length-1))
            return date.dayOfMonth
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun returnMonthFromDate(string: String): String {
            val date = LocalDateTime.parse(string.slice(0 until string.length - 1))
            return date.month.toString()
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun returnYearFromDate(string: String): Int {
            val date = LocalDateTime.parse(string.slice(0 until string.length - 1))
            return date.year
        }

        fun obtainThumbnailUrl(thumb: ThumbnailsXX): String {
            if (thumb.maxres.url.isNotEmpty()) {
                return thumb.maxres.url
            }
            else if (thumb.standard.url.isNotEmpty()) {
                return thumb.standard.url
            }
            else if (thumb.high.url.isNotEmpty()) {
                return thumb.high.url
            }
            else if (thumb.medium.url.isNotEmpty()) {
                return thumb.medium.url
            }
            else if (thumb.default.url.isNotEmpty()){
                return thumb.default.url
            }
            return "https://www.labnol.org/images/2008/1.jpg"
        }
        @RequiresApi(Build.VERSION_CODES.O)
        fun getMillisecondsFromLocalTime(timer: String): Long {
            val timeString = timer.slice(0 until timer.length - 1)
            val time = LocalDateTime.parse(timeString)
            val zonedTime = time.atZone(ZoneId.of("Asia/Calcutta"))
            return zonedTime.toInstant().toEpochMilli()
        }
    }
}