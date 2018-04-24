package com.github.naz013.tasker.utils

import android.text.TextUtils
import java.text.SimpleDateFormat
import java.util.*

/**
 * Copyright 2018 Nazar Suhovich
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

