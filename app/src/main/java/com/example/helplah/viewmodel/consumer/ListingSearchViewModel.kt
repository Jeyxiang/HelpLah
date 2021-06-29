package com.example.helplah.viewmodel.consumer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.list.SearcherSingleIndexDataSource
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxConnectorPagedList
import com.algolia.instantsearch.helper.searcher.SearcherSingleIndex
import com.algolia.instantsearch.helper.stats.StatsConnector
import com.algolia.search.client.ClientSearch
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.example.helplah.models.Constants
import com.example.helplah.models.Listings
import io.ktor.client.features.logging.*
import kotlinx.serialization.json.jsonPrimitive

class ListingSearchViewModel : ViewModel() {

    val client = ClientSearch(ApplicationID(Constants.ALGOLIA_APP_ID),
            APIKey(Constants.ALGOLIA_API_KEY), LogLevel.ALL)
    private val index = client.initIndex(IndexName(Constants.ALGOLIA_LISTINGS_INDEX))
    val searcher = SearcherSingleIndex(index)

    private val dataSourceFactory = SearcherSingleIndexDataSource.Factory(searcher) { hit ->
        Listings(
            hit.json.getValue("name").jsonPrimitive.content,
                hit.json.getValue("listingId").jsonPrimitive.content
        )
    }

    private val pagedListConfig = PagedList.Config.Builder().setPageSize(40).setEnablePlaceholders(true).build()

    val listings: LiveData<PagedList<Listings>> =
            LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()

    val searchBox = SearchBoxConnectorPagedList(searcher, listOf(listings))
    val connection = ConnectionHandler()

    val stats = StatsConnector(searcher)

    init {
        connection += searchBox
        connection += stats
    }


    override fun onCleared() {
        super.onCleared()
        searcher.cancel()
        connection.clear()
    }
}