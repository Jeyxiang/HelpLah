package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.ChatChannelAdapter;
import com.example.helplah.models.ChatChannel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatFragment extends Fragment {

    private static final String TAG = "Chat fragment";

    private List<ChatChannel> chatChannelList;
    private ChatChannelAdapter chatChannelAdapter;
    private boolean isScrolling;
    private Date date;

    private View rootView;
    private RecyclerView rv;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.date = new Date();
        this.chatChannelList = new ArrayList<>();
        Log.d(TAG, "onCreateView: called");
        this.rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        this.rv = this.rootView.findViewById(R.id.chatChannelsRv);
        this.rv.setAdapter(this.chatChannelAdapter);

        return this.rootView;
    }
}