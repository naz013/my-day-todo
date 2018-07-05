package com.github.naz013.tasker.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.utils.Notifier
import com.github.naz013.tasker.utils.Prefs
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

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
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == null || intent.action != Intent.ACTION_BOOT_COMPLETED) return
        val prefs = Prefs.getInstance(context)
        val boot = prefs.getBootNotification()
        if (boot == Prefs.ENABLED) {
            launch(CommonPool) {
                val db = AppDb.getInMemoryDatabase(context)
                val groups = db.groupDao().getAll()
                withContext(UI) {
                    val notifier = Notifier(context)
                    groups.forEach {
                        if (it.notificationEnabled) notifier.showNotification(it)
                        else notifier.hideNotification(it)
                    }
                }
            }
        } else if (boot == Prefs.CUSTOM) {
            val ids = prefs.getStringList(Prefs.BOOT_IDS)
            if (!ids.isEmpty()) {
                launch(CommonPool) {
                    val db = AppDb.getInMemoryDatabase(context)
                    val groups = db.groupDao().getAll()
                    withContext(UI) {
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
