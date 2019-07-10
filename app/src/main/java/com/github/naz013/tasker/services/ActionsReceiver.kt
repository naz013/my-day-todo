package com.github.naz013.tasker.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.utils.Notifier
import com.github.naz013.tasker.utils.launchIo
import com.github.naz013.tasker.utils.withUIContext

class ActionsReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_CANCEL_NOTIFICATION = "com.github.naz013.tasker.CANCEL"
        const val ARG_ID = "_id"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && context != null) {
            when (intent.action) {
                ACTION_CANCEL_NOTIFICATION -> cancelNotification(context, intent.getIntExtra(ARG_ID, 0))
            }
        }
    }

    private fun cancelNotification(context: Context, id: Int) {
        if (id == 0) return
        launchIo {
            val appDb = AppDb.getInMemoryDatabase(context)
            val notifier = Notifier(context)
            val group = appDb.groupDao().getById(id)
            if (group != null) {
                group.notificationEnabled = false
                appDb.groupDao().insert(group)
                withUIContext { notifier.hideNotification(group) }
            }
        }
    }
}