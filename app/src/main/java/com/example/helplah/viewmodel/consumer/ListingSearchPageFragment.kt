package com.example.helplah.viewmodel.consumer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.algolia.instantsearch.core.connection.ConnectionHandler
import com.algolia.instantsearch.helper.android.item.StatsTextView
import com.algolia.instantsearch.helper.android.list.autoScrollToStart
import com.algolia.instantsearch.helper.android.searchbox.SearchBoxViewAppCompat
import com.algolia.instantsearch.helper.android.searchbox.connectView
import com.algolia.instantsearch.helper.stats.StatsPresenterImpl
import com.algolia.instantsearch.helper.stats.connectView
import com.example.helplah.R
import com.example.helplah.adapters.SearchListingAdapter
import com.example.helplah.databinding.FragmentSearchPageBinding
import com.example.helplah.models.Listings
import com.google.firebase.firestore.FirebaseFirestore

/**
 * When a user searches for a listing by name, this page shows the results of that search to the user.
 */
class ListingSearchPageFragment : Fragment(), SearchListingAdapter.OnListingClickedListener {

    private val TAG = "Listing search page fragment"

    private var _binding: FragmentSearchPageBinding? = null
    private val binding get() = _binding!!

    private val connection = ConnectionHandler()
    private var search = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        search = requireArguments().getString("search")!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchPageBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = ViewModelProvider(requireActivity())[ListingSearchViewModel::class.java]

        val adapter = SearchListingAdapter(this)
        viewModel.listings.observe(viewLifecycleOwner, Observer { hits -> adapter.submitList(hits) })
        binding.listingsList.let {
            it.itemAnimator = null
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(adapter)
        }

        val searchBoxView = SearchBoxViewAppCompat(binding.searchView)
        connection += viewModel.searchBox.connectView(searchBoxView)
        binding.searchView.setQuery(search, true)

        val statsView = StatsTextView(binding.stats)
        connection += viewModel.stats.connectView(statsView, StatsPresenterImpl())

        binding.backButton.setOnClickListener{requireActivity().onBackPressed()}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        connection.clear()
        _binding = null
    }

    override fun onListingClicked(listing: Listings, view: View) {
        Log.d(TAG, "onListingClicked: listing clicked " + listing.name + " " + listing.listingId)
        val docRef = FirebaseFirestore.getInstance().collection(Listings.DATABASE_COLLECTION)
                .document(listing.listingId)
        docRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    val queryListing = documentSnapshot.toObject(Listings::class.java)
                    val bundle = bundleOf("listing" to queryListing, "id" to listing.listingId)
                    view.findNavController().navigate(
                            R.id.action_searchPageFragment_to_listingDescription, bundle)
                }
    }
}