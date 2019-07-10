package com.github.naz013.tasker.settings.groups

import androidx.recyclerview.widget.DiffUtil
import com.github.naz013.tasker.data.TaskGroup

class GroupsDiffCallback(private val oldList: List<TaskGroup>, private val newList: List<TaskGroup>) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val p1 = oldList[oldItemPosition]
        val p2 = newList[newItemPosition]
        return p1 == p2
    }
}