package com.github.naz013.tasker.settings.groups

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.data.TaskGroup
import com.github.naz013.tasker.utils.Notifier
import com.github.naz013.tasker.utils.launchDefault

class GroupsViewModel(application: Application) : AndroidViewModel(application) {

    private val mDb = AppDb.getInMemoryDatabase(application.applicationContext)
    val data: LiveData<List<TaskGroup>> = mDb.groupDao().loadAll()

    fun deleteGroup(group: TaskGroup) {
        if (group.notificationEnabled) Notifier(getApplication()).hideNotification(group)
        launchDefault {
            mDb.groupDao().delete(group)
        }
    }

    fun saveGroups(items: MutableList<TaskGroup>?) {
        if (items != null) {
            launchDefault {
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