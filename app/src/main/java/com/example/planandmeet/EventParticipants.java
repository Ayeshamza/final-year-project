package com.example.planandmeet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventParticipants extends AppCompatActivity {

    RecyclerView participantRecyclerView;
    EventParticipantsAdapter eventParticipantsAdapter;

    String eventId;
    ArrayList<String> userIdList;
    ArrayList<User> userInfoList;

    Event event;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_participants);

        ImageButton back = findViewById(R.id.backToMain);
        TextView title = findViewById(R.id.title);
        title.setText(R.string.eventParticipantsTitle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventParticipants.this, EventDetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //getting event id
        SharedPreferences sharedPreferences = getSharedPreferences("eventId", MODE_PRIVATE);
        eventId = sharedPreferences.getString("eventID", "");

        event = new Event();
        user = new User();
        userIdList = new ArrayList<>();
        userInfoList = new ArrayList<>();

        ArrayList<String> tempIDs = new ArrayList<>();
        ArrayList<User> tempData = new ArrayList<>();

        participantRecyclerView = findViewById(R.id.participantsList);
        participantRecyclerView.setHasFixedSize(true);
        participantRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventParticipantsAdapter = new EventParticipantsAdapter(this, userInfoList, userIdList);
        participantRecyclerView.setAdapter(eventParticipantsAdapter);

        //getting userIds of event
        FirebaseDatabase.getInstance().getReference().child("events").child(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userIdList.clear();
                        if (snapshot.hasChild("userID")) {
                            event = snapshot.getValue(Event.class);
                            userIdList.addAll(event.getUserID());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        //getting users data
        FirebaseDatabase.getInstance().getReference().child("users")
                .addValueEventListener(new ValueEventListener() {

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userInfoList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {
                                String userKey = dataSnapshot.getKey();

                                for (int a = 0; a < userIdList.size(); a++) {
                                    if (userKey.equals(userIdList.get(a))) {
                                        user = dataSnapshot.getValue(User.class);

                                        if (userKey.equals(userIdList.get(0))) {
                                            userInfoList.add(0, user);
                                        } else {
                                            tempIDs.add(userKey);
                                            tempData.add(user);
                                        }
                                    }
                                }
                            }
                        }

                        if (tempIDs.size() > 0) {
                            for (int b = 1; b < userIdList.size(); b++) {
                                for (int c = 0; c < tempIDs.size(); c++) {
                                    if (userIdList.get(b).equals(tempIDs.get(c))) {
                                        userInfoList.add(tempData.get(c));
                                    }
                                }
                            }
                        }
                        eventParticipantsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EventParticipants.this, EventDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}