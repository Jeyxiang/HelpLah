package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.adapters.ChatDialogueDataSource;
import com.example.helplah.models.ChatDialogue;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;

import java.util.ArrayList;
import java.util.Date;

public class ChatFragment extends Fragment implements ChatDialogueDataSource.DataSourceListener {

    private static final String TAG = "Chat fragment";

    private Date date = new Date();

    private DialogsListAdapter<ChatDialogue> adapter;
    ChatDialogueDataSource source;

    private ArrayList<ChatDialogue> chatDialogues = new ArrayList<>();

    public static class ChatDialogViewModel extends ViewModel {
        private boolean initialised;
    }

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
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        DialogsList dialogsList = rootView.findViewById(R.id.chatDialogsList);
        this.source = new ChatDialogueDataSource(this);
        this.adapter = new DialogsListAdapter<>((imageView, url, payload) -> {
            imageView.setImageResource(R.drawable.plumbing_logos);
        });
        this.adapter.setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);
        dialogsList.setAdapter(adapter);
        this.adapter.setItems(this.chatDialogues);
        return rootView;
    }


    @Override
    public void chatDialogueAdded(ChatDialogue chatDialogue) {
        if (chatDialogue.getTime() > this.date.getTime()) {
            this.adapter.addItem(0, chatDialogue);
        } else {
            this.adapter.addItem(chatDialogue);
        }
        Log.d(TAG, "chatDialogueAdded: adapter has " + adapter.getItemCount() + "items");
    }

    @Override
    public void chatDialogueModified(ChatDialogue chatDialogue) {
    }

    @Override
    public void chatDialogueRemoved(ChatDialogue chatDialogue) {

    }

    @Override
    public void onResume() {
        super.onResume();
        this.source.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.source.stopListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: number of items in adapter " + adapter.getItemCount());
        this.adapter.setItems(new ArrayList<>());
    }
}