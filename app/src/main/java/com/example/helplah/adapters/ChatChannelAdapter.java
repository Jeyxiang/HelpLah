package com.example.helplah.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;
import com.example.helplah.models.ChatDialogue;

import java.util.List;

public class ChatChannelAdapter extends RecyclerView.Adapter<ChatChannelAdapter.ChatViewHolder> {

    private List<ChatDialogue> chatDialogueList;

    public ChatChannelAdapter(List<ChatDialogue> chatDialogueList) {
        this.chatDialogueList = chatDialogueList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.chat_channel_list_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatDialogue chatDialogue = this.chatDialogueList.get(position);
        holder.bindChatChannel(chatDialogue);
    }

    @Override
    public int getItemCount() {
        return this.chatDialogueList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView name;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            this.name = itemView.findViewById(R.id.chatChannelUsername);
        }

        public void bindChatChannel(ChatDialogue chatDialogue) {
            this.name.setText(chatDialogue.getBusinessName());
        }
    }
}