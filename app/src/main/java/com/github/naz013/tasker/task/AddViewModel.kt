package com.github.naz013.tasker.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.data.Task
import com.github.naz013.tasker.data.TaskGroup
import com.github.naz013.tasker.utils.*
import java.util.*

class AddViewModel(application: Application, val id: Int) : AndroidViewModel(application) {

    private val mDb = AppDb.getInMemoryDatabase(application.applicationContext)
    val data: LiveData<TaskGroup?> = mDb.groupDao().loadById(id)

    fun saveTask(summary: String, group: TaskGroup, important: Boolean) {
        launchDefault {
            group.tasks.add(Task((UUID.randomUUID().mostSignificantBits and Long.MAX_VALUE).toInt(), false, summary, group.id, important, "", ""))
            mDb.groupDao().insert(group)
            val googleDrive = GoogleDrive(getApplication())
            val localDrive = LocalDrive(getApplication())
            googleDrive.saveToDrive()
            localDrive.saveToDrive()
        }
    }

    fun saveGroup(group: TaskGroup) {
        launchDefault {
            mDb.groupDao().insert(group)
            if (group.notificationEnabled) {
                withUIContext {
                    Notifier(getApplication()).showNotification(group)
                }
            }
        }
    }

    class Factory(private val application: Application, val id: Int) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AddViewModel(application, id) as T
        }
    }
}