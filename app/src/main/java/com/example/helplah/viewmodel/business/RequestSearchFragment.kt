package com.example.helplah.viewmodel.business

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
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
import com.example.helplah.adapters.SearchRequestsAdapter
import com.example.helplah.databinding.FragmentSearchPageBinding
import com.example.helplah.models.Listings

/**
 * A simple [Fragment] subclass.
 * Use the [RequestSearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RequestSearchFragment : Fragment(), SearchRequestsAdapter.OnRequestClickedListener {

    private val TAG = "Request search page fragment"

    private var _binding: FragmentSearchPageBinding? = null
    private val binding get() = _binding!!

    private val connection = ConnectionHandler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        val viewModel = ViewModelProvider(requireActivity())[RequestSearchViewModel::class.java]

        val adapter = SearchRequestsAdapter(this)
        viewModel.jobRequests.observe(viewLifecycleOwner, Observer { hits -> adapter.submitList(hits) })
        binding.listingsList.let {
            it.itemAnimator = null
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(requireContext())
            it.autoScrollToStart(adapter)
        }

        val searchBoxView = SearchBoxViewAppCompat(binding.searchView)
        connection += viewModel.searchBox.connectView(searchBoxView)

        val statsView = StatsTextView(binding.stats)
        connection += viewModel.stats.connectView(statsView, StatsPresenterImpl())

        binding.backButton.setOnClickListener{requireActivity().onBackPressed()}
        binding.searchLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.almostBlack))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        connection.clear()
        _binding = null
    }

    override fun onRequestClicked(requestId: String, view: View) {
        Log.d(TAG, "onRequestClicked: Clicked on request with id: $requestId")
        val bundle = bundleOf("id" to requestId)
        view.findNavController().navigate(
                R.id.action_requestSearchFragment_to_businessJobRequestNotification, bundle)
    }
}