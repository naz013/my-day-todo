package com.github.naz013.tasker.utils

import android.view.View
import kotlinx.coroutines.*
import java.util.*

suspend fun <T> withUIContext(block: suspend CoroutineScope.() -> T)
        : T = withContext(Dispatchers.Main, block)

fun launchDefault(start: CoroutineStart = CoroutineStart.DEFAULT, block: suspend CoroutineScope.() -> Unit)
        : Job = GlobalScope.launch(Dispatchers.Default, start, block)

fun launchIo(start: CoroutineStart = CoroutineStart.DEFAULT, block: suspend CoroutineScope.() -> Unit)
        : Job = GlobalScope.launch(Dispatchers.IO, start, block)

fun View.isVisible(): Boolean = visibility == View.VISIBLE

fun View.isNotVisible(): Boolean = visibility == View.INVISIBLE

fun View.isGone(): Boolean = visibility == View.GONE

fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

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