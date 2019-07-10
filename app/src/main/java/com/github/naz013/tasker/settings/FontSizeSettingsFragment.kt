package com.github.naz013.tasker.settings

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.BaseFragment
import com.github.naz013.tasker.utils.Prefs
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.fragment_font_size.*

class FontSizeSettingsFragment : BaseFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_font_size, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateShowcase(Prefs.getInstance(context!!).getFontSize())

        fab.onClick { findNavController().navigateUp() }
        slider.value = Prefs.getInstance(context!!).getFontSize()
    }

    private fun updateShowcase(points: Int) {
        showCase.setTextSize(TypedValue.COMPLEX_UNIT_SP, points.toFloat())
    }

    override fun onStop() {
        super.onStop()
        Prefs.getInstance(context!!).setFontSize(slider.value)
    }
}