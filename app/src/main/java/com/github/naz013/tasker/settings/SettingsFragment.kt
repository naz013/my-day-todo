package com.github.naz013.tasker.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.NestedFragment
import com.github.naz013.tasker.settings.backup.BackupSettingsFragment
import com.github.naz013.tasker.settings.groups.GroupsFragment
import com.github.naz013.tasker.utils.Prefs
import com.mcxiaoke.koi.ext.onClick
import com.mcxiaoke.koi.ext.onLongClick
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

        infoButton.onClick { navInterface?.openFragment(AboutFragment.newInstance(), AboutFragment.TAG) }

        favButton.onClick { changeFav() }
        favButton.onLongClick { openExtraSettings(getString(R.string.favorite_first), Prefs.IMPORTANT_FIRST, Prefs.IMPORTANT_FIRST_IDS) }

        dayButton.onClick { changeDay() }
        dayButton.onLongClick { openExtraSettings(getString(R.string.clear_every_day), Prefs.CLEAR_GROUP, Prefs.CLEAR_GROUP_IDS) }

        uncheckButton.onClick { changeUncheck() }
        uncheckButton.onLongClick { openExtraSettings(getString(R.string.uncheck_tasks_on_new_day), Prefs.CLEAR_CHECKS, Prefs.CLEAR_CHECKS_IDS) }

        fontButton.onClick { navInterface?.openFragment(FontSizeSettingsFragment.newInstance(), FontSizeSettingsFragment.TAG) }
        groupButton.onClick { navInterface?.openFragment(GroupsFragment.newInstance(), GroupsFragment.TAG) }
        backupButton.onClick { navInterface?.openFragment(BackupSettingsFragment.newInstance(), BackupSettingsFragment.TAG) }
    }

    override fun onBackStackResume() {
        super.onBackStackResume()
        val prefs = Prefs.getInstance(context!!)
        initValue(favIcon, prefs.getImportant())
        initValue(dayIcon, prefs.getClearOnDay())
        initValue(uncheckIcon, prefs.getClearChecks())
    }

    private fun openExtraSettings(title: String, key: String, keyList: String): Boolean {
        navInterface?.openFragment(SwitchableSettingsFragment.newInstance(title, key, keyList), SwitchableSettingsFragment.TAG)
        return true
    }

    private fun initValue(imageView: ImageView, value: Int) {
        when (value) {
            1 -> imageView.setBackgroundResource(R.drawable.round_green)
            0 -> imageView.setBackgroundResource(R.drawable.round_red)
            else -> imageView.setBackgroundResource(R.drawable.round_orange)
        }
    }

    private fun changeUncheck() {
        val prefs = Prefs.getInstance(context!!)
        val value = prefs.getClearChecks()
        if (value == 0) {
            prefs.setClearChecks(1)
        } else {
            prefs.setClearChecks(0)
        }
        initValue(uncheckIcon, prefs.getClearChecks())
    }

    private fun changeFav() {
        val prefs = Prefs.getInstance(context!!)
        val value = prefs.getImportant()
        if (value == 0) {
            prefs.setImportant(1)
        } else {
            prefs.setImportant(0)
        }
        initValue(favIcon, prefs.getImportant())
    }

    private fun changeDay() {
        val prefs = Prefs.getInstance(context!!)
        val value = prefs.getClearOnDay()
        if (value == 0) {
            prefs.setClearOnDay(1)
        } else {
            prefs.setClearOnDay(0)
        }
        initValue(dayIcon, prefs.getClearOnDay())
    }
}