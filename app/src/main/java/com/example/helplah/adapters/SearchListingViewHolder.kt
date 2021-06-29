package com.example.helplah.adapters

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.helplah.databinding.SearchListItemBinding
import com.example.helplah.models.Listings

class SearchListingViewHolder(private val view : View,
                              private val listener: SearchListingAdapter.OnListingClickedListener)
    : RecyclerView.ViewHolder(view), View.OnClickListener {

    private lateinit var binding: SearchListItemBinding
    private var viewListing: Listings? = null

    fun bind(listing: Listings) {
        binding = SearchListItemBinding.bind(view)
        binding.listingName.text = listing.name;
        viewListing = listing
    }

    init {
        view.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        listener.onListingClicked(viewListing!!, view)
    }
}