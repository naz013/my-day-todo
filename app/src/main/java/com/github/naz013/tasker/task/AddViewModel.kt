package com.github.naz013.tasker.task

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.data.Task
import com.github.naz013.tasker.data.TaskGroup
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
class AddViewModel(application: Application, val id: Int) : AndroidViewModel(application) {

    private val mDb = AppDb.getInMemoryDatabase(application.applicationContext)
    val data: LiveData<TaskGroup?> = mDb.groupDao().loadById(id)

    fun saveTask(summary: String, group: TaskGroup, important: Boolean) {
        async(CommonPool) {
            group.tasks.add(Task(0, false, summary, group.id, important, "", ""))
            mDb.groupDao().insert(group)
        }
    }

    class Factory(private val application: Application, val id: Int) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AddViewModel(application, id) as T
        }
    }
}