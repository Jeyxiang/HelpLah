package com.example.helplah.viewmodel.consumer;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.models.Conversation;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.chats.CometChatConversationList;
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;
import com.cometchat.pro.uikit.ui_resources.utils.item_clickListener.OnItemClickListener;
import com.example.helplah.R;

public class ChatFragment extends Fragment {

    private static final String TAG = "Chat fragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        CometChatConversationList.setItemClickListener(new OnItemClickListener<Conversation>() {
            @Override
            public void OnItemClick(Conversation var, int position) {
                User user = (User) var.getConversationWith();
                Intent intent = new Intent(getActivity(), CometChatMessageListActivity.class);
                intent.putExtra(UIKitConstants.IntentStrings.UID, user.getUid());
                intent.putExtra(UIKitConstants.IntentStrings.NAME, user.getName());
                intent.putExtra(UIKitConstants.IntentStrings.AVATAR, user.getAvatar());
                intent.putExtra(UIKitConstants.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER);
                startActivity(intent);
            }
        });
        return rootView;
    }

}