package org.muren.requests

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.Collections

class RequestItemTouchHelper(val itemsList: List<RequestObject>, val adapter: RequestListAdapter, val mainActivity: MainActivity)
    : ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
    override fun onMove(
        recyclerView: RecyclerView,
        source: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val sourcePosition = source.adapterPosition
        val targetPosition = target.adapterPosition

        Collections.swap(itemsList,sourcePosition,targetPosition)
        adapter.notifyItemMoved(sourcePosition,targetPosition)

        mainActivity.writePreferences();

        return true

    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        TODO("Not yet implemented")
    }
})