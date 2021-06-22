package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.ViewPager2;

import com.example.helplah.R;
import com.example.helplah.adapters.AccountPagerAdapter;
import com.example.helplah.models.ProfilePictureHandler;
import com.example.helplah.models.User;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {

    public static class UserAccountViewModel extends ViewModel {

        private User user;

        public User getUser() {
            return user;
        }

        public void setUser(User user) {
            this.user = user;
        }
    }

    private View rootView;
    private UserAccountViewModel viewModel;
    private TextView name;
    private CircleImageView profilePicture;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.viewModel = new ViewModelProvider(this).get(UserAccountViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.rootView = inflater.inflate(R.layout.fragment_account, container, false);
        this.name = this.rootView.findViewById(R.id.accountName);
        this.profilePicture = this.rootView.findViewById(R.id.accountProfilePicture);

        getUserFromDataBase();
        configureButtons();
        configureViewPager();
        AppBarLayout appBarLayout = this.rootView.findViewById(R.id.accountAppBar);
        configureAppBarScroll(appBarLayout);

        return this.rootView;
    }

    private void getUserFromDataBase() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ProfilePictureHandler.setProfilePicture(this.profilePicture, id, requireActivity());
        if (this.viewModel.getUser() != null) {
            bindData();
            return;
        }

        DocumentReference doc = FirebaseFirestore.getInstance()
                .collection(User.DATABASE_COLLECTION).document(id);

        doc.get().addOnSuccessListener(documentSnapshot -> {
            User user = documentSnapshot.toObject(User.class);
            viewModel.setUser(user);
            bindData();
        });
    }

    private void bindData() {
        User user = this.viewModel.getUser();
        this.name.setText(user.getUsername());
    }

    private void configureButtons() {

        ImageView settingsButton = this.rootView.findViewById(R.id.accountSettingsButton);

        settingsButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_accountFragment_to_consumerSettingsFragment);
        });
    }

    private void configureViewPager() {
        ViewPager2 viewPager = this.rootView.findViewById(R.id.userAccountViewPager);
        TabLayout tabLayout = this.rootView.findViewById(R.id.userAccountTabLayout);
        AccountPagerAdapter accountPagerAdapter = new AccountPagerAdapter(this, false);
        viewPager.setAdapter(accountPagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(position == 0 ? "My reviews" : "Notifications")
        ).attach();
    }

    private void configureAppBarScroll(AppBarLayout appBarLayout) {

        ConstraintLayout constraintLayout = this.rootView.findViewById(R.id.accountConstraintLayout);

        appBarLayout.addOnOffsetChangedListener((BarLayout, verticalOffset) -> {
            if (verticalOffset > -80) {
                constraintLayout.setVisibility(View.VISIBLE);
            } else {
                constraintLayout.setVisibility(View.INVISIBLE);
            }
        });
    }
}