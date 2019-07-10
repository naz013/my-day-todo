package com.github.naz013.tasker.utils

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.github.naz013.tasker.R
import com.mcxiaoke.koi.ext.onClick

class GroupColorsController {

    companion object {
        val COLORS = arrayOf(
                "#FF5252", "#FF4081", "#E040FB", "#7C4DFF",
                "#536DFE", "#448AFF", "#40C4FF", "#18FFFF",
                "#64FFDA", "#69F0AE", "#B2FF59", "#EEFF41",
                "#FFFF00", "#FFD740", "#FFAB40", "#FF6E40"
        )
    }

    private val items: MutableList<Item> = mutableListOf()
    private var mSelected = 0

    fun getSelectedColor(): String {
        return items[mSelected].color
    }

    fun selectColor(color: String) {
        val index = COLORS.indexOf(color)
        if (index != -1) updateViews(index)
    }

    fun fillSlider(container: LinearLayout) {
        container.isFocusableInTouchMode = false
        container.isFocusable = false
        container.removeAllViewsInLayout()
        items.clear()
        for (i in 0 until COLORS.size) {
            val imageView = LayoutInflater.from(container.context).inflate(R.layout.item_color_circle, container, false) as ImageView

            val drawable = imageView.background as GradientDrawable
            drawable.setColor(Color.parseColor(COLORS[i]))

            imageView.onClick { updateViews(i) }
            container.addView(imageView)
            items.add(Item(i, COLORS[i], imageView))
        }
        mSelected = 0
        items[0].imageView.setImageResource(R.drawable.ic_check_white)
    }

    private fun updateViews(i: Int) {
        if (mSelected != i) {
            items[mSelected].imageView.setImageResource(0)
            mSelected = i
            items[i].imageView.setImageResource(R.drawable.ic_check_white)
        }
    }

    data class Item(var position: Int, var color: String, var imageView: ImageView)
}