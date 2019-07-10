package com.github.naz013.tasker.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.BaseFragment
import com.github.naz013.tasker.data.TaskGroup
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : BaseFragment() {

    private var mAdapter: GroupsListAdapter = GroupsListAdapter()
    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.onClick { openSettings() }

        mAdapter.callback = { position, action ->
            performAction(position, action)
        }

        if (isTablet() && isHorizontal()) {
            tasksList.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        } else {
            tasksList.layoutManager = LinearLayoutManager(context)
        }
        tasksList.adapter = mAdapter
        updateEmpty()

        initViewModel()
    }

    private fun performAction(group: TaskGroup, action: Int) {
        when (action) {
            GroupsListAdapter.OPEN -> openGroup(group)
            GroupsListAdapter.ADD -> openAddScreen(group)
        }
    }

    private fun openAddScreen(group: TaskGroup) {
        val direction = HomeFragmentDirections.actionHomeFragmentToAddTaskFragment()
        direction.argId = group.id
        findNavController().navigate(direction)
    }

    private fun openGroup(group: TaskGroup) {
        val direction = HomeFragmentDirections.actionHomeFragmentToViewGroupFragment()
        direction.argId = group.id
        findNavController().navigate(direction)
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        viewModel.data.observe(this, Observer { data -> if (data != null) {
            mAdapter.setData(data)
            updateEmpty()
        } })
    }

    private fun updateEmpty() {
        if (mAdapter.itemCount == 0) {
            emptyView.visibility = View.VISIBLE
        } else {
            emptyView.visibility = View.GONE
        }
    }

    private fun openSettings() {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToSettingsFragment())
    }
}