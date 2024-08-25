package com.example.planandmeet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EventDetails extends AppCompatActivity {

    TextView title, eventNameText, eventCreationDate, startDate, endDate, offlineMeetingVenue;
    Button schedule, document, chat, checkSchedules, joinMeeting, participants;
    ImageButton backBtn;

    Event event = new Event();

    SharedPreferences gettingEventId, gettingEventName, gettingEventStartDate, gettingEventEndDate, gettingCreationDate;
    public String currentUserID;
    public static String eventId, eventName, eventCreateDate, eventStartDate, eventEndDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        participants = findViewById(R.id.participantsBtn);
        schedule = findViewById(R.id.scheduleBtn);
        document = findViewById(R.id.documentBtn);
        chat = findViewById(R.id.chatBtn);
        checkSchedules = findViewById(R.id.checkSchedulesBtn);
        joinMeeting = findViewById(R.id.joinMeetingBtn);
        eventNameText = findViewById(R.id.eventNameDetail);
        startDate = findViewById(R.id.textstartDate2);
        endDate = findViewById(R.id.textEndDate2);
        offlineMeetingVenue = findViewById(R.id.offlineMeetingVenue);
        eventCreationDate = findViewById(R.id.eventCreationDate2);

        //getting current User id
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //getting event id
        gettingEventId = getSharedPreferences("eventId", MODE_PRIVATE);
        eventId = gettingEventId.getString("eventID", "");

        //getting event name
        gettingEventName = getSharedPreferences("eventName", MODE_PRIVATE);
        eventName = gettingEventName.getString("name", "");
        eventNameText.setText(eventName);

        //getting event creation date
        gettingCreationDate = getSharedPreferences("eventCreationDate", MODE_PRIVATE);
        eventCreateDate = gettingCreationDate.getString("creationDate", "");
        eventCreationDate.setText(eventCreateDate);

        //getting event start date
        gettingEventStartDate = getSharedPreferences("eventStartDate", MODE_PRIVATE);
        eventStartDate = gettingEventStartDate.getString("startDate", "");
        startDate.setText(eventStartDate);

        //getting event end date
        gettingEventEndDate = getSharedPreferences("eventEndDate", MODE_PRIVATE);
        eventEndDate = gettingEventEndDate.getString("endDate", "");
        endDate.setText(eventEndDate);

        backBtn = findViewById(R.id.backToMain);
        title = findViewById(R.id.title);
        title.setText(R.string.eventDetailsTitle);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetails.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // view/hide finalize schedule button
        //checking any schedule exists for that event
        FirebaseDatabase.getInstance().getReference().child("scheduling").child(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            checkSchedules.setVisibility(View.VISIBLE);
                        } else {
                            checkSchedules.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        // view/hide meeting button
        FirebaseDatabase.getInstance().getReference().child("events").child(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.hasChild("meetingOccurence")) {
                                event = snapshot.getValue(Event.class);

                                String eventMode = event.getMode();

                                if (eventMode.equals("Offline")) {
                                    offlineMeetingVenue.setVisibility(View.VISIBLE);
                                    joinMeeting.setVisibility(View.GONE);

                                    offlineMeetingVenue.setText(event.getVenue());
                                } else {
                                    offlineMeetingVenue.setVisibility(View.GONE);

                                    String meetFinalDate = event.getMeetingOccurence();

                                    String defaultString = "Not Set Yet";

                                    if (meetFinalDate.equals(defaultString)) {
                                        joinMeeting.setVisibility(View.GONE);

                                    } else {
                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);

                                        Date currentDateTime = new Date();
                                        Date meetingDate = null;

                                        try {
                                            meetFinalDate = meetFinalDate.concat(":00");
                                            meetingDate = sdf1.parse(meetFinalDate);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        //meeting date time is equal or before current time
                                        if (meetingDate.compareTo(currentDateTime) == 0 || meetingDate.compareTo(currentDateTime) < 0) {
                                            joinMeeting.setVisibility(View.VISIBLE);
                                        } else {
                                            joinMeeting.setVisibility(View.GONE);
                                        }
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        participants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EventDetails.this, EventParticipants.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetails.this, Scheduling.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetails.this, DocumentsView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetails.this, Chat.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        checkSchedules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EventDetails.this, ScheduleFinalize.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        try {
            new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL("https://meet.jit.si"))
                    .setAudioMuted(false)
                    .setVideoMuted(false)
                    .setWelcomePageEnabled(false)
                    .setConfigOverride("requireDisplayName", true)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public void meetingOnclick(View v) {
        if (eventId.length() > 0) {
            JitsiMeetConferenceOptions options;
            options = new JitsiMeetConferenceOptions.Builder().setRoom(eventId).build();
            JitsiMeetActivity.launch(this, options);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(EventDetails.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}