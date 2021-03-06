package com.github.naz013.tasker.task

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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
import com.github.naz013.tasker.utils.Prefs
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.fragment_add.*
import java.lang.Exception

class AddTaskFragment : BaseFragment() {

    private var mGroupId: Int = 0
    private var mGroup: TaskGroup? = null
    private lateinit var viewModel: AddViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val safeArgs = AddTaskFragmentArgs.fromBundle(it)
            mGroupId = safeArgs.argId
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab.onClick { findNavController().navigateUp() }

        initViewModel()

        if (!Prefs.getInstance(context!!).isCreateBannerShown()) {
            closeButton.onClick { hideBanner() }
            bannerView.visibility = View.VISIBLE
        } else bannerView.visibility = View.GONE
    }

    private fun hideBanner() {
        Prefs.getInstance(context!!).setCreateBannerShown(true)
        bannerView.visibility = View.GONE
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this, AddViewModel.Factory(activity?.application!!, mGroupId)).get(AddViewModel::class.java)
        viewModel.data.observe(this, Observer { group -> if (group != null) showGroup(group) })
    }

    private fun showGroup(group: TaskGroup) {
        mGroup = group
        groupTitleView.text = group.name
        try {
            val drawable = groupTitleView.background as GradientDrawable
            drawable.setColor(Color.parseColor(group.color))
        } catch (e: Exception) {
        }
    }

    override fun onStop() {
        super.onStop()
        val summary = summaryView.text.toString().trim()
        val group = mGroup
        if (!TextUtils.isEmpty(summary) && group != null && isRemoving) {
            hideKeyboard(summaryView)
            viewModel.saveTask(summary, group, favouriteView.isChecked)
        }
    }
}