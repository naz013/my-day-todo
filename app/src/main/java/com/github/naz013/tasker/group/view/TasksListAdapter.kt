package com.github.naz013.tasker.group.view

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.github.naz013.tasker.R
import com.github.naz013.tasker.data.Task
import com.github.naz013.tasker.utils.Prefs
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.item_task.view.*

class TasksListAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<TasksListAdapter.Holder>() {

    private val items: MutableList<Task> = mutableListOf()
    var callback: ((List<Task>) -> Unit)? = null
    var deleteCallback: ((Int) -> Unit)? = null

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

    fun setData(data: List<Task>) {
        val diffCallback = TasksDiffCallback(this.items, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.items.clear()
        this.items.addAll(data)
        diffResult.dispatchUpdatesTo(this)
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

    inner class Holder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(task: Task) {
            itemView.summaryView.text = task.summary
            itemView.summaryView.setTextSize(TypedValue.COMPLEX_UNIT_SP, Prefs.getInstance(itemView.context).getFontSize().toFloat())
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
            itemView.deleteView.onClick { deleteCallback?.invoke(adapterPosition) }
        }
    }

    fun delete(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(0, items.size)
        callback?.invoke(items)
    }

    private fun changeDone(position: Int) {
        val item = items[position]
        item.done = !item.done
        items[position] = item
        notifyItemChanged(position)
        callback?.invoke(items)
    }

    private fun changeFav(position: Int) {
        val item = items[position]
        item.important = !item.important
        items[position] = item
        notifyItemChanged(position)
        callback?.invoke(items)
    }

    inner class TasksDiffCallback(private val oldList: List<Task>, private val newList: List<Task>) : DiffUtil.Callback() {
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
}