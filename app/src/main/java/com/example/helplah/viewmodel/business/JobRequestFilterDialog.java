package com.example.helplah.viewmodel.business;

import android.os.Bundle;
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
import com.example.helplah.models.JobRequestQuery;
import com.example.helplah.models.JobRequests;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JobRequestFilterDialog} factory method to
 * create an instance of this fragment.
 */
public class JobRequestFilterDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = "Job request filter dialog";

    private static final long milliseconds = 86400000;


    public interface RequestFilterListener {

        void onFilter(Query query);
    }

    public static class RequestFilterViewModel extends ViewModel {

        private int spinnerPosition;
        private int radioGroupPosition;
        private boolean statusConfirmed = true;
        private boolean statusPending = true;
        private boolean statusCancelled = true;

        public int getSpinnerPosition() {
            return spinnerPosition;
        }

        public void setSpinnerPosition(int spinnerPosition) {
            this.spinnerPosition = spinnerPosition;
        }

        public int getRadioGroupPosition() {
            return radioGroupPosition;
        }

        public void setRadioGroupPosition(int radioGroupPosition) {
            this.radioGroupPosition = radioGroupPosition;
        }
    }

    private RequestFilterListener mListener;
    private View rootView;
    private RequestFilterViewModel mViewModel;

    private Spinner sortSpinner;
    private RadioGroup dateRadioGroup;
    private CheckBox checkConfirmed;
    private CheckBox checkPending;
    private CheckBox checkCancelled;

    public JobRequestFilterDialog(Fragment fragment) {
        if (fragment instanceof RequestFilterListener) {
            this.mListener = (RequestFilterListener) fragment;
        }
        this.mViewModel = new ViewModelProvider(fragment).get(RequestFilterViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.rootView = inflater.inflate(R.layout.job_request_filter_dialog, container, false);

        this.sortSpinner = this.rootView.findViewById(R.id.sort_date_of_jobs);
        this.dateRadioGroup = this.rootView.findViewById(R.id.dateRadioGroup);
        this.checkConfirmed = this.rootView.findViewById(R.id.status_confirmed);
        this.checkPending = this.rootView.findViewById(R.id.status_pending);
        this.checkCancelled = this.rootView.findViewById(R.id.status_cancelled);

        restorePastSettings();

        this.rootView.findViewById(R.id.button_cancel).setOnClickListener(this);
        this.rootView.findViewById(R.id.button_apply).setOnClickListener(this);
        Objects.requireNonNull(getDialog()).setCanceledOnTouchOutside(true);

        return this.rootView;
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
        if (v.getId() == R.id.button_apply) {
            this.onApplyClicked();
        } else if (v.getId() == R.id.button_cancel) {
            this.dismiss();
        }
    }

    private void restorePastSettings() {
        this.sortSpinner.setSelection(this.mViewModel.getSpinnerPosition());
        this.checkConfirmed.setChecked(this.mViewModel.statusConfirmed);
        this.checkPending.setChecked(this.mViewModel.statusPending);
        this.checkCancelled.setChecked(this.mViewModel.statusCancelled);
    }

    private void onApplyClicked() {
        if (this.rootView != null) {
            Query query = getFilters().createQuery();
            if (this.mListener != null) {
                this.mListener.onFilter(query);
            }
        }
        this.dismiss();
    }

    public JobRequestQuery getFilters() {
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        JobRequestQuery query = new JobRequestQuery(FirebaseFirestore.getInstance(), id, true);

        this.getSortBy(query);
        query.setStatus(this.getStatus());
        this.getDateFilter(query);

        return query;
    }

    private void getSortBy(JobRequestQuery query) {
        String selected = (String) this.sortSpinner.getSelectedItem();

        if (getString(R.string.sort_descending).equals(selected)) {
            query.setSortBy(JobRequests.FIELD_DATE_OF_JOB, false);
        } else if (getString(R.string.sort_ascending).equals(selected)) {
            query.setSortBy(JobRequests.FIELD_DATE_OF_JOB, true);
        }
        this.mViewModel.setSpinnerPosition(this.sortSpinner.getSelectedItemPosition());

    }

    private ArrayList<Integer> getStatus() {
        ArrayList<Integer> optionsSelected = new ArrayList<>();

        if (this.checkConfirmed.isChecked()) {
            optionsSelected.add(JobRequests.STATUS_CONFIRMED);
        }
        if (this.checkPending.isChecked()) {
            optionsSelected.add(JobRequests.STATUS_PENDING);
        }
        if (this.checkCancelled.isChecked()) {
            optionsSelected.add(JobRequests.STATUS_CANCELLED);
        }

        this.mViewModel.statusConfirmed = this.checkConfirmed.isChecked();
        this.mViewModel.statusPending = this.checkPending.isChecked();
        this.mViewModel.statusCancelled = this.checkCancelled.isChecked();

        return optionsSelected;
    }

    private void getDateFilter(JobRequestQuery query) {
        int selectedDateFilter = this.dateRadioGroup.getCheckedRadioButtonId();
        if (selectedDateFilter == -1) {
            // No selection
            return;
        }
        RadioButton radioButton = this.rootView.findViewById(selectedDateFilter);
        String selected = radioButton.getText().toString();
        long current_milliseconds = System.currentTimeMillis();
        Date now = new Date();

        if (selected.equals(getString(R.string.date_today))) {
            query.setDateFilter(now, new Date(current_milliseconds + milliseconds));

        } else if (selected.equals(getString(R.string.date_next_3_days))) {
            query.setDateFilter(now, new Date(current_milliseconds + 3 * milliseconds));
        } else if (selected.equals(getString(R.string.date_next_week))) {
            query.setDateFilter(now, new Date(current_milliseconds + 7 * milliseconds));
        } else if (selected.equals(getString(R.string.date_past_week))) {
            query.setDateFilter(new Date(current_milliseconds + 7 * milliseconds), now);
        }
    }
}