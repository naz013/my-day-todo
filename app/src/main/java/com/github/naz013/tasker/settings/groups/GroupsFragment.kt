package com.github.naz013.tasker.settings.groups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.NestedFragment
import com.github.naz013.tasker.arch.OnStartDragListener
import com.github.naz013.tasker.data.TaskGroup
import com.github.naz013.tasker.group.AddGroupFragment
import com.github.naz013.tasker.utils.GoogleDrive
import com.github.naz013.tasker.utils.LocalDrive
import com.github.naz013.tasker.utils.launchDefault
import com.google.android.material.snackbar.Snackbar
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.fragment_groups.*


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
class GroupsFragment : NestedFragment(), OnStartDragListener {

    companion object {
        const val TAG = "GroupsFragment"
        fun newInstance(): GroupsFragment {
            return GroupsFragment()
        }
    }

    private var mItemTouchHelper: ItemTouchHelper? = null
    private var mAdapter: GroupsListAdapter = GroupsListAdapter()
    private lateinit var viewModel: GroupsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.onClick { moveBack() }
        fabAdd.onClick { openAddScreen() }

        mAdapter = GroupsListAdapter()
        mAdapter.mDragStartListener = this
        mAdapter.callback = { position, action -> performAction(position, action) }
        mAdapter.deleteCallback = { position -> showSnackbar(position) }

        tasksList.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        tasksList.adapter = mAdapter

        val callback = SimpleItemTouchHelperCallback(mAdapter)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper?.attachToRecyclerView(tasksList)

        updateEmpty()

        initViewModel()
    }

    private fun updateEmpty() {
        if (mAdapter.itemCount == 0) {
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
        }
    }

    private fun showSnackbar(position: Int) {
        val snack = Snackbar.make(coordinator, getString(R.string.delete_this_group_), Snackbar.LENGTH_LONG)
        snack.setAction(getString(R.string.yes)) { mAdapter.delete(position) }
        snack.setActionTextColor(ContextCompat.getColor(context!!, R.color.colorRed))
        snack.show()
    }

    private fun performAction(group: TaskGroup, action: Int) {
        when (action) {
            GroupsListAdapter.EDIT -> openGroup(group)
            GroupsListAdapter.DELETE -> deleteGroup(group)
        }
    }

    private fun deleteGroup(group: TaskGroup) {
        viewModel.deleteGroup(group)
    }

    private fun openAddScreen() {
        navInterface?.openFragment(AddGroupFragment.newInstance(0), AddGroupFragment.TAG)
    }

    private fun openGroup(group: TaskGroup) {
        navInterface?.openFragment(AddGroupFragment.newInstance(group.id), AddGroupFragment.TAG)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(GroupsViewModel::class.java)
        viewModel.data.observe(this, Observer { data -> if (data != null) updateList(data) })
    }

    private fun updateList(data: List<TaskGroup>) {
        mAdapter.setData(data)
        updateEmpty()
    }

    override fun onStartDrag(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        mItemTouchHelper?.startDrag(viewHolder)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveGroups(mAdapter.items)
        backupData()
    }

    private fun backupData() {
        val app = activity?.application ?: return
        launchDefault {
            val googleDrive = GoogleDrive(app)
            val localDrive = LocalDrive(app)
            googleDrive.saveToDrive()
            localDrive.saveToDrive()
        }
    }
}