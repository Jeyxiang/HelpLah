package com.example.helplah.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.helplah.R
import com.example.helplah.models.JobRequests
import com.example.helplah.models.Listings

class SearchRequestsAdapter(private val listener: OnRequestClickedListener) :
        PagedListAdapter<JobRequests, SearchRequestViewHolder>(SearchRequestsAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchRequestViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.job_request_search_list_item,
                parent, false)
        return SearchRequestViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: SearchRequestViewHolder, position: Int) {
        val request = getItem(position)
        if (request != null) {
            holder.bind(request)
        }
    }

    interface OnRequestClickedListener {
        fun onRequestClicked(requestId: String, view: View)
    }

    companion object : DiffUtil.ItemCallback<JobRequests>() {

        override fun areItemsTheSame(oldItem: JobRequests, newItem: JobRequests): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: JobRequests, newItem:JobRequests): Boolean {
            return oldItem == newItem
        }

    }

}