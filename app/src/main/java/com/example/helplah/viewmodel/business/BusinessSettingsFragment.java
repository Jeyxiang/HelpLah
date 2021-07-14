package com.example.helplah.viewmodel.business;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.helplah.R;
import com.example.helplah.models.FCMHandler;
import com.example.helplah.models.Token;
import com.example.helplah.viewmodel.login_screen.LoginScreen;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Shows the setting menu for the current business user.
 */
public class BusinessSettingsFragment extends Fragment {

    private AlertDialog.Builder builder;
    private View dialogView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.settings_fragment, container, false);

        //Initialise
        builder = new AlertDialog.Builder(requireActivity());

        ImageView backButton = rootView.findViewById(R.id.backButton);
        TextView logoutButton = rootView.findViewById(R.id.signoutButton);
        TextView accountButton = rootView.findViewById(R.id.accountSetting);
        TextView contactButton = rootView.findViewById(R.id.contactSetting);
        TextView helpButton = rootView.findViewById(R.id.helpSetting);
        TextView complainButton = rootView.findViewById(R.id.complainSetting);
        TextView passwordButton = rootView.findViewById(R.id.passwordSetting);
        TextView notificationButton = rootView.findViewById(R.id.notificationSetting);
        TextView recoveryButton = rootView.findViewById(R.id.recoverySetting);
        TextView editPictureButton = rootView.findViewById(R.id.editProfilePicture);


        accountButton.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_business_edit_account));

        backButton.setOnClickListener(v -> requireActivity().onBackPressed());

        editPictureButton.setOnClickListener(v -> Navigation.findNavController(v)
                .navigate(R.id.action_businessSettingsFragment_to_businessPictureFragment));

        logoutButton.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), LoginScreen.class);
            startActivity(i);
            String userid = FirebaseAuth.getInstance().getUid();
            FirebaseAuth.getInstance().signOut(); //sign user out
            assert userid != null;
            FirebaseFirestore.getInstance().collection(Token.DATABASE_COLLECTION).document(userid).set(new Token()); //remove token from db
            requireActivity().finish();
            FCMHandler.disableFCM();
        });


        contactButton.setOnClickListener(v -> {
            dialogView = inflater.inflate(R.layout.contact_popup_window,null);
            builder.setView(dialogView).setPositiveButton("OK", (dialog, which) -> dialog.cancel());
            builder.show();
        });

        //helpButton.setOnClickListener(x -> gotoChat()); TODO chat with administrators live

        complainButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_business_write_complaint));

        passwordButton.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_business_edit_password));

        notificationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingsIntent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra(Settings.EXTRA_APP_PACKAGE, requireActivity().getPackageName());
                startActivity(settingsIntent);
            }
        });

        return rootView;
    }
}