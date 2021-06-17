package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.helplah.R;
import com.example.helplah.models.ChatDialogue;
import com.example.helplah.models.ChatMessage;
import com.example.helplah.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

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
    private MessageInput messageInput;
    private MessagesList messageList;
    private ListenerRegistration registration;
    private MessagesListAdapter<ChatMessage> adapter;

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
        this.messageInput = rootView.findViewById(R.id.chatMessageInput);
        this.messageList = rootView.findViewById(R.id.chatMessageList);
        this.senderId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        setSenderName();
        this.adapter = new MessagesListAdapter<>(senderId, (imageView, url, payload) -> {});
        this.messageList.setAdapter(this.adapter);
        createDatasource();
        configureInput();
        return rootView;
    }

    private void configureInput() {
        this.messageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                if (senderName != null) {
                    ChatMessage message = new ChatMessage(senderId, senderName, input.toString());
                    Log.d(TAG, "onSubmit: " + message.getText());
                    adapter.addToStart(message, false);
                }
                return true;
            }
        });
    }

    private void setSenderName() {
        FirebaseFirestore.getInstance().collection(User.DATABASE_COLLECTION).document(this.senderId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    User user= documentSnapshot.toObject(User.class);
                    assert user != null;
                    senderName = user.getUsername();
                });
    }

    private void createDatasource() {
        DocumentReference chatChannel = FirebaseFirestore.getInstance()
                .collection(ChatDialogue.DATABASE_CHANNELS_COLLECTION).document(this.chatId);
        Query query = chatChannel.collection(ChatDialogue.DATABASE_MESSAGE_COLLECTION)
                .orderBy(ChatMessage.FIELD_DATE, Query.Direction.DESCENDING);
        this.registration = query.addSnapshotListener((value, error) -> {
            if (error != null) {
                return;
            }
            assert value != null;
            for (DocumentChange change : value.getDocumentChanges()) {
                ChatMessage message = change.getDocument().toObject(ChatMessage.class);
                if (change.getType() == DocumentChange.Type.ADDED) {
                    addMessage(message);
                } else if (change.getType() == DocumentChange.Type.MODIFIED) {
                    modifyMessage(message);
                } else if (change.getType() == DocumentChange.Type.REMOVED) {
                   deleteMessage(message);
                }
            }
        });
    }

    public void addMessage(ChatMessage message) {

    }

    public void modifyMessage(ChatMessage message) {

    }

    public void deleteMessage(ChatMessage message) {

    }
}