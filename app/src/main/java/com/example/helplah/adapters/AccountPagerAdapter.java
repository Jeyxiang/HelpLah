package com.example.helplah.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.helplah.viewmodel.business.AccountNotificationsTab;
import com.example.helplah.viewmodel.business.BusinessAccountReviewsTab;
import com.example.helplah.viewmodel.consumer.UserAccountReviewsTab;

public class AccountPagerAdapter extends FragmentStateAdapter {

    private boolean business;

    public AccountPagerAdapter(@NonNull Fragment fragment, boolean business) {
        super(fragment);
        this.business = business;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            if (this.business) {
                return new BusinessAccountReviewsTab();
            } else {
                return new UserAccountReviewsTab();
            }
        } else {
            return new AccountNotificationsTab();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
