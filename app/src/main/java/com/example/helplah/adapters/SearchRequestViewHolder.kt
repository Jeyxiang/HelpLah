package com.example.helplah.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.helplah.adapters.SearchRequestsAdapter.OnRequestClickedListener
import com.example.helplah.databinding.JobRequestSearchListItemBinding
import com.example.helplah.databinding.SearchListItemBinding
import com.example.helplah.models.JobRequests
import com.example.helplah.models.Listings

class SearchRequestViewHolder(private val itemView: View,
                              private val listener: OnRequestClickedListener)
    : RecyclerView.ViewHolder(itemView), View.OnClickListener {

    private lateinit var binding: JobRequestSearchListItemBinding
    private var id = ""

    fun bind(request: JobRequests) {
        binding = JobRequestSearchListItemBinding.bind(itemView)
        binding.requestName.text = request.customerName;
        binding.requestDate.text = JobRequests.dateToString(request.dateOfJob)
        id = request.id
    }

    init {
        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            listener.onRequestClicked(id, v)
        }
    }
}