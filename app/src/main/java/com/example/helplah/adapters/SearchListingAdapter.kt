package com.example.helplah.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.example.helplah.R
import com.example.helplah.databinding.SearchListItemBinding
import com.example.helplah.models.Listings

/**
 * A paged list adapter that fills a page with search results of different listings when the user
 * uses the search bar at the home screen to search for a particular listing.
 */
class SearchListingAdapter(private val listener: OnListingClickedListener) :
        PagedListAdapter<Listings, SearchListingViewHolder>(SearchListingAdapter) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchListingViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_list_item, parent, false)
        return SearchListingViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: SearchListingViewHolder, position: Int) {
        val listing = getItem(position)
        if (listing != null) {
            holder.bind(listing)
        }
    }

    interface OnListingClickedListener {
        fun onListingClicked(listing: Listings, view: View)
    }

    companion object : DiffUtil.ItemCallback<Listings>() {

        override fun areItemsTheSame(oldItem: Listings, newItem: Listings): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Listings, newItem: Listings): Boolean {
            return oldItem == newItem
        }

    }

}