package com.github.naz013.tasker.utils

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

object TimeUtils {

    private val gmtFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    fun isSameDay(gmt: String?): Boolean {
        val gmt2 = getGmtStamp()
        if (TextUtils.isEmpty(gmt) && TextUtils.isEmpty(gmt2)) return true
        else if (TextUtils.isEmpty(gmt) || TextUtils.isEmpty(gmt2)) return false
        return gmtFormat.parse(gmt).toCalendar().sameDayAs(gmtFormat.parse(gmt2).toCalendar())
    }

    fun getGmtStamp(time: Long = System.currentTimeMillis()): String {
        return gmtFormat.format(Date(time))
    }
}

