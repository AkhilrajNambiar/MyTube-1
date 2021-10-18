package com.example.mytube.util

import android.content.Context
import android.view.View
import android.widget.TextView
import com.example.mytube.R

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

        fun timeFormatter(time: Map<String, Int>, view: TextView, context: Context) {
            if (time.containsKey("seconds")){
                if (time["seconds"] == 0)  view.text = context.resources.getString(
                    R.string.recent
                )
                else {
                    view.text = context.getString(
                        R.string.video_published_time,
                        time["seconds"],
                        context.resources.getQuantityString(
                            R.plurals.secondCount, time["seconds"]!!.toInt()
                        )
                    )
                }
            }
            else if (time.containsKey("minutes")) {
                view.text = context.getString(
                    R.string.video_published_time,
                    time["minutes"],
                    context.resources.getQuantityString(
                        R.plurals.minCount, time["minutes"]!!.toInt()
                    )
                )
            }
            else if (time.containsKey("hours")) {
                view.text = context.getString(
                    R.string.video_published_time,
                    time["hours"],
                    context.resources.getQuantityString(
                        R.plurals.hourCount, time["hours"]!!.toInt())
                )
            }
            else if (time.containsKey("days")) {
                view.text = context.getString(
                    R.string.video_published_time,
                    time["days"],
                    context.resources.getQuantityString(
                        R.plurals.dayCount, time["days"]!!.toInt())
                )
            }
            else if (time.containsKey("weeks")) {
                view.text = context.getString(
                    R.string.video_published_time,
                    time["weeks"],
                    context.resources.getQuantityString(
                        R.plurals.weekCount, time["weeks"]!!.toInt())
                )
            }
            else if (time.containsKey("months")) {
                view.text = context.getString(
                    R.string.video_published_time,
                    time["months"],
                    context.resources.getQuantityString(
                        R.plurals.monthCount, time["months"]!!.toInt())
                )
            }
            else if (time.containsKey("years")) {
                view.text = context.getString(
                    R.string.video_published_time,
                    time["years"],
                    context.resources.getQuantityString(
                        R.plurals.yearCount, time["years"]!!.toInt())
                )
            }
        }
    }
}