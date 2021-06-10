package com.example.helplah.viewmodel.business;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BusinessSettingsFragment} factory method to
 * create an instance of this fragment.
 */
public class BusinessSettingsFragment extends Fragment {

    private AlertDialog.Builder builder;
    private View dialogView;
    private ImageView backButton;
    private TextView logoutButton;
    private TextView accountButton;
    private TextView contactButton;
    private TextView helpButton;
    private TextView complainButton;
    private TextView passwordButton;
    private TextView notificationButton;
    private TextView recoveryButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.business_settings_fragment, container, false);

        //Initialise
        builder = new AlertDialog.Builder(getActivity());

        backButton = v.findViewById(R.id.backButton);
        logoutButton = v.findViewById(R.id.signoutButton);
        accountButton = v.findViewById(R.id.accountSetting);
        contactButton = v.findViewById(R.id.contactSetting);
        helpButton = v.findViewById(R.id.helpSetting);
        complainButton = v.findViewById(R.id.complainSetting);
        passwordButton = v.findViewById(R.id.passwordSetting);
        notificationButton = v.findViewById(R.id.notificationSetting);
        recoveryButton = v.findViewById(R.id.recoverySetting);


        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_business_edit_account);
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

        //helpButton.setOnClickListener(x -> gotoChat()); TODO chat with administrators live

        complainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_business_write_complaint);
            }
        });

        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).navigate(R.id.action_business_edit_password);
            }
        });

        return v;
    }
}