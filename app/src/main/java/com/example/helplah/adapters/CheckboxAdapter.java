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

        String data1[];
        ArrayList<String> arrayLst;
        Context context;

        public CheckboxAdapter(Context ct,String s1[]) {
            data1 = s1;
            context = ct;
        }

        @NonNull
        @Override
        public CheckboxAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.checkbox_reg,parent,false);
            return new CheckboxAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CheckboxAdapter.MyViewHolder holder, int position) {
            holder.chckboxes.setText(data1[position]);
            holder.chckboxes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.chckboxes.isChecked()) {
                        arrayLst.add(data1[position]);
                    } else {
                        arrayLst.remove(data1[position]);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return data1.length;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            CheckBox chckboxes;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                chckboxes = itemView.findViewById(R.id.checkBox);
                arrayLst = new ArrayList<String>();
            }
        }

        public List<String> listofSelected() {
            return arrayLst;
        }
    }

