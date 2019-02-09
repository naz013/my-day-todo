package com.github.naz013.tasker.arch

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import com.github.naz013.tasker.R
import com.github.naz013.tasker.utils.Prefs

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
abstract class BaseFragment : Fragment() {

    @StyleRes
    fun dialogStyle(): Int {
        return if (Prefs.getInstance(context!!).isDarkMode()) {
            R.style.Dark_Dialog
        } else {
            R.style.Light_Dialog
        }
    }

    fun isHorizontal(): Boolean {
        return context?.resources?.configuration?.orientation ?: 0 == Configuration.ORIENTATION_LANDSCAPE
    }

    fun isChromeOs(): Boolean {
        return context?.packageManager?.hasSystemFeature("org.chromium.arc.device_management") ?: false
    }

    fun isTablet(): Boolean = resources.getBoolean(R.bool.is_tablet)

    fun hideKeyboard(view: View? = null) {
        val token = view?.windowToken ?: activity?.window?.currentFocus?.windowToken ?: return
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(token, 0)
    }
}