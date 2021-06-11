package com.example.helplah.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;

import java.util.ArrayList;

public class DescriptionCategoryAdapter extends
        RecyclerView.Adapter<DescriptionCategoryAdapter.ViewHolder> {

    private static final String TAG = "Description Category Adapter";

    private ArrayList<String> services;
    private Context context;
    private CategoriesAdapter.onCategorySelected listener;

    public DescriptionCategoryAdapter(Context context, ArrayList<String> services, Fragment fragment) {
        this.context = context;
        this.services = services;
        this.listener = (CategoriesAdapter.onCategorySelected) fragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ViewHolder(inflater.inflate(R.layout.categories_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return this.services.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.category = itemView.findViewById(R.id.rvDescriptionCategory);
        }

        public void bind(int position) {
            itemView.setOnClickListener(v -> listener.onCategoryClicked(services.get(position), v));
            this.category.setText(services.get(position));
        }
    }
}
