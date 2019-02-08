package com.github.naz013.tasker.group.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.BaseFragment
import com.github.naz013.tasker.data.Task
import com.github.naz013.tasker.data.TaskGroup
import com.github.naz013.tasker.task.AddViewModel
import com.github.naz013.tasker.utils.*
import com.google.android.material.snackbar.Snackbar
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.fragment_view_group.*

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
class ViewGroupFragment : BaseFragment() {

    private var mGroupId: Int = 0
    private var mGroup: TaskGroup? = null
    private lateinit var viewModel: AddViewModel
    private val mAdapter = TasksListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val safeArgs = ViewGroupFragmentArgs.fromBundle(it)
            mGroupId = safeArgs.argId
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.onClick { findNavController().navigateUp() }
        fabAdd.onClick {
            val direction = ViewGroupFragmentDirections.actionViewGroupFragmentToAddTaskFragment()
            direction.argId = mGroupId
            findNavController().navigate(direction)
        }
        fabNotification.onClick { showNotification() }

        if (isTablet() && isHorizontal()) {
            tasksList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        } else {
            tasksList.layoutManager = LinearLayoutManager(context)
        }

        mAdapter.callback = { list -> saveUpdates(list) }
        mAdapter.deleteCallback = { position -> showSnackbar(position) }
        tasksList.adapter = mAdapter

        updateEmpty()

        initViewModel()
    }

    private fun showNotification() {
        val group = mGroup ?: return
        group.notificationEnabled = !group.notificationEnabled
        viewModel.saveGroup(group)
    }

    private fun updateEmpty() {
        if (mAdapter.itemCount == 0) {
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
        }
    }

    private fun showSnackbar(position: Int) {
        val snack = Snackbar.make(coordinator, getString(R.string.delete_this_task_), Snackbar.LENGTH_LONG)
        snack.setAction(getString(R.string.yes)) { mAdapter.delete(position) }
        snack.setActionTextColor(ContextCompat.getColor(context!!, R.color.colorRed))
        snack.show()
    }

    private fun saveUpdates(list: List<Task>) {
        val group = mGroup
        if (group != null) {
            group.tasks.clear()
            group.tasks.addAll(list)
            viewModel.saveGroup(group)
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this, AddViewModel.Factory(activity?.application!!, mGroupId)).get(AddViewModel::class.java)
        viewModel.data.observe(this, Observer { group -> if (group != null) showGroup(group) })
    }

    private fun showGroup(group: TaskGroup) {
        mGroup = group

        fabNotification.setImageResource(if (group.notificationEnabled) R.drawable.ic_silence else R.drawable.ic_alarm)
        if (group.notificationEnabled) {
            Notifier(context!!).showNotification(group)
        } else {
            Notifier(context!!).hideNotification(group)
        }

        titleView.text = group.name
        var list = group.tasks
        val important = Prefs.getInstance(context!!).getImportant()
        val importantIds = Prefs.getInstance(context!!).getStringList(Prefs.IMPORTANT_FIRST_IDS)
        if (important == Prefs.ENABLED || (important == Prefs.CUSTOM && importantIds.contains(group.id.toString()))) {
            list = list.sortedByDescending { it.important }.toMutableList()
        }
        mAdapter.setData(list.sortedByDescending { it.dt }.sortedBy { it.done })
        updateEmpty()
    }

    override fun onDestroy() {
        super.onDestroy()
        backupData()
    }

    private fun backupData() {
        val app = activity?.application ?: return
        launchIo {
            val googleDrive = GoogleDrive(app)
            val localDrive = LocalDrive(app)
            googleDrive.saveToDrive()
            localDrive.saveToDrive()
        }
    }
}