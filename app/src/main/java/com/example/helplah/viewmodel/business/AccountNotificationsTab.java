package com.example.helplah.viewmodel.business;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.NotificationTabAdapter;
import com.example.helplah.models.Notification;
import com.example.helplah.models.User;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class AccountNotificationsTab extends Fragment
        implements NotificationTabAdapter.NotificationClickedListener{

    private boolean isBusiness;
    private RecyclerView recyclerView;
    private View rootView;
    private PagedList.Config rvConfig;
    private FirestorePagingOptions<Notification> options;

    public AccountNotificationsTab() {}

    public static AccountNotificationsTab newInstance(boolean isBusiness) {

        Bundle args = new Bundle();
        args.putBoolean("isBusiness", isBusiness);
        AccountNotificationsTab fragment = new AccountNotificationsTab();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.isBusiness = getArguments().getBoolean("isBusiness");
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query query = FirebaseFirestore.getInstance()
                .collection(User.DATABASE_COLLECTION)
                .document(id)
                .collection(Notification.DATABASE_COLLECTION)
                .orderBy(Notification.FIELD_DATE, Query.Direction.DESCENDING);

        configureFirestore(query);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.business_account_notifications_tab, container, false);
        this.recyclerView = this.rootView.findViewById(R.id.accountNotificationsRv);
        getQuery();

        return this.rootView;
    }

    private void getQuery() {
        NotificationTabAdapter adapter = new NotificationTabAdapter(this.options, this);
        adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        this.recyclerView.setAdapter(adapter);

        DividerItemDecoration divider = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        this.recyclerView.addItemDecoration(divider);
    }

    private void configureFirestore(Query query) {
        this.rvConfig = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(20)
                .setPageSize(10)
                .setPrefetchDistance(10)
                .build();

        this.options = new FirestorePagingOptions.Builder<Notification>()
                .setLifecycleOwner(this)
                .setQuery(query, this.rvConfig, Notification.class)
                .build();
    }

    @Override
    public void notificationClicked(Notification notification, View v) {
        Notification.notificationClicked(notification, v, isBusiness, requireActivity());
    }
}