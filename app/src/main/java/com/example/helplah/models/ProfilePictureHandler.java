package com.example.helplah.models;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.example.helplah.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfilePictureHandler {

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

    public static Bitmap compressBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        return null;
    }
}
