package com.example.planandmeet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class EventParticipantsAdapter extends RecyclerView.Adapter<EventParticipantsAdapter.MyViewHoooolder> {

    Context context;
    ArrayList<User> usersList;
    ArrayList<String> usersIdList;

    String currentUserID, eventId;

    public EventParticipantsAdapter(Context context, ArrayList<User> usersList, ArrayList<String> usersIdList) {
        this.context = context;
        this.usersList = usersList;
        this.usersIdList = usersIdList;
    }

    @NotNull
    @Override
    public MyViewHoooolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.participants_item, parent, false);
        return new MyViewHoooolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHoooolder holder, @SuppressLint("RecyclerView") int position) {

        User user = usersList.get(position);

        holder.nameTextView.setText(user.getName());
        holder.emailTextView.setText(user.getEmail());

        //getting event id
        SharedPreferences sharedPreferences = context.getSharedPreferences("eventId", Context.MODE_PRIVATE);
        eventId = sharedPreferences.getString("eventID", "");

        //getting current User id
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (currentUserID.equals(usersIdList.get(0)) && position != 0) {
            holder.removeParticipant.setVisibility(View.VISIBLE);
        } else {
            holder.removeParticipant.setVisibility(View.GONE);
        }

        holder.removeParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.nameTextView.getContext());
                builder.setTitle("Are You Sure?");

                builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        usersList.remove(position);
                        usersIdList.remove(position);

                        Map<String, Object> map = new HashMap<>();
                        map.put("userID", usersIdList);

                        FirebaseDatabase.getInstance().getReference().child("events")
                                .child(eventId).updateChildren(map);

                        Intent intent = new Intent(context.getApplicationContext(), EventParticipants.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(holder.nameTextView.getContext(), "Removal Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class MyViewHoooolder extends RecyclerView.ViewHolder {

        TextView nameTextView, emailTextView;
        Button removeParticipant;

        public MyViewHoooolder(@NonNull @NotNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.participantName);
            emailTextView = itemView.findViewById(R.id.participantEmail);
            removeParticipant = itemView.findViewById(R.id.removeParticipantBtn);
        }
    }
}