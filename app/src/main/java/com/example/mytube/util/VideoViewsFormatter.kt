package com.example.mytube.util

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
    }
}