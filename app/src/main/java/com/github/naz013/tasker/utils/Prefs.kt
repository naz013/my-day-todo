package com.github.naz013.tasker.utils

import android.content.Context
import java.lang.IllegalStateException

/**
 * Copyright 2017 Nazar Suhovich
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
open class Prefs private constructor(context: Context) {

    companion object {

        const val DISABLED = 0
        const val ENABLED = 1
        const val CUSTOM = 2

        const val LIGHT = 0
        const val DARK = 1

        private const val PREFS_NAME = "my_day"
        const val IMPORTANT_FIRST = "important_first"
        const val IMPORTANT_FIRST_IDS = "important_first_ids"
        const val CLEAR_GROUP = "clear_day"
        const val CLEAR_GROUP_IDS = "clear_day_ids"
        const val CLEAR_CHECKS = "clear_checks"
        const val CLEAR_CHECKS_IDS = "clear_checks_ids"
        const val BOOT_IDS = "boot_ids"
        private const val FONT_SIZE = "font_size"
        private const val APP_STYLE = "app_style"
        private const val CREATE_BANNER_SHOWN = "create_banner_shown"
        private const val GROUP_BANNER_SHOWN = "group_banner_shown"
        private const val LAST_LAUNCH = "last_launch"
        private const val FIRST_INSERT_GROUPS = "first_insert_groups"
        private const val LOCAL_BACKUP = "local_backup"
        private const val GOOGLE_EMAIL = "google_email"
        const val BOOT_NOTIFICATION = "boot_notification"

        private var instance: Prefs? = null

        fun getInstance(context: Context): Prefs {
            if (instance == null) {
                instance = Prefs(context.applicationContext)
            }
            if (instance == null) {
                throw IllegalStateException()
            }
            return instance!!
        }

        fun destroyInstance() {
            instance = null
        }
    }

    private var prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isDarkMode(): Boolean {
        return getAppStyle() == DARK
    }

    fun setAppStyle(value: Int) {
        putInt(APP_STYLE, value)
    }

    fun getAppStyle(): Int = getInt(APP_STYLE, LIGHT)

    fun setBootNotification(value: Int) {
        putInt(BOOT_NOTIFICATION, value)
    }

    fun getBootNotification(): Int = getInt(BOOT_NOTIFICATION, DISABLED)

    fun setLocalBackupEnabled(value: Boolean) {
        putBoolean(LOCAL_BACKUP, value)
    }

    fun isLocalBackupEnabled(): Boolean = getBoolean(LOCAL_BACKUP)

    fun setGoogleEmail(value: String) {
        putString(GOOGLE_EMAIL, value)
    }

    fun getGoogleEmail(): String {
        return getString(GOOGLE_EMAIL, "")
    }

    fun setClearChecks(value: Int) {
        putInt(CLEAR_CHECKS, value)
    }

    fun getClearChecks(): Int = getInt(CLEAR_CHECKS, DISABLED)

    fun setLastLaunch(time: String) {
        putString(LAST_LAUNCH, time)
    }

    fun getLastLaunch(): String {
        return getString(LAST_LAUNCH, TimeUtils.getGmtStamp())
    }

    fun setFontSize(value: Int) {
        putInt(FONT_SIZE, value)
    }

    fun getFontSize(): Int = getInt(FONT_SIZE, 14)

    fun setImportant(value: Int) {
        putInt(IMPORTANT_FIRST, value)
    }

    fun getImportant(): Int = getInt(IMPORTANT_FIRST, DISABLED)

    fun setCreateBannerShown(value: Boolean) {
        putBoolean(CREATE_BANNER_SHOWN, value)
    }

    fun isCreateBannerShown(): Boolean = getBoolean(CREATE_BANNER_SHOWN)

    fun setFirstAdded(value: Boolean) {
        putBoolean(FIRST_INSERT_GROUPS, value)
    }

    fun isFirstAdded(): Boolean = getBoolean(FIRST_INSERT_GROUPS)

    fun setGroupBannerShown(value: Boolean) {
        putBoolean(GROUP_BANNER_SHOWN, value)
    }

    fun isGroupBannerShown(): Boolean = getBoolean(GROUP_BANNER_SHOWN)

    fun setClearOnDay(value: Int) {
        putInt(CLEAR_GROUP, value)
    }

    fun getClearOnDay(): Int = getInt(CLEAR_GROUP, DISABLED)

    fun getStringList(key: String): Set<String> {
        return prefs.getStringSet(key, setOf())
    }

    fun putStringList(key: String, set: Set<String>) {
        prefs.edit().putStringSet(key, set).apply()
    }

    private fun putString(stringToSave: String, value: String) {
        prefs.edit().putString(stringToSave, value).apply()
    }

    fun putInt(stringToSave: String, value: Int) {
        prefs.edit().putInt(stringToSave, value).apply()
    }

    fun getInt(stringToLoad: String, def: Int): Int {
        return try {
            prefs.getInt(stringToLoad, def)
        } catch (e: ClassCastException) {
            try {
                Integer.parseInt(prefs.getString(stringToLoad, "$def"))
            } catch (e1: ClassCastException) {
                0
            }
        }
    }

    private fun putLong(stringToSave: String, value: Long) {
        prefs.edit().putLong(stringToSave, value).apply()
    }

    private fun getLong(stringToLoad: String): Long {
        return try {
            prefs.getLong(stringToLoad, 1000)
        } catch (e: ClassCastException) {
            java.lang.Long.parseLong(prefs.getString(stringToLoad, "1000"))
        }
    }

    private fun getString(stringToLoad: String, def: String = ""): String {
        try {
            return prefs.getString(stringToLoad, def)
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return def
    }

    private fun hasKey(checkString: String): Boolean = prefs.contains(checkString)

    private fun putBoolean(stringToSave: String, value: Boolean) {
        prefs.edit().putBoolean(stringToSave, value).apply()
    }

    private fun getBoolean(stringToLoad: String): Boolean {
        return try {
            prefs.getBoolean(stringToLoad, false)
        } catch (e: ClassCastException) {
            java.lang.Boolean.parseBoolean(prefs.getString(stringToLoad, "false"))
        }
    }
}