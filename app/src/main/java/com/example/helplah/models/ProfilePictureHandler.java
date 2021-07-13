package com.example.helplah.models;

import android.content.Context;
import android.widget.ImageView;

import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.example.helplah.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfilePictureHandler {

    /**
     * Takes an image view and set the image of the imageview as the profile picture of the user.
     * The profile picture is stored on firebase storage and is obtained from the user id.
     * @param imageView The imageView to set the profile picture.
     * @param id The id of the user.
     * @param context The context of the imageView.
     */
    public static void setProfilePicture(ImageView imageView, String id, Context context) {
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profilepic/" + id + "/profile.jpg");
        reference.getDownloadUrl()
                .addOnSuccessListener(uri -> Glide.with(context).load(uri).centerCrop().into(imageView))
                .addOnFailureListener(e ->
                        imageView.setImageDrawable(ResourcesCompat
                                .getDrawable(context.getResources(),
                                        R.drawable.blank_profile_picture, null)));
    }

    /**
     * Updates the Comet Chat API account profile picture of a user.
     * @param id The firebase authentication user id of the user.
     */
    public static void updateCometPicture(String id) {
        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child("profilepic/" + id + "/profile.jpg");
        reference.getDownloadUrl()
                .addOnSuccessListener(uri -> ChatFunction.updateChatProfilePicture(uri.toString()));
    }
}
