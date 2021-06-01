package com.example.helplah.viewmodel.consumer;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.helplah.R;
import com.example.helplah.models.AvailabilityStatus;
import com.example.helplah.models.Listings;
import com.example.helplah.models.ListingsQuery;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

public class ListingsDialogFragment extends DialogFragment implements View.OnClickListener {

    public static final String TAG = "ListingsFilterDialog";

    public interface FilterListener {

        void onFilter(Query query);

    }

    public static class FilterViewModel extends ViewModel {

        private int spinnerPosition = 0;
        private boolean fourHours = false;
        private boolean oneDay = false;
        private boolean twoDays = false;
        private boolean threeDays = false;
        private boolean oneWeek = false;

        public void setSpinnerPosition(int spinnerPosition) {
            this.spinnerPosition = spinnerPosition;
        }

        public int getSpinnerPosition() {
            return this.spinnerPosition;
        }
    }

    private FilterListener filterListener;
    private View rootView;
    private FilterViewModel mViewModel;

    private Spinner sortSpinner;
    private RadioGroup languageRadioGroup;
    private CheckBox available4Hours;
    private CheckBox available1Day;
    private CheckBox available2Days;
    private CheckBox available3Days;
    private CheckBox available1Week;

    public ListingsDialogFragment(Fragment fragment) {
        if (fragment instanceof FilterListener) {
            this.filterListener = (FilterListener) fragment;
        }
        this.mViewModel = new ViewModelProvider(fragment).get(FilterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.rootView = inflater.inflate(R.layout.listings_filter_dialog, container, false);

        this.sortSpinner = this.rootView.findViewById(R.id.spinner_sort_listings);
        this.languageRadioGroup = this.rootView.findViewById(R.id.languageRadioGroup);
        this.available4Hours = this.rootView.findViewById(R.id.availability_4_hours);
        this.available1Day = this.rootView.findViewById(R.id.availability_1_day);
        this.available2Days = this.rootView.findViewById(R.id.availability_2_days);
        this.available3Days = this.rootView.findViewById(R.id.availability_3_days);
        this.available1Week = this.rootView.findViewById(R.id.availability_1_week);

        restorePastSettings();

        this.rootView.findViewById(R.id.button_cancel).setOnClickListener(this);
        this.rootView.findViewById(R.id.button_apply).setOnClickListener(this);

        Objects.requireNonNull(getDialog()).setCanceledOnTouchOutside(true);

        return this.rootView;
    }

    private void restorePastSettings() {
        this.sortSpinner.setSelection(this.mViewModel.getSpinnerPosition());
        this.available4Hours.setChecked(this.mViewModel.fourHours);
        this.available1Day.setChecked(this.mViewModel.oneDay);
        this.available2Days.setChecked(this.mViewModel.twoDays);
        this.available3Days.setChecked(this.mViewModel.threeDays);
        this.available1Week.setChecked(this.mViewModel.oneWeek);
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_apply:
                this.onApplyClicked();
                break;
            case R.id.button_cancel:
                this.onCancelClicked();
                break;
        }
    }

    public void onApplyClicked() {
        if (this.rootView != null) {
            Query query = getFilters().createQuery();
            if (this.filterListener != null) {
                this.filterListener.onFilter(query);
            }
        }
        this.dismiss();
    }

    public void onCancelClicked() {
        this.dismiss();
    }

    private void getSortBy(ListingsQuery query) {
        String selected = (String) this.sortSpinner.getSelectedItem();
        if (getString(R.string.sort_by_price_ascending).equals(selected)) {
            query.setSortBy(Listings.FIELD_PRICE, true);
        } else if (getString(R.string.sort_by_price_descending).equals(selected)) {
            query.setSortBy(Listings.FIELD_PRICE, false);
        } else if (getString(R.string.sort_by_score).equals(selected)) {
            query.setSortBy(Listings.FIELD_REVIEW_SCORE, false);
        } else if (getString(R.string.sort_by_number_of_reviews).equals(selected)) {
            query.setSortBy(Listings.FIELD_NUMBER_OF_REVIEWS, false);
        }
        Log.d(TAG, "getSortBy: Selected position: " + this.sortSpinner.getSelectedItemPosition());
        this.mViewModel.setSpinnerPosition(this.sortSpinner.getSelectedItemPosition());
    }

    private int[] getAvailability() {
        int[] optionsSelected = new int[6];
        int i = 0;

        if (this.available4Hours.isChecked()) {
            optionsSelected[i] = AvailabilityStatus.fourHours;
            i++;
        }
        if (this.available1Day.isChecked()) {
            optionsSelected[i] = AvailabilityStatus.oneDay;
            i++;
        }
        if (this.available2Days.isChecked()) {
            optionsSelected[i] = AvailabilityStatus.twoDays;
            i++;
        }
        if (this.available3Days.isChecked()) {
            optionsSelected[i] = AvailabilityStatus.threeDays;
            i++;
        }
        if (this.available1Week.isChecked()) {
            optionsSelected[i] = AvailabilityStatus.oneWeek;
            i++;
        }

        this.mViewModel.fourHours = this.available4Hours.isChecked();
        this.mViewModel.oneDay = this.available1Day.isChecked();
        this.mViewModel.twoDays = this.available2Days.isChecked();
        this.mViewModel.threeDays = this.available3Days.isChecked();
        this.mViewModel.oneWeek = this.available1Week.isChecked();

        return optionsSelected;
    }

    private String getLanguage() {
        int selectedLanguage = this.languageRadioGroup.getCheckedRadioButtonId();
        if (selectedLanguage != -1) {
            RadioButton radioButton = this.rootView.findViewById(selectedLanguage);
            return radioButton.getText().toString();
        }
        else {
            // No selection
            return null;
        }
    }

    public ListingsQuery getFilters() {
        ListingsQuery query = new ListingsQuery(FirebaseFirestore.getInstance());

        if (this.rootView != null) {
            getSortBy(query);
            query.setAvailability(this.getAvailability());
            query.setPreferredLanguage(getLanguage());
        }
        return query;
    }
}