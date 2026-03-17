package org.muren.requests

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView


class RequestListAdapter (private val itemsList: List<RequestObject>, private val mainActivity: MainActivity)
    : RecyclerView.Adapter<RequestListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val linearLayout : LinearLayout = itemView.findViewById(R.id.request_item_linear_layout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.request_item_linear_layout, parent, false)
        return ViewHolder(itemView);
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val requestObject = itemsList[position]
        val requestTextView = RequestTextView(holder.linearLayout.context, mainActivity, requestObject)
        holder.linearLayout.addView(requestTextView)
    }

    override fun getItemCount(): Int {
        return itemsList.size
    }
}