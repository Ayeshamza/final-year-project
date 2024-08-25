package com.example.planandmeet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;


public class UpComingMeetingsAdapter extends RecyclerView.Adapter<UpComingMeetingsAdapter.MyViewHolder> {

    ArrayList<Event> eventsList;
    Context context;

    public UpComingMeetingsAdapter(Context context, ArrayList<Event> eventsList) {
        this.context = context;
        this.eventsList = eventsList;
    }

    @NonNull
    @NotNull
    @Override
    public UpComingMeetingsAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.upcoming_meeting_item, parent, false);
        return new UpComingMeetingsAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UpComingMeetingsAdapter.MyViewHolder holder, int position) {

        Event event = eventsList.get(position);

        holder.eventsName.setText(event.getName());
        holder.eventsMeetTime.setText(event.getMeetingOccurence());
        holder.eventsMode.setText(event.getMode());
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView eventsName, eventsMeetTime, eventsMode;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            eventsName = itemView.findViewById(R.id.eventsNames);
            eventsMeetTime = itemView.findViewById(R.id.eventsOccurenceDates);
            eventsMode = itemView.findViewById(R.id.eventsModes);
        }
    }
}