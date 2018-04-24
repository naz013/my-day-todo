package com.github.naz013.tasker.settings.groups

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.NestedFragment
import com.github.naz013.tasker.arch.OnStartDragListener
import com.github.naz013.tasker.data.TaskGroup
import com.github.naz013.tasker.group.AddGroupFragment
import com.github.naz013.tasker.group.view.ViewGroupFragment
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
    private var mAdapter: GroupsListAdapter? = null
    private lateinit var viewModel: GroupsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.onClick { moveBack() }
        fabAdd.onClick { openAddScreen() }

        mAdapter = GroupsListAdapter()
        mAdapter?.mDragStartListener = this
        mAdapter?.callback = { position, action ->
            performAction(position, action)
        }

        tasksList.layoutManager = LinearLayoutManager(context)
        tasksList.adapter = mAdapter

        val callback = SimpleItemTouchHelperCallback(mAdapter!!)
        mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper?.attachToRecyclerView(tasksList)

        initViewModel()
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
        navInterface?.openFragment(ViewGroupFragment.newInstance(group.id), ViewGroupFragment.TAG)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(GroupsViewModel::class.java)
        viewModel.data.observe(this, Observer { data -> if (data != null) updateList(data) })
    }

    private fun updateList(data: List<TaskGroup>) {
        mAdapter?.setData(data)
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        mItemTouchHelper?.startDrag(viewHolder)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveGroups(mAdapter?.items)
    }
}