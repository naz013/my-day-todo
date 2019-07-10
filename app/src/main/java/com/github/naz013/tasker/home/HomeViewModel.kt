package com.github.naz013.tasker.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.github.naz013.tasker.data.AppDb
import com.github.naz013.tasker.data.TaskGroup

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val data: LiveData<List<TaskGroup>> = AppDb.getInMemoryDatabase(application.applicationContext).groupDao().loadAll()
}