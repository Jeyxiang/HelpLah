package com.example.helplah.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.helplah.R;

import java.util.ArrayList;
import java.util.List;

public class CheckboxAdapter extends RecyclerView.Adapter<CheckboxAdapter.MyViewHolder> {

        String services[];
        ArrayList<String> arrayList;
        Context context;

        public CheckboxAdapter(Context ct, String s1[]) {
            services = s1;
            context = ct;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.checkbox_reg,parent,false);
            return new CheckboxAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            holder.chckboxes.setText(services[position]);
            holder.chckboxes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.chckboxes.isChecked()) {
                        arrayList.add(services[position]);
                    } else {
                        arrayList.remove(services[position]);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return services.length;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            CheckBox chckboxes;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                chckboxes = itemView.findViewById(R.id.checkBox);
                arrayList = new ArrayList<String>();
            }
        }

        public List<String> listofSelected() {
            return arrayList;
        }
    }

