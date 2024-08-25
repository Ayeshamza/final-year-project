package com.example.planandmeet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;


public class DayTextViewAdapter extends RecyclerView.Adapter<com.example.planandmeet.DayTextViewAdapter.MyViewHolder> {

    Context context;
    String[] days;

    public DayTextViewAdapter(Context context, String[] days) {
        this.context = context;
        this.days = days;
    }

    @NonNull
    @NotNull
    @Override
    public com.example.planandmeet.DayTextViewAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View textBoxView = LayoutInflater.from(context).inflate(R.layout.day_textview_item, parent, false);
        return new com.example.planandmeet.DayTextViewAdapter.MyViewHolder(textBoxView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull com.example.planandmeet.DayTextViewAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(days[position]);
    }

    @Override
    public int getItemCount() {
        return days.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.dayTextview);
        }
    }
}