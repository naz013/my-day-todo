package com.github.naz013.tasker.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.github.naz013.tasker.R
import com.github.naz013.tasker.SplashScreenActivity
import com.github.naz013.tasker.arch.BaseFragment
import com.github.naz013.tasker.utils.Prefs
import com.github.naz013.tasker.utils.launchDefault
import com.github.naz013.tasker.utils.withUIContext
import com.mcxiaoke.koi.ext.onClick
import com.mcxiaoke.koi.ext.onLongClick
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.coroutines.delay
import timber.log.Timber

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
class SettingsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.onClick { findNavController().navigateUp() }

        infoButton.onClick { findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToAboutFragment()) }

        favButton.onClick { changeFav() }
        favButton.onLongClick { openExtraSettings(getString(R.string.favorite_first), Prefs.IMPORTANT_FIRST, Prefs.IMPORTANT_FIRST_IDS) }

        dayButton.onClick { changeDay() }
        dayButton.onLongClick { openExtraSettings(getString(R.string.clear_every_day), Prefs.CLEAR_GROUP, Prefs.CLEAR_GROUP_IDS) }

        uncheckButton.onClick { changeUncheck() }
        uncheckButton.onLongClick { openExtraSettings(getString(R.string.uncheck_tasks_on_new_day), Prefs.CLEAR_CHECKS, Prefs.CLEAR_CHECKS_IDS) }

        notificationButton.onClick { changeNotification() }
        notificationButton.onLongClick { openExtraSettings(getString(R.string.notification_on_boot), Prefs.BOOT_NOTIFICATION, Prefs.BOOT_IDS) }

        darkModeButton.onClick { changeTheme() }

        fontButton.onClick { findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToFontSizeSettingsFragment()) }
        groupButton.onClick { findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToGroupsFragment()) }
        backupButton.onClick { findNavController().navigate(SettingsFragmentDirections.actionSettingsFragmentToBackupSettingsFragment()) }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Timber.d("onViewStateRestored: ")
        launchDefault {
            delay(250)
            withUIContext {
                if (!isRemoving) {
                    initValues()
                }
            }
        }
    }

    private fun initValues() {
        val prefs = Prefs.getInstance(context!!)
        initValue(favIcon, prefs.getImportant())
        initValue(dayIcon, prefs.getClearOnDay())
        initValue(uncheckIcon, prefs.getClearChecks())
        initValue(notificationIcon, prefs.getBootNotification())
        initTheme(darkModeIcon, prefs.getAppStyle())
        initBackupValue()
    }

    private fun initBackupValue() {
        when {
            isAllBackupEnabled() -> backupIcon.setBackgroundResource(R.drawable.round_green)
            isAllBackupDisabled() -> backupIcon.setBackgroundResource(R.drawable.round_red)
            else -> backupIcon.setBackgroundResource(R.drawable.round_orange)
        }
    }

    private fun isAllBackupEnabled(): Boolean {
        return Prefs.getInstance(context!!).isLocalBackupEnabled()
                && Prefs.getInstance(context!!).getGoogleEmail().matches(".*@.*".toRegex())
    }

    private fun isAllBackupDisabled(): Boolean {
        return !Prefs.getInstance(context!!).isLocalBackupEnabled()
                && !Prefs.getInstance(context!!).getGoogleEmail().matches(".*@.*".toRegex())
    }

    private fun openExtraSettings(title: String, key: String, keyList: String): Boolean {
        val direction = SettingsFragmentDirections.actionSettingsFragmentToSwitchableSettingsFragment(title, key, keyList)
        findNavController().navigate(direction)
        return true
    }

    private fun initValue(imageView: ImageView, value: Int) {
        when (value) {
            Prefs.ENABLED -> imageView.setBackgroundResource(R.drawable.round_green)
            Prefs.DISABLED -> imageView.setBackgroundResource(R.drawable.round_red)
            else -> imageView.setBackgroundResource(R.drawable.round_orange)
        }
    }

    private fun initTheme(imageView: ImageView, value: Int) {
        when (value) {
            Prefs.DARK -> imageView.setBackgroundResource(R.drawable.round_green)
            else -> imageView.setBackgroundResource(R.drawable.round_red)
        }
    }

    private fun changeTheme() {
        val prefs = Prefs.getInstance(context!!)
        val value = prefs.getAppStyle()
        if (value == Prefs.DARK) {
            prefs.setAppStyle(Prefs.LIGHT)
        } else {
            prefs.setAppStyle(Prefs.DARK)
        }
        initTheme(darkModeIcon, prefs.getAppStyle())
        startActivity(Intent(activity!!, SplashScreenActivity::class.java))
        activity?.finishAffinity()
    }

    private fun changeNotification() {
        val prefs = Prefs.getInstance(context!!)
        val value = prefs.getBootNotification()
        if (value == Prefs.DISABLED) {
            prefs.setBootNotification(Prefs.ENABLED)
        } else {
            prefs.setBootNotification(Prefs.DISABLED)
        }
        initValue(notificationIcon, prefs.getBootNotification())
    }

    private fun changeUncheck() {
        val prefs = Prefs.getInstance(context!!)
        val value = prefs.getClearChecks()
        if (value == Prefs.DISABLED) {
            prefs.setClearChecks(Prefs.ENABLED)
        } else {
            prefs.setClearChecks(Prefs.DISABLED)
        }
        initValue(uncheckIcon, prefs.getClearChecks())
    }

    private fun changeFav() {
        val prefs = Prefs.getInstance(context!!)
        val value = prefs.getImportant()
        if (value == Prefs.DISABLED) {
            prefs.setImportant(Prefs.ENABLED)
        } else {
            prefs.setImportant(Prefs.DISABLED)
        }
        initValue(favIcon, prefs.getImportant())
    }

    private fun changeDay() {
        val prefs = Prefs.getInstance(context!!)
        val value = prefs.getClearOnDay()
        if (value == Prefs.DISABLED) {
            prefs.setClearOnDay(Prefs.ENABLED)
        } else {
            prefs.setClearOnDay(Prefs.DISABLED)
        }
        initValue(dayIcon, prefs.getClearOnDay())
    }
}