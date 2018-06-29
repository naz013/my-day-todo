package com.github.naz013.tasker.group

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.NestedFragment
import com.github.naz013.tasker.data.TaskGroup
import com.github.naz013.tasker.task.AddViewModel
import com.github.naz013.tasker.utils.GroupColorsController
import com.github.naz013.tasker.utils.Prefs
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.fragment_add_group.*

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
class AddGroupFragment : NestedFragment() {

    companion object {
        const val TAG = "AddGroupFragment"
        private const val ARG_ID = "arg_id"
        fun newInstance(id: Int): AddGroupFragment {
            val fragment = AddGroupFragment()
            val bundle = Bundle()
            bundle.putInt(ARG_ID, id)
            fragment.arguments = bundle
            return fragment
        }
    }

    private var mGroupId: Int = 0
    private var mGroup: TaskGroup? = null
    private lateinit var viewModel: AddViewModel
    private lateinit var colorsController: GroupColorsController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mGroupId = arguments?.getInt(AddGroupFragment.ARG_ID)!!
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.onClick { navInterface?.moveBack() }

        initViewModel()

        if (!Prefs.getInstance(context!!).isGroupBannerShown()) {
            closeButton.onClick { hideBanner() }
            bannerView.visibility = View.VISIBLE
        } else bannerView.visibility = View.GONE

        colorsController = GroupColorsController()
        colorsController.fillSlider(colorSwitchContainer)
    }

    private fun hideBanner() {
        Prefs.getInstance(context!!).setGroupBannerShown(true)
        bannerView.visibility = View.GONE
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this, AddViewModel.Factory(activity?.application!!, mGroupId)).get(AddViewModel::class.java)
        viewModel.data.observe(this, Observer { group -> if (group != null) showGroup(group) })
    }

    private fun showGroup(group: TaskGroup) {
        mGroup = group
        nameView.setText(group.name)
        titleView.text = getString(R.string.edit_group)
        colorsController.selectColor(group.color)
    }

    override fun onStop() {
        super.onStop()
        val summary = nameView.text.toString().trim()
        var group = mGroup
        if (!TextUtils.isEmpty(summary)) {
            if (group == null) {
                group = TaskGroup().apply {
                    position = 100
                }
            }
            viewModel.saveGroup(group.apply {
                this.name = summary
                this.color = colorsController.getSelectedColor()
            })
        }
    }
}