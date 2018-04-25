package com.github.naz013.tasker.group.view

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.NestedFragment
import com.github.naz013.tasker.data.Task
import com.github.naz013.tasker.data.TaskGroup
import com.github.naz013.tasker.task.AddViewModel
import com.github.naz013.tasker.utils.Prefs
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
class ViewGroupFragment : NestedFragment() {

    companion object {
        const val TAG = "ViewGroupFragment"
        private const val ARG_ID = "arg_id"
        fun newInstance(id: Int): ViewGroupFragment {
            val fragment = ViewGroupFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_ID, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var mGroupId: Int = 0
    private var mGroup: TaskGroup? = null
    private lateinit var viewModel: AddViewModel
    private lateinit var mAdapter: TasksListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mGroupId = arguments?.getInt(ARG_ID)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tasksList.layoutManager = LinearLayoutManager(context)
        mAdapter = TasksListAdapter()
        mAdapter.callback = { list -> saveUpdates(list) }
        tasksList.adapter = mAdapter

        initViewModel()
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
        titleView.text = group.name
        var list = group.tasks
        val important = Prefs.getInstance(context!!).getImportant()
        val importantIds = Prefs.getInstance(context!!).getStringList(Prefs.IMPORTANT_FIRST_IDS)
        if (important == Prefs.ENABLED || (important == Prefs.CUSTOM && importantIds.contains(group.id.toString()))) {
            list = list.sortedByDescending { it.important }.toMutableList()
        }
        mAdapter.setData(list.sortedBy { it.done })
    }
}