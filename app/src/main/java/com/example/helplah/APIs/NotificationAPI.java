package com.example.helplah.APIs;

import com.example.helplah.models.MyResponse;
import com.example.helplah.models.NotificationData;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface NotificationAPI {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAOLDRTlo:APA91bH5MiQl1NM84r87WgXrxGBL6EFjWjhnJs9LUCreq-byUs92hdpeP4DPA9Dre_OokEHR5pIWjLNngy9XPOjKBIHoiWeFb2b4C28hP0HvNeXAOmMjwiRC2TH4f652ZRxsJxRTfRc9"
                    // server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationData body);
}
