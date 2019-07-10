package com.github.naz013.tasker.arch

import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.StyleRes
import androidx.fragment.app.Fragment
import com.github.naz013.tasker.R
import com.github.naz013.tasker.utils.Prefs

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