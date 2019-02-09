package com.github.naz013.tasker.home

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.github.naz013.tasker.R
import com.github.naz013.tasker.data.TaskGroup
import com.github.naz013.tasker.settings.groups.GroupsDiffCallback
import com.github.naz013.tasker.utils.Prefs
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.item_group.view.*
import kotlinx.android.synthetic.main.item_task.view.*
import java.lang.Exception


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
class GroupsListAdapter : RecyclerView.Adapter<GroupsListAdapter.Holder>() {

    companion object {
        const val ADD = 0
        const val OPEN = 1
    }

    private val items: MutableList<TaskGroup> = mutableListOf()
    var callback: ((TaskGroup, Int) -> Unit)? = null

    init {
        registerAdapterDataObserver(object : androidx.recyclerview.widget.RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                if (itemCount > positionStart + 1) {
                    notifyItemChanged(positionStart + 1, true)
                }
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
                if (itemCount > positionStart) {
                    notifyItemChanged(positionStart, true)
                }
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
                notifyItemChanged(fromPosition)
                notifyItemChanged(toPosition)
            }
        })
    }

    fun setData(data: List<TaskGroup>) {
        val diffCallback = GroupsDiffCallback(this.items, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.items.clear()
        this.items.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_group, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    inner class Holder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(taskGroup: TaskGroup) {
            Log.d("GroupsListAdapter", "bind: $taskGroup")
            try {
                val drawable = itemView.groupTitleView.background as GradientDrawable
                drawable.setColor(Color.parseColor(taskGroup.color))
            } catch (e: Exception) {
            }

            itemView.groupTitleView.text = taskGroup.name

            loadItems(itemView.tasksList, taskGroup)
        }

        init {
            itemView.addButton.onClick { callback?.invoke(items[adapterPosition], ADD) }
            itemView.cardView.onClick { callback?.invoke(items[adapterPosition], OPEN) }
        }

        private fun loadItems(container: LinearLayout, taskGroup: TaskGroup) {
            container.isFocusableInTouchMode = false
            container.isFocusable = false
            container.removeAllViewsInLayout()
            val prefs = Prefs.getInstance(container.context)
            var items = taskGroup.tasks
            val important = prefs.getImportant()
            val importantIds = prefs.getStringList(Prefs.IMPORTANT_FIRST_IDS)
            if (important == Prefs.ENABLED || (important == Prefs.CUSTOM && importantIds.contains(taskGroup.id.toString()))) {
                items = items.sortedByDescending { it.important }.toMutableList()
            }
            items.sortedByDescending { it.dt }.sortedBy { it.done }.forEach {
                val binding = LayoutInflater.from(container.context).inflate(R.layout.item_task, container, false)
                val checkView = binding.statusView
                val textView = binding.summaryView
                val favView = binding.favouriteView
                if (it.done) {
                    checkView.setImageResource(R.drawable.ic_status_check)
                } else {
                    checkView.setImageResource(R.drawable.ic_status_non_check)
                }
                checkView.visibility = View.VISIBLE
                favView.visibility = if (it.important) View.VISIBLE else View.INVISIBLE
                textView.text = it.summary
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, prefs.getFontSize().toFloat())
                container.addView(binding)
            }
        }
    }
}