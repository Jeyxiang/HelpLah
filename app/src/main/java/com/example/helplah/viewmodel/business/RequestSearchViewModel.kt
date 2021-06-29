package com.example.helplah.viewmodel.business

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
import com.example.helplah.models.JobRequests
import io.ktor.client.features.logging.*
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long

class RequestSearchViewModel : ViewModel() {

    private val TAG = "Request search view model"
    val client = ClientSearch(ApplicationID(Constants.ALGOLIA_APP_ID),
            APIKey(Constants.ALGOLIA_API_KEY), LogLevel.ALL)
    private val index = client.initIndex(IndexName(Constants.ALGOLIA_JOB_REQUESTS_INDEX))
    val searcher = SearcherSingleIndex(index)

    private val dataSourceFactory = SearcherSingleIndexDataSource.Factory(searcher) { hit ->
        Log.d(TAG, "Results :$hit")
        JobRequests(
                hit.json.getValue("businessName").jsonPrimitive.content,
                hit.json.getValue("customerName").jsonPrimitive.content,
                hit.json.getValue("dateOfJob").jsonPrimitive.long,
                hit.json.getValue("id").jsonPrimitive.content
        )
    }

    private val pagedListConfig = PagedList.Config.Builder().setPageSize(40).setEnablePlaceholders(true).build()

    val jobRequests: LiveData<PagedList<JobRequests>> =
            LivePagedListBuilder(dataSourceFactory, pagedListConfig).build()

    val searchBox = SearchBoxConnectorPagedList(searcher, listOf(jobRequests))
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