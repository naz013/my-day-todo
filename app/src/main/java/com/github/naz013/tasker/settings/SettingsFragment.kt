package com.github.naz013.tasker.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.NestedFragment
import com.github.naz013.tasker.settings.groups.GroupsFragment
import com.github.naz013.tasker.utils.Prefs
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.fragment_settings.*

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
class SettingsFragment : NestedFragment() {

    companion object {
        const val TAG = "SettingsFragment"
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.onClick { navInterface?.moveBack() }
        favButton.onClick { changeFav() }
        dayButton.onClick { changeDay() }
        fontButton.onClick { navInterface?.openFragment(FontSizeSettingsFragment.newInstance(), FontSizeSettingsFragment.TAG) }
        groupButton.onClick { navInterface?.openFragment(GroupsFragment.newInstance(), GroupsFragment.TAG) }

        initValue(favIcon, Prefs.getInstance(context!!).isImportantEnabled())
        initValue(dayIcon, Prefs.getInstance(context!!).isClearOnDay())
    }

    private fun initValue(imageView: ImageView, value: Boolean) {
        if (value) {
            imageView.setBackgroundResource(R.drawable.round_green)
        } else {
            imageView.setBackgroundResource(R.drawable.round_red)
        }
    }

    private fun changeFav() {
        val prefs = Prefs.getInstance(context!!)
        prefs.setImportantEnabled(!prefs.isImportantEnabled())
        initValue(favIcon, prefs.isImportantEnabled())
    }

    private fun changeDay() {
        val prefs = Prefs.getInstance(context!!)
        prefs.setClearOnDay(!prefs.isClearOnDay())
        initValue(dayIcon, prefs.isClearOnDay())
    }
}