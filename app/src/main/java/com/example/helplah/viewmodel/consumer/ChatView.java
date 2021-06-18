package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.helplah.R;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatView} factory method to
 * create an instance of this fragment.
 * ACCEPTS A CHAT_CHANNEL OBJECT
 */
public class ChatView extends Fragment {

    private static final String TAG = "Chat view";

    private String chatId;
    private String senderId;
    private String senderName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.chatId = getArguments().getString("id");
        View rootView = inflater.inflate(R.layout.fragment_chat_view, container, false);
        this.senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        return rootView;
    }


}