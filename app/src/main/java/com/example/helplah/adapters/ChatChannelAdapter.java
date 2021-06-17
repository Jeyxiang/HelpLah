package com.example.helplah.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ActionMode;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.models.ChatChannel;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatChannelAdapter extends FirestoreRecyclerAdapter<ChatChannel, ChatChannelAdapter.ChatViewHolder> {

    public interface ChatClickListener {

        void onChatChannelClicked();
    }

    private final ChatClickListener mListener;
    private final RecyclerView rv;
    private boolean multiSelect = false;
    private int numberOfSelected = 0;
    private ArrayList<String> selectedItems = new ArrayList<>();
    private ActionMode actionMode;
    private Context context;

    public ChatChannelAdapter(@NonNull FirestoreRecyclerOptions<ChatChannel> options,
                              ChatClickListener listener, RecyclerView rv) {
        super(options);
        this.mListener = listener;
        this.rv = rv;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        View view = LayoutInflater.from(this.context).
                inflate(R.layout.chat_channel_list_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatViewHolder holder, int position, @NonNull ChatChannel model) {
        holder.bindChatChannel(getItem(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private final TextView chatName;
        private final TextView lastMessage;
        private final TextView lastMessageDate;
        private final FrameLayout unreadCountLayout;
        private final TextView unreadCount;
        private final CircleImageView chatPicture;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            this.chatName = itemView.findViewById(R.id.chatChannelUsername);
            this.lastMessage = itemView.findViewById(R.id.chatChannelLastMessage);
            this.lastMessageDate = itemView.findViewById(R.id.chatChannelTime);
            this.unreadCountLayout = itemView.findViewById(R.id.chatChannelUnreadCountLayout);
            this.unreadCount = itemView.findViewById(R.id.chatChannelUnreadCount);
            this.chatPicture = itemView.findViewById(R.id.chatChannelProfilePicture);
        }

        @SuppressLint("SetTextI18n")
        public void bindChatChannel(ChatChannel chatChannel) {
            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (id.equals(chatChannel.getCustomerId())) {
                this.chatName.setText(chatChannel.getBusinessName());
            } else {
                this.chatName.setText(chatChannel.getCustomerName());
            }

            this.lastMessage.setText(chatChannel.getLastMessage());
            this.lastMessageDate.setText(getTimeAgo(chatChannel.getTime()));
            if (chatChannel.getUnreadMessageCount() > 0) {
                this.unreadCountLayout.setVisibility(View.VISIBLE);
                this.unreadCount.setText(chatChannel.getUnreadMessageCount() + "");
            }
        }

        private String getTimeAgo(long time) {
            return "";
        }
    }
}