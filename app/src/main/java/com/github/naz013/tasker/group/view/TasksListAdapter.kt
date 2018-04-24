package com.github.naz013.tasker.group.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.naz013.tasker.R
import com.github.naz013.tasker.data.Task
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.item_task.view.*


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
class TasksListAdapter : RecyclerView.Adapter<TasksListAdapter.Holder>() {

    private val items: MutableList<Task> = mutableListOf()
    var callback: ((List<Task>) -> Unit)? = null

    fun setData(data: List<Task>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(task: Task) {
            itemView.summaryView.text = task.summary
            if (task.done) {
                itemView.statusView.setImageResource(R.drawable.ic_status_check)
            } else {
                itemView.statusView.setImageResource(R.drawable.ic_status_non_check)
            }
            if (task.important) {
                itemView.favouriteView.setImageResource(R.drawable.ic_favourite_on)
            } else {
                itemView.favouriteView.setImageResource(R.drawable.ic_favourite_off)
            }
            itemView.deleteView.visibility = View.VISIBLE
        }

        init {
            itemView.statusView.onClick { changeDone(adapterPosition) }
            itemView.favouriteView.onClick { changeFav(adapterPosition) }
            itemView.deleteView.onClick { delete(adapterPosition) }
        }
    }

    private fun delete(position: Int) {
        items.removeAt(position)
        callback?.invoke(items)
    }

    private fun changeDone(position: Int) {
        val item = items[position]
        item.done = !item.done
        items[position] = item
        callback?.invoke(items)
    }

    private fun changeFav(position: Int) {
        val item = items[position]
        item.important = !item.important
        items[position] = item
        callback?.invoke(items)
    }
}