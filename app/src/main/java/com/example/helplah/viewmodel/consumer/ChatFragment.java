package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.ChatChannelAdapter;
import com.example.helplah.models.ChatDialogue;
import com.example.helplah.models.ChatChannelLiveData;
import com.example.helplah.models.ChatChannelsViewModel;
import com.example.helplah.models.Operation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatFragment extends Fragment {

    private static final String TAG = "Chat fragment";

    private List<ChatDialogue> chatDialogueList;
    private ChatChannelAdapter chatChannelAdapter;
    private ChatChannelsViewModel chatChannelsViewModel;
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
        this.chatChannelsViewModel = new ViewModelProvider(this).get(ChatChannelsViewModel.class);
        this.chatDialogueList = new ArrayList<>();
        Log.d(TAG, "onCreateView: called");
        this.rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        this.rv = this.rootView.findViewById(R.id.chatChannelsRv);
        this.chatChannelAdapter = new ChatChannelAdapter(this.chatDialogueList);
        this.rv.setAdapter(this.chatChannelAdapter);
        getChannels();
        this.rv.setLayoutManager(new LinearLayoutManager(requireActivity()));
        configureScroll();

        return this.rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        this.chatChannelsViewModel.setChatChannels(this.chatDialogueList);
        Log.d(TAG, "onPause: called");
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume: called");
        super.onResume();
    }

    private void getChannels() {
        ChatChannelLiveData liveData = this.chatChannelsViewModel.getChatChannelLiveData();
        if (liveData != null) {
            liveData.observe(getViewLifecycleOwner(), operation -> {
                if (operation.getType() == Operation.ADDED) {
                    ChatDialogue chatDialogue = (ChatDialogue) operation.getObject();
                    addChatChannel(chatDialogue);
                } else if (operation.getType() == Operation.MODIFIED) {
                    ChatDialogue chatDialogue = (ChatDialogue) operation.getObject();
                    modifyChatChanel(chatDialogue);
                } else if (operation.getType() == Operation.REMOVED) {
                    ChatDialogue chatDialogue = (ChatDialogue) operation.getObject();
                    deleteChatChannel(chatDialogue);
                }
                this.chatChannelAdapter.notifyDataSetChanged();
            });
        }
    }

    private void addChatChannel(ChatDialogue chatDialogue) {
        Log.d(TAG, "addChatChannel: array contains "+ this.chatDialogueList.size());
        if(chatDialogue.getTime() > this.date.getTime()) {
            this.chatDialogueList.add(0, chatDialogue);
        } else {
            this.chatDialogueList.add(chatDialogue);
        }
    }

    private void modifyChatChanel(ChatDialogue modifiedChannel) {
        for (int i = 0; i < this.chatDialogueList.size(); i++) {
            ChatDialogue chatDialogue = this.chatDialogueList.get(i);
            if (modifiedChannel.equals(chatDialogue)) {
                this.chatDialogueList.set(i, modifiedChannel);
            }
        }
    }

    private void deleteChatChannel(ChatDialogue removedChannel) {
        this.chatDialogueList.remove(removedChannel);
    }

    private void configureScroll() {
        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int firstVisible = layoutManager.findFirstVisibleItemPosition();
                    int visibleCount = layoutManager.getChildCount();
                    int totalProductCount = layoutManager.getItemCount();

                    if (isScrolling && (firstVisible + visibleCount == totalProductCount)) {
                        isScrolling = false;
                        Log.d(TAG, "onScrolled: getting channels");
                        getChannels();
                    }
                }
            }
        };
        this.rv.addOnScrollListener(onScrollListener);
    }
}