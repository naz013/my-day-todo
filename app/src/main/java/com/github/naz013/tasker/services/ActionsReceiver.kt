package com.github.naz013.tasker.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.utils.Notifier
import com.github.naz013.tasker.utils.launchIo
import com.github.naz013.tasker.utils.withUIContext

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
class ActionsReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_CANCEL_NOTIFICATION = "com.github.naz013.tasker.CANCEL"
        const val ARG_ID = "_id"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ActionsReceiver", "onReceive: $intent")
        if (intent != null && context != null) {
            val action = intent.action
            when (action) {
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