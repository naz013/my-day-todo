package com.github.naz013.tasker.utils

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
fun Calendar.sameDayAs(calendar: Calendar): Boolean {
    val d = this.get(Calendar.DAY_OF_MONTH)
    val m = this.get(Calendar.MONTH)
    val y = this.get(Calendar.YEAR)
    val d1 = calendar.get(Calendar.DAY_OF_MONTH)
    val m1 = calendar.get(Calendar.MONTH)
    val y1 = calendar.get(Calendar.YEAR)
    return d == d1 && m == m1 && y == y1
}

fun Date.toCalendar(): Calendar {
    val calendar = Calendar.getInstance()
    calendar.time = this
    return calendar
}