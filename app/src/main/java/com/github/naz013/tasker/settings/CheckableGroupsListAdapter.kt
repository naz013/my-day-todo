package com.github.naz013.tasker.settings

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.naz013.tasker.R
import com.github.naz013.tasker.data.TaskGroup
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.item_group_check.view.*


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
class CheckableGroupsListAdapter : RecyclerView.Adapter<CheckableGroupsListAdapter.Holder>() {

    private val items: MutableList<TaskGroup> = mutableListOf()
    var ids: Set<String> = setOf()
    var isClickable = true

    fun setData(data: List<TaskGroup>) {
        items.clear()
        data.forEach { it.active = ids.contains(it.id.toString()) }
        items.addAll(data)
        notifyDataSetChanged()
    }

    fun getCheckedIds(): Set<String> {
        return items.filter { it.active }.map { it.id.toString() }.toSet()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_group_check, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(task: TaskGroup) {
            itemView.summaryView.text = task.name
            if (task.active) {
                itemView.statusView.setImageResource(R.drawable.ic_status_check)
            } else {
                itemView.statusView.setImageResource(R.drawable.ic_status_non_check)
            }
        }

        init {
            itemView.statusView.onClick { updateState(adapterPosition) }
        }
    }

    private fun updateState(position: Int) {
        if (!isClickable) return
        items[position].active = !items[position].active
        notifyItemChanged(position)
    }
}