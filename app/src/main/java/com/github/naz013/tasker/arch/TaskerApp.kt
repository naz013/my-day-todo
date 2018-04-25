package com.github.naz013.tasker.arch

import android.app.Application
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.data.TaskGroup
import com.github.naz013.tasker.utils.Prefs
import com.github.naz013.tasker.utils.TimeUtils
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async

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
class TaskerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        async(CommonPool) {
            val db = AppDb.getInMemoryDatabase(this@TaskerApp)
            val groups = db.groupDao().getAll()
            if (groups.isEmpty()) {
                db.groupDao().insert(TaskGroup(0, "#FF4081", 0, "Todo", mutableListOf()))
                db.groupDao().insert(TaskGroup(0, "#69F0AE", 1, "Places to go", mutableListOf()))
                db.groupDao().insert(TaskGroup(0, "#FFAB40", 2, "Talk with", mutableListOf()))
            } else if (!TimeUtils.isSameDay(Prefs.getInstance(this@TaskerApp).getLastLaunch())) {
                val prefs = Prefs.getInstance(this@TaskerApp)
                val clear = prefs.getClearOnDay()
                if (clear == Prefs.ENABLED) {
                    groups.forEach {
                        it.tasks.clear()
                    }
                    db.groupDao().insert(groups)
                } else if (clear == Prefs.CUSTOM) {
                    val ids = prefs.getStringList(Prefs.CLEAR_GROUP_IDS)
                    if (!ids.isEmpty()) {
                        groups.filter { ids.contains(it.id.toString()) }.forEach {
                            it.tasks.clear()
                        }
                        db.groupDao().insert(groups)
                    }
                }

                val unCheck = prefs.getClearChecks()
                if (unCheck == Prefs.ENABLED) {
                    groups.forEach {
                        it.tasks.forEach { it.done = false }
                    }
                    db.groupDao().insert(groups)
                } else if (unCheck == Prefs.CUSTOM) {
                    val ids = prefs.getStringList(Prefs.CLEAR_CHECKS_IDS)
                    if (!ids.isEmpty()) {
                        groups.filter { ids.contains(it.id.toString()) }.forEach {
                            it.tasks.forEach { it.done = false }
                        }
                        db.groupDao().insert(groups)
                    }
                }
            }
        }
    }
}