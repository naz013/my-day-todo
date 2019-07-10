package com.github.naz013.tasker.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.naz013.tasker.R
import com.github.naz013.tasker.data.TaskGroup
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.item_group_check.view.*

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