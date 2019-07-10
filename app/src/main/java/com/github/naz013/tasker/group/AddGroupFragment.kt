package com.github.naz013.tasker.group

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.BaseFragment
import com.github.naz013.tasker.data.TaskGroup
import com.github.naz013.tasker.task.AddViewModel
import com.github.naz013.tasker.utils.GroupColorsController
import com.github.naz013.tasker.utils.Prefs
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.fragment_add_group.*

class AddGroupFragment : BaseFragment() {

    private var mGroupId: Int = 0
    private var mGroup: TaskGroup? = null
    private lateinit var viewModel: AddViewModel
    private lateinit var colorsController: GroupColorsController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val safeArgs = AddGroupFragmentArgs.fromBundle(it)
            mGroupId = safeArgs.argId
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_group, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.onClick { findNavController().navigateUp()}

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
        if (!TextUtils.isEmpty(summary) && isRemoving) {
            hideKeyboard(nameView)
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