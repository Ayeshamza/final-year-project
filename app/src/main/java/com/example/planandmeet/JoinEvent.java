package com.example.planandmeet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinEvent extends AppCompatActivity {

    EditText enterEventid;
    Button JoinEvent;

    int listSizeFlag;
    int size;
    long eventsCount;
    long eventCounter = 1;
    int updateFlag;
    public static ArrayList<String> eventsUserIdList;
    public static String msg, title, token, userName, eventName, currentUserId;

    Event event = new Event();
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_event);

        ImageButton back = findViewById(R.id.backToMain);
        TextView title = findViewById(R.id.title);
        title.setText(R.string.joinEventTitle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JoinEvent.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        enterEventid = findViewById(R.id.enterEventId);
        JoinEvent = findViewById(R.id.joinEvent);

        JoinEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performEventJoining();
            }
        });
    }

    private void performEventJoining() {

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String enterEventID = enterEventid.getText().toString();

        FirebaseDatabase.getInstance().getReference("events")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            eventsCount = snapshot.getChildrenCount();

                            if (dataSnapshot.exists()) {
                                listSizeFlag = 0;
                                size = 0;

                                String eventKey = dataSnapshot.getKey();

                                if (enterEventID.equals(eventKey)) {
                                    updateFlag = 0;

                                    event = dataSnapshot.getValue(Event.class);
                                    eventsUserIdList = new ArrayList<>();
                                    eventsUserIdList = event.getUserID();
                                    eventName = event.getName();

                                    if (listSizeFlag == 0) {        //(if) to get size only once
                                        size = eventsUserIdList.size();
                                        listSizeFlag = 1;
                                    }

                                    for (int a = 0; a < size; a++) {        //if(a!=0) because not to check the organizer events
                                        if (a != 0 && eventsUserIdList.get(a).equals(currentUserId)) {
                                            Toast.makeText(JoinEvent.this, "Event Already Joined!", Toast.LENGTH_SHORT).show();
                                            toMainActivity();
                                            updateFlag = 1;
                                        } else if (a == 0 && currentUserId.equals(eventsUserIdList.get(a))) {   //check for user's own event
                                            Toast.makeText(JoinEvent.this, "You Can't Join This Event!", Toast.LENGTH_SHORT).show();
                                            toMainActivity();
                                            updateFlag = 1;
                                        }
                                    }

                                    if (updateFlag == 0) {
                                        eventsUserIdList.add(currentUserId);

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("userID", eventsUserIdList);

                                        FirebaseDatabase.getInstance().getReference().child("events")
                                                .child(eventKey).updateChildren(map)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                        Toast.makeText(JoinEvent.this, "Event Joined Successfully", Toast.LENGTH_SHORT).show();
                                                        toMainActivity();
                                                        sendNotification();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(JoinEvent.this, "Event not Joined!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    if (eventsCount == eventCounter) {
                                        Toast.makeText(JoinEvent.this, "Event does not Exist!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        eventCounter++;
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });
    }

    private void sendNotification() {
        eventsUserIdList.remove(currentUserId);
        user = new User();

        DatabaseReference usersReference = FirebaseDatabase.getInstance().getReference().child("users");

        //getting joined user name
        usersReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                userName = user.getName();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //sending tokens
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (snapshot.exists()) {
                        user = dataSnapshot.getValue(User.class);

                        String userKey = dataSnapshot.getKey();

                        for (int a = 0; a < eventsUserIdList.size(); a++) {
                            if (eventsUserIdList.get(a).equals(userKey)) {

                                token = user.getToken();
                                title = "Plan&Meet";
                                msg = userName + " Joined The Event " + eventName;

                                FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(
                                        token,
                                        msg,
                                        title,
                                        getApplicationContext(),
                                        JoinEvent.this
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

    public void toMainActivity() {
        Intent intent = new Intent(JoinEvent.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(JoinEvent.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}