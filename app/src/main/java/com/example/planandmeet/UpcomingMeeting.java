package com.example.planandmeet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class UpcomingMeeting extends AppCompatActivity {

    DrawerLayout drawerLayout;
    TextView toolBrText;

    RecyclerView meetingsRecyclerView;
    UpComingMeetingsAdapter upComingMeetingsAdapter;
    Event event;

    String currentUserID;
    ArrayList<Event> eventsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_meeting);

        toolBrText = findViewById(R.id.toolbarText);
        toolBrText.setText(R.string.upcomingMeetingTitle);

        drawerLayout = findViewById(R.id.drawer_layout);

        //getting current User id
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        event = new Event();
        eventsList = new ArrayList<>();

        meetingsRecyclerView = findViewById(R.id.upComingEventsList);
        meetingsRecyclerView.setHasFixedSize(true);
        meetingsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        upComingMeetingsAdapter = new UpComingMeetingsAdapter(this, eventsList);
        meetingsRecyclerView.setAdapter(upComingMeetingsAdapter);

        FirebaseDatabase.getInstance().getReference().child("events")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {
                                event = dataSnapshot.getValue(Event.class);

                                for (int a = 0; a < event.getUserID().size(); a++) {
                                    if (currentUserID.equals(event.getUserID().get(a))) {

                                        String meetFinalDate = event.getMeetingOccurence();
                                        String defaultString = "Not Set Yet";

                                        if (!meetFinalDate.equals(defaultString)) {

                                            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);

                                            Date currentDateTime = new Date();
                                            Date meetingDate = null;

                                            try {
                                                meetFinalDate = meetFinalDate.concat(":00");
                                                meetingDate = sdf1.parse(meetFinalDate);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            if (meetingDate.compareTo(currentDateTime) > 0) {
                                                eventsList.add(event);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        upComingMeetingsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public void ClickMenu(View view) {
        MainActivity.openDrawer(drawerLayout);
    }

    public void ClickLogo(View view) {
        //close drawer
        MainActivity.closeDrawer(drawerLayout);
    }

    public void ClickHome(View view) {
        //redirect activity
        MainActivity.redirectActivity(this, MainActivity.class);
    }

    public void ClickProfile(View view) {
        //redirect activity
        MainActivity.redirectActivity(this, Profile.class);
    }

    public void ClickUpcomingMeeting(View view) {
        //recreate activity
        recreate();
    }

    public void ClickShare(View view) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=WvJBXWiSkTU&t=1085s");
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "ShareVia"));
    }

    public void ClickAboutUs(View view) {
        //redirect activity
        MainActivity.redirectActivity(this, AboutUs.class);
    }

    public void ClickLogout(View view) {
        //close app
        MainActivity.logout(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //close drawer
        MainActivity.closeDrawer(drawerLayout);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(UpcomingMeeting.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}