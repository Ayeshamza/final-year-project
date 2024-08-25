package com.example.planandmeet;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Scheduling extends AppCompatActivity {

    RecyclerView.LayoutManager layoutManager;
    RecyclerView checkboxRecyclerView, timeTextboxRecyclerView, dayTextboxRecyclerView;
    SetCheckBoxAdapter checkBoxAdapter, setCheckBoxAdapter;
    TimeTextViewAdapter timeTextViewAdapter;
    DayTextViewAdapter dayTextViewAdapter;

    public static String eventId, currentUserID;
    public String[] timeSlots;
    public static String[] dates = new String[7];

    Button saveButton, updateButton;

    DatabaseReference eventReference;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheduling);


        ImageButton back = findViewById(R.id.backToMain);
        TextView title = findViewById(R.id.title);
        title.setText(R.string.setScheduleTitle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Scheduling.this, EventDetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //getting event id
        SharedPreferences sharedPreferences = getSharedPreferences("eventId", MODE_PRIVATE);
        eventId = sharedPreferences.getString("eventID", "");

        //getting current User id
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        setCheckBoxAdapter = new SetCheckBoxAdapter();

        saveButton = findViewById(R.id.scheduleSaveBtn);
        updateButton = findViewById(R.id.scheduleUpdateBtn);

        saveButton.setVisibility(View.VISIBLE);
        updateButton.setVisibility(View.GONE);

        // hide/show save and update buttons
        FirebaseDatabase.getInstance().getReference().child("scheduling").child(eventId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {

                                SchedulingModel schedulingModel = new SchedulingModel();
                                schedulingModel = dataSnapshot.getValue(SchedulingModel.class);

                                if (schedulingModel.getUserId().equals(currentUserID)) {
                                    saveButton.setVisibility(View.GONE);
                                    updateButton.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Toast.makeText(Scheduling.this, "snapshot else", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        //on click save schedule button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCheckBoxAdapter.saveScheduleToDatabase();
            }
        });

        //on click update schedule button
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCheckBoxAdapter.updateScheduleToDatabase(currentUserID);
            }
        });

        displayDays();
        displayTimeSlots();
        displayCheckBoxes();
    }

    public void displayTimeSlots() {
        String time;
        timeSlots = new String[10];

        timeTextboxRecyclerView = findViewById(R.id.timeList);
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

    public void displayDays() {
        String[] days = new String[7];

        dayTextboxRecyclerView = findViewById(R.id.dayList);
        dayTextboxRecyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        dayTextboxRecyclerView.setLayoutManager(layoutManager);
        dayTextViewAdapter = new DayTextViewAdapter(this, days);
        dayTextboxRecyclerView.setAdapter(dayTextViewAdapter);

        eventReference = FirebaseDatabase.getInstance().getReference("events");

        eventReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SimpleDateFormat")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.exists()) {
                        String referenceEventId = dataSnapshot.getKey();

                        if (referenceEventId.equals(eventId)) {
                            String startDate = (String) dataSnapshot.child("startDate").getValue();

                            SimpleDateFormat sdf1, sdf;
                            sdf1 = new SimpleDateFormat("EEE");
                            sdf = new SimpleDateFormat("dd/MM/yyyy");
                            Calendar c = Calendar.getInstance();

                            try {
                                c.setTime(sdf.parse(startDate));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            for (int i = 0; i < 7; i++) {
                                if (i == 0) {
                                    c.add(Calendar.DATE, 0);
                                    days[i] = sdf1.format(c.getTime());

                                    //getting dates
                                    dates[i] = sdf.format(c.getTime());
                                } else {
                                    c.add(Calendar.DATE, 1);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
                                    days[i] = sdf1.format(c.getTime());

                                    //getting dates
                                    dates[i] = sdf.format(c.getTime());
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

    public void displayCheckBoxes() {
        int id;
        int flag = 0;

        for (int listNo = 1; listNo <= 7; listNo++) {
            id = getResources().getIdentifier("list" + listNo, "id", getPackageName());

            checkboxRecyclerView = findViewById(id);
            checkboxRecyclerView.setHasFixedSize(true);
            checkboxRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            checkBoxAdapter = new SetCheckBoxAdapter(this, listNo, eventId, timeSlots, flag, currentUserID, dates);
            checkboxRecyclerView.setAdapter(checkBoxAdapter);
            checkBoxAdapter.notifyDataSetChanged();
        }// end for loop
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Scheduling.this, EventDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}