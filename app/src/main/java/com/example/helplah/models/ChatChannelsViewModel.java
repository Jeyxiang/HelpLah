package com.example.helplah.models;

import androidx.lifecycle.ViewModel;

import com.example.helplah.adapters.FirestoreChatChannelRepository;

import java.util.List;

public class ChatChannelsViewModel extends ViewModel {

    private final ChatChannelRepository chatChannelRepository = new FirestoreChatChannelRepository();
    private List<ChatDialogue> chatDialogues;

    public List<ChatDialogue> getChatChannels() {
        return chatDialogues;
    }

    public void setChatChannels(List<ChatDialogue> chatDialogues) {
        this.chatDialogues = chatDialogues;
    }

    public ChatChannelLiveData getChatChannelLiveData() {
        return chatChannelRepository.getChatChannelLiveDate();
    }

    public interface ChatChannelRepository {
        ChatChannelLiveData getChatChannelLiveDate();
    }
}
