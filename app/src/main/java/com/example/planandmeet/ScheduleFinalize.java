package com.example.planandmeet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleFinalize extends AppCompatActivity {

    RecyclerView.LayoutManager layoutManager;
    RecyclerView checkboxRecyclerView, timeTextboxRecyclerView, dayTextboxRecyclerView;
    SetCheckBoxAdapter checkBoxAdapter;
    TimeTextViewAdapter timeTextViewAdapter;
    DayTextViewAdapter dayTextViewAdapter;

    Spinner spinner;

    public static String currentUserID;
    public static String userIdToCheckboxAdapter;
    public String[] timeSlots;
    public static String eventId;
    public static ArrayList<String> usersIds = new ArrayList<>();
    public static String[] usersNames;

    SchedulingModel schedulingModel;
    User user;
    Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_finalize);

        //getting event id
        SharedPreferences sharedPreferences = getSharedPreferences("eventId", MODE_PRIVATE);
        eventId = sharedPreferences.getString("eventID", "");

        //getting current User id
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        schedulingModel = new SchedulingModel();
        user = new User();
        event = new Event();

        TextView title = findViewById(R.id.title);
        title.setText(R.string.schedulesTitle);

        ImageButton back = findViewById(R.id.backToMain);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleFinalize.this, EventDetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Button commonSlots = findViewById(R.id.commonSlotsBtn);
        commonSlots.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ScheduleFinalize.this, ScheduleCommonSlots.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent1);
            }
        });

        //show/hide common Slots Btn
        FirebaseDatabase.getInstance().getReference().child("events").child(eventId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            event = snapshot.getValue(Event.class);
                            if (currentUserID.equals(event.getUserID().get(0))) {
                                commonSlots.setVisibility(View.VISIBLE);
                            } else {
                                commonSlots.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        //getting User Ids From Schedule
        FirebaseDatabase.getInstance().getReference().child("scheduling").child(eventId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usersIds.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {
                                schedulingModel = dataSnapshot.getValue(SchedulingModel.class);
                                usersIds.add(schedulingModel.getUserId());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        //accessing users table for names
        FirebaseDatabase.getInstance().getReference().child("users")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        //initializing array
                        usersNames = new String[usersIds.size()];
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {
                                String userKey = dataSnapshot.getKey();

                                for (int a = 0; a < usersIds.size(); a++) {
                                    if (userKey.equals(usersIds.get(a))) {
                                        user = dataSnapshot.getValue(User.class);
                                        usersNames[a] = user.getName();

                                        fillSpinner();
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                    }
                });

        displayTimeSlots();
        displayDays();
    }

    private void fillSpinner() {
        spinner = findViewById(R.id.userNameSpinner);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, usersNames);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String selectedName = spinner.getSelectedItem().toString();

                for (int a = 0; a < usersNames.length; a++) {
                    if (selectedName.equals(usersNames[a])) {
                        userIdToCheckboxAdapter = usersIds.get(a);
                        displayCheckBoxes();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void displayTimeSlots() {
        String time;
        timeSlots = new String[10];

        timeTextboxRecyclerView = findViewById(R.id.timeSlotsList);
        timeTextboxRecyclerView.setHasFixedSize(true);
        timeTextboxRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        timeTextViewAdapter = new TimeTextViewAdapter(this, timeSlots);
        timeTextboxRecyclerView.setAdapter(timeTextViewAdapter);

        int i = 0;
        for (int timeNumber = 9; timeNumber < 19; timeNumber++) {
            if (timeNumber == 9) {
                time = "09:00";
                timeSlots[i] = time;
            } else {
                String sampleTime = ":00";
                time = timeNumber + sampleTime;

                timeSlots[++i] = time;
            }
            timeTextViewAdapter.notifyDataSetChanged();
        }
    }

    public void displayCheckBoxes() {
        int id;
        int flag = 1;

        for (int listNo = 1; listNo <= 7; listNo++) {
            id = getResources().getIdentifier("checkBoxList" + listNo, "id", getPackageName());

            checkboxRecyclerView = findViewById(id);
            checkboxRecyclerView.setHasFixedSize(true);
            checkboxRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            checkBoxAdapter = new SetCheckBoxAdapter(this, listNo, eventId, timeSlots, flag, userIdToCheckboxAdapter);
            checkboxRecyclerView.setAdapter(checkBoxAdapter);
            checkBoxAdapter.notifyDataSetChanged();
        }// end for loop
    }

    public void displayDays() {
        String[] days = new String[7];

        dayTextboxRecyclerView = findViewById(R.id.daysList);
        dayTextboxRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        dayTextboxRecyclerView.setLayoutManager(layoutManager);
        dayTextViewAdapter = new DayTextViewAdapter(this, days);
        dayTextboxRecyclerView.setAdapter(dayTextViewAdapter);

        FirebaseDatabase.getInstance().getReference("events")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {
                                String referenceEventId = dataSnapshot.getKey();

                                if (referenceEventId.equals(eventId)) {
                                    String startDate = (String) dataSnapshot.child("startDate").getValue();

                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    Calendar c = Calendar.getInstance();

                                    try {
                                        c.setTime(sdf.parse(startDate));

                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    for (int i = 0; i < 7; i++) {
                                        if (i == 0) {
                                            c.add(Calendar.DATE, 0);
                                            SimpleDateFormat sdf1 = new SimpleDateFormat("EEE");
                                            days[i] = sdf1.format(c.getTime());
                                        } else {
                                            c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                            SimpleDateFormat sdf1 = new SimpleDateFormat("EEE");
                                            days[i] = sdf1.format(c.getTime());
                                        }
                                    }
                                }
                            }
                        }
                        dayTextViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ScheduleFinalize.this, EventDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}