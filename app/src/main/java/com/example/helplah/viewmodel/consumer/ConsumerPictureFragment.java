package com.example.helplah.viewmodel.consumer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.helplah.R;
import com.example.helplah.models.ProfilePictureHandler;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class ConsumerPictureFragment extends Fragment {
    private static File tempFile; //to store the camera picture temporarily
    private boolean isCamera = false;
    private Uri fileUri;
    private Bitmap imageBitmap = null;
    private ImageView image;
    private ProgressBar progressBar;
    private ImageView backButton;
    private Button uploadButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_profile_picture,container,false);
        image = v.findViewById(R.id.imageUpload);
        progressBar = v.findViewById(R.id.progressBar);
        backButton = v.findViewById(R.id.backPictureButton);
        uploadButton = v.findViewById(R.id.uploadButton);
        uploadButton.setEnabled(false);

        try {
            tempFile = File.createTempFile("profile","jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ProfilePictureHandler.setProfilePicture(image, id, requireActivity());
        image.setOnClickListener(x -> selectImage());

        backButton.setOnClickListener(x -> getActivity().onBackPressed());

        uploadButton.setOnClickListener(x -> uploadImage());
        return v;
    }

    public static Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;
        List<Intent> intentList = new ArrayList<>();
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT,tempFile);
        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    context.getString(R.string.pick_image_intent_text));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }
        return chooserIntent;
    }


    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    public void selectImage() {
        Intent intent = getPickImageIntent(requireActivity().getApplicationContext());
        mGetContent.launch(intent);
    }


    ActivityResultLauncher<Intent> mGetContent = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent i = result.getData();
                        //if a camera is used, this will be null
                        isCamera = (i.getData() == null) ;
                        fileUri = i.getData();
                        Bundle extras = i.getExtras();
                        imageBitmap = (Bitmap) extras.get("data");
                        if (isCamera) {
                            image.setImageBitmap(imageBitmap);
                        } else {
                            image.setImageURI(fileUri);
                        }
                        uploadButton.setEnabled(true);
                    }
                }
            });

    //Convert from a bitmap to a uri
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public void uploadImage() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference uploadImageRef = storageRef.child("profilepic/"+ uid + "/profile.jpg");
        progressBar.setVisibility(View.VISIBLE);

        if (isCamera) {
            fileUri = getImageUri(requireContext(), imageBitmap); //convert bitmap to uri first
        }
        try {
            //compressing the image
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), fileUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            byte[] data = baos.toByteArray();

            //uploading the image
            UploadTask uploadTask = uploadImageRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getActivity(),"Profile picture updated successfully",Toast.LENGTH_LONG).show();
                    requireActivity().onBackPressed();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), "Failed to Upload Image", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressBar.setProgress((int) progress);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
