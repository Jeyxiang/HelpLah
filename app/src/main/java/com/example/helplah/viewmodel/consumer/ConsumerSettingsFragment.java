package com.example.helplah.viewmodel.consumer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.helplah.R;
import com.example.helplah.viewmodel.login_screen.LoginScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.StorageReference;


public class ConsumerSettingsFragment extends Fragment {

    private AlertDialog.Builder builder;
    private StorageReference mStorageReference;
    private View dialogView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.settings_fragment, container, false);

        //Initialise
        builder = new AlertDialog.Builder(getActivity());

        ImageView backButton = v.findViewById(R.id.backButton);
        TextView logoutButton = v.findViewById(R.id.signoutButton);
        TextView profileButton = v.findViewById(R.id.editProfilePicture);
        TextView accountButton = v.findViewById(R.id.accountSetting);
        TextView contactButton = v.findViewById(R.id.contactSetting);
        TextView helpButton = v.findViewById(R.id.helpSetting);
        TextView complainButton = v.findViewById(R.id.complainSetting);
        TextView passwordButton = v.findViewById(R.id.passwordSetting);
        TextView notificationButton = v.findViewById(R.id.notificationSetting);
        TextView recoveryButton = v.findViewById(R.id.recoverySetting);

        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_consumerSettingsFragment_to_editConAccountFragment);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();;
            }
        });

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), LoginScreen.class);
                startActivity(i);
                FirebaseAuth.getInstance().signOut(); //sign user out
                requireActivity().finish();
            }
        });


        contactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogView = inflater.inflate(R.layout.contact_popup_window,null);
                builder.setView(dialogView).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_consumerSettingsFragment_to_consumerPictureFragment);
            }
        });

        //helpButton.setOnClickListener(x -> gotoChat()); TODO chat with administrators live

        complainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_consumerSettingsFragment_to_writeComplainFragment2);
            }
        });

        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_consumerSettingsFragment_to_editConPasswordFragment);
            }
        });

        return v;
    }

}
