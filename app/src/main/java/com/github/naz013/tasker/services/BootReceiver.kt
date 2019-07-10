package com.github.naz013.tasker.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.utils.Notifier
import com.github.naz013.tasker.utils.Prefs
import com.github.naz013.tasker.utils.launchDefault
import com.github.naz013.tasker.utils.withUIContext

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == null || intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val prefs = Prefs.getInstance(context)
        val boot = prefs.getBootNotification()
        if (boot == Prefs.ENABLED) {
            launchDefault {
                val db = AppDb.getInMemoryDatabase(context)
                val groups = db.groupDao().getAll()
                withUIContext {
                    val notifier = Notifier(context)
                    groups.forEach {
                        if (it.notificationEnabled) notifier.showNotification(it)
                        else notifier.hideNotification(it)
                    }
                }
            }
        } else if (boot == Prefs.CUSTOM) {
            val ids = prefs.getStringList(Prefs.BOOT_IDS)
            if (ids.isNotEmpty()) {
                launchDefault {
                    val db = AppDb.getInMemoryDatabase(context)
                    val groups = db.groupDao().getAll()
                    withUIContext {
                        val notifier = Notifier(context)
                        groups.filter { ids.contains(it.id.toString()) }.forEach {
                            if (it.notificationEnabled) notifier.showNotification(it)
                            else notifier.hideNotification(it)
                        }
                    }
                }
            }
        }
    }
}
