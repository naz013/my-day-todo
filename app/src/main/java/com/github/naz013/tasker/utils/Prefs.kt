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

        private const val PREFS_NAME = "my_day"
        private const val IMPORTANT_FIRST = "important_first"
        private const val CLEAR_DAY = "clear_day"
        private const val FONT_SIZE = "font_size"
        private const val CREATE_BANNER_SHOWN = "create_banner_shown"

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

    fun setFontSize(value: Int) {
        putInt(FONT_SIZE, value)
    }

    fun getFontSize(): Int = getInt(FONT_SIZE, 14)

    fun setImportantEnabled(value: Boolean) {
        putBoolean(IMPORTANT_FIRST, value)
    }

    fun isImportantEnabled(): Boolean = getBoolean(IMPORTANT_FIRST)

    fun setCreateBannerShown(value: Boolean) {
        putBoolean(CREATE_BANNER_SHOWN, value)
    }

    fun isCreateBannerShown(): Boolean = getBoolean(CREATE_BANNER_SHOWN)

    fun setClearOnDay(value: Boolean) {
        putBoolean(CLEAR_DAY, value)
    }

    fun isClearOnDay(): Boolean = getBoolean(CLEAR_DAY)

    private fun putString(stringToSave: String, value: String) {
        prefs.edit().putString(stringToSave, value).apply()
    }

    private fun putInt(stringToSave: String, value: Int) {
        prefs.edit().putInt(stringToSave, value).apply()
    }

    private fun getInt(stringToLoad: String, def: Int): Int {
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

    private fun getString(stringToLoad: String): String {
        try {
            return prefs.getString(stringToLoad, "")
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
        return ""
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