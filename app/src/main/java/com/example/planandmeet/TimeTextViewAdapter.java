package com.example.planandmeet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class TimeTextViewAdapter extends RecyclerView.Adapter<com.example.planandmeet.TimeTextViewAdapter.MyViewHolder> {

    private static String[] time;
    Context context;

    public TimeTextViewAdapter(Context context, String[] time) {
        this.context = context;
        TimeTextViewAdapter.time = time;
    }

    public TimeTextViewAdapter() {

    }

    @NonNull
    @NotNull
    @Override
    public com.example.planandmeet.TimeTextViewAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View textBoxView = LayoutInflater.from(context).inflate(R.layout.time_textview_item, parent, false);
        return new com.example.planandmeet.TimeTextViewAdapter.MyViewHolder(textBoxView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull com.example.planandmeet.TimeTextViewAdapter.MyViewHolder holder, int position) {
        holder.textView.setText(time[position]);
    }

    public String getTimeAtPosition(int x) {
        return time[x];
    }

    @Override
    public int getItemCount() {
        return time.length;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.timeSlotTextview);
        }
    }
}