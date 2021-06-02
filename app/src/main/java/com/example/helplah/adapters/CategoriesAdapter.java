package com.example.helplah.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.MyViewHolder> {

    public interface onCategorySelected {

        void onCategoryClicked(String category, View v);

    }

    private onCategorySelected mListener;

    String[] categories;
    int[] categoriesImages;
    Context context;

    public CategoriesAdapter(Context context, String[] categories, int[] img, Fragment fragment) {
        this.categories = categories;
        this.mListener = (onCategorySelected) fragment;
        this.context = context;
        this.categoriesImages = img;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(this.context);
        View view = inflater.inflate(R.layout.categories_list_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String category = this.categories[position];
        holder.serView.setText(category);
        holder.myImage.setImageResource(this.categoriesImages[position]);
        holder.bindListener(this.mListener, category);
    }

    @Override
    public int getItemCount() {
        return this.categories.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView serView;
        ImageView myImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.serView = itemView.findViewById(R.id.categoryListServiceView);
            this.myImage = itemView.findViewById(R.id.categoryListImageView);
        }

        public void bindListener(final onCategorySelected listener, String category) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onCategoryClicked(category, v);
                    }
                }
            });
        }
    }
}
