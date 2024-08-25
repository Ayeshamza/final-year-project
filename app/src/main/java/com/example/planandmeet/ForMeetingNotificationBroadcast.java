package com.example.planandmeet;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ForMeetingNotificationBroadcast extends BroadcastReceiver {

    Activity activity;
    Event event;
    User user;
    ArrayList<String> eventUserIds;
    String eventId, eventName, msg, notificationTitle, token;
    SharedPreferences gettingEventId, gettingEventName;

    @Override
    public void onReceive(Context context, Intent intent) {

        MainActivity mainActivity = new MainActivity();

        activity = mainActivity.shareActivity();

        //getting event name
        gettingEventName = context.getSharedPreferences("eventName", Context.MODE_PRIVATE);
        eventName = gettingEventName.getString("name", "");

        //getting event id
        gettingEventId = context.getSharedPreferences("eventId", Context.MODE_PRIVATE);
        eventId = gettingEventId.getString("eventID", "");

        event = new Event();
        user = new User();
        eventUserIds = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        //getting joined user name
        databaseReference.child("events").child(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        event = snapshot.getValue(Event.class);
                        eventUserIds = event.getUserID();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //sending tokens
        databaseReference.child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (snapshot.exists()) {
                        user = dataSnapshot.getValue(User.class);

                        String userKey = dataSnapshot.getKey();

                        for (int a = 0; a < eventUserIds.size(); a++) {
                            if (eventUserIds.get(a).equals(userKey)) {

                                token = user.getToken();
                                notificationTitle = "Plan&Meet";
                                msg = "Event " + eventName + " Has Reached The Meeting Time";

                                FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(
                                        token,
                                        msg,
                                        notificationTitle,
                                        context.getApplicationContext(),
                                        activity
                                );
                                fcmNotificationsSender.SendNotifications();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}