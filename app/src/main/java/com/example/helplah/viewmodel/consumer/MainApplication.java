package com.example.helplah.viewmodel.consumer;

import android.app.Application;
import android.util.Log;

import com.cometchat.pro.core.AppSettings;
import com.cometchat.pro.core.CometChat;
import com.cometchat.pro.exceptions.CometChatException;
import com.cometchat.pro.uikit.ui_settings.UIKitSettings;

public class MainApplication extends Application {

    private static final String TAG = "Main Application";

    String appId = "189332e5f5e42a30";
    String region = "us";

    @Override
    public void onCreate() {
        super.onCreate();

        AppSettings appSettings = new AppSettings.AppSettingsBuilder()
                .subscribePresenceForAllUsers().setRegion(region).build();

        CometChat.init(this, appId, appSettings, new CometChat.CallbackListener<String>() {
            @Override
            public void onSuccess(String s) {
                Log.d(TAG, "onSuccess: Initialisation completed");
            }

            @Override
            public void onError(CometChatException e) {
                Log.d(TAG, "onError: Initialization failed " + e.getMessage());
            }
        });

        UIKitSettings.calls(false);
        UIKitSettings.userAudioCall(false);
        UIKitSettings.userVideoCall(false);
        UIKitSettings.blockUser(false);
        UIKitSettings.threadedChats(false);
    }
}
