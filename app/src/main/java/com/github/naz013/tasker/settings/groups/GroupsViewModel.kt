package com.github.naz013.tasker.settings.groups

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.data.TaskGroup
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch

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
class GroupsViewModel(application: Application) : AndroidViewModel(application) {

    private val mDb = AppDb.getInMemoryDatabase(application.applicationContext)
    val data: LiveData<List<TaskGroup>> = mDb.groupDao().loadAll()

    fun deleteGroup(group: TaskGroup) {
        launch(CommonPool) {
            mDb.groupDao().delete(group)
        }
    }

    fun saveGroups(items: MutableList<TaskGroup>?) {
        if (items != null) {
            launch(CommonPool) {
                val newList = mutableListOf<TaskGroup>()
                for (i in 0 until items.size) {
                    newList.add(items[i].apply {
                        this.position = i
                    })
                }
                mDb.groupDao().insert(newList)
            }
        }
    }
}