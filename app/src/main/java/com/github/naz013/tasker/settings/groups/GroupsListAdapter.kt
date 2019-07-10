package com.github.naz013.tasker.settings.groups

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.github.naz013.tasker.R
import com.github.naz013.tasker.arch.ItemTouchHelperAdapter
import com.github.naz013.tasker.arch.OnStartDragListener
import com.github.naz013.tasker.data.TaskGroup
import com.mcxiaoke.koi.ext.onClick
import kotlinx.android.synthetic.main.item_group_edit.view.*
import java.util.*

class GroupsListAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<GroupsListAdapter.Holder>(), ItemTouchHelperAdapter {

    companion object {
        const val DELETE = 0
        const val EDIT = 1
    }

    val items: MutableList<TaskGroup> = mutableListOf()
    var callback: ((TaskGroup, Int) -> Unit)? = null
    var mDragStartListener: OnStartDragListener? = null
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

    fun setData(data: List<TaskGroup>) {
        val diffCallback = GroupsDiffCallback(this.items, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.items.clear()
        this.items.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        Collections.swap(items, fromPosition, toPosition)
        notifyItemMoved(fromPosition, toPosition)
        return true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.item_group_edit, parent, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(items[position])
    }

    inner class Holder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {

        fun bind(task: TaskGroup) {
            itemView.summaryView.text = task.name
        }

        init {
            itemView.onClick { edit(adapterPosition) }
            itemView.deleteView.onClick { deleteCallback?.invoke(adapterPosition) }
            itemView.handleView.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    mDragStartListener?.onStartDrag(this)
                }
                false
            }
        }
    }

    private fun edit(position: Int) {
        callback?.invoke(items[position], EDIT)
    }

    fun delete(position: Int) {
        callback?.invoke(items[position], DELETE)
    }
}