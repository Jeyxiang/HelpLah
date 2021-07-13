package com.example.helplah.models;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cometchat.pro.constants.CometChatConstants;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.models.User;
import com.cometchat.pro.uikit.ui_components.messages.message_list.CometChatMessageListActivity;
import com.cometchat.pro.uikit.ui_resources.constants.UIKitConstants;

/**
 * A class that contains static functions that are used to connect to the CometChat API.
 *
 * In the Comet Chat API, the id of a user is the same as the user's firebase authentication id.
 */
public class ChatFunction {

    private static final String TAG = "Chat message";

    /**
     * Creates a new chat between the current user of the app and another recipient.
     * @param receiverId The firestore/CometChat id of the recipient.
     * @param receiverName The name of the recipient.
     * @param context The context from which this function is called.
     */
    public static void createChat(String receiverId, String receiverName, Context context) {
        com.cometchat.pro.models.User receiver = new com.cometchat.pro.models.User();
        receiver.setUid(receiverId);
        receiver.setName(receiverName);
        CometChat.getUser(receiverId, new CometChat.CallbackListener<com.cometchat.pro.models.User>() {
            @Override
            public void onSuccess(com.cometchat.pro.models.User user) {
                Log.d(TAG, "onSuccess: Found" + user.getName());
                Intent intent = new Intent(context, CometChatMessageListActivity.class);
                intent.putExtra(UIKitConstants.IntentStrings.UID, user.getUid());
                intent.putExtra(UIKitConstants.IntentStrings.AVATAR, user.getAvatar());
                intent.putExtra(UIKitConstants.IntentStrings.STATUS, user.getStatus());
                intent.putExtra(UIKitConstants.IntentStrings.NAME, user.getName());
                intent.putExtra(UIKitConstants.IntentStrings.LINK,user.getLink());
                intent.putExtra(UIKitConstants.IntentStrings.TYPE, CometChatConstants.RECEIVER_TYPE_USER);
                context.startActivity(intent);
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: Failed. This user may be a mock data and has no account to chat with. " + e.getMessage());
            }
        });
    }

    /**
     * Updates the CometChat username of a user. It is called when the user changes his username in
     * the app's firestore database. This will sync the changes between the 2 backends.
     * @param userId The firestore/CometChat id of the user whose name is to be changed.
     * @param newName The new name of the user.
     */
    public static void updateChatUsername(String userId, String newName) {
        User user = CometChat.getLoggedInUser();
        user.setName(newName);
        if (user.getUid().equals(userId.toLowerCase())) {
            CometChat.updateCurrentUserDetails(user, new CometChat.CallbackListener<User>() {
                @Override
                public void onSuccess(User user) {
                    Log.d(TAG, "onSuccess: Chat current user updated");
                }

                @Override
                public void onError(CometChatException e) {
                    Log.d(TAG, "onSuccess: Chat current user failed to update");
                }
            });
        }
    }

    /**
     * Updates the CometChat profile picture of the current user.
     * @param profilePicture The download url of the profile picture that is stored in the
     *                       firebase storage.
     */
    public static void updateChatProfilePicture(String profilePicture) {
        User user = CometChat.getLoggedInUser();
        user.setAvatar(profilePicture);
        CometChat.updateCurrentUserDetails(user, new CometChat.CallbackListener<User>() {
            @Override
            public void onSuccess(User user) {
                Log.d(TAG, "onSuccess: Chat current user updated");
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onSuccess: Chat current user failed to update");
            }
        });
    }
}
