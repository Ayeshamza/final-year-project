package com.example.planandmeet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ScheduleCommonSlots extends AppCompatActivity {

    Button finalSlotBtn;
    EditText offlineVenueAddress;

    RecyclerView.LayoutManager layoutManager;
    RecyclerView commonSlotTextBoxRecyclerView, timeTextboxRecyclerView, dayTextboxRecyclerView;
    ScheduleCommonSlotsAdapter scheduleCommonSlotsAdapter;
    TimeTextViewAdapter timeTextViewAdapter;
    DayTextViewAdapter dayTextViewAdapter;

    public String[] timeSlots;
    public static String eventId;
    String eventMode;

    public static String[] days = new String[7];
    public static String[] dates = new String[7];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_common_slots);

        //getting event id
        SharedPreferences sharedPreferences = getSharedPreferences("eventId", MODE_PRIVATE);
        eventId = sharedPreferences.getString("eventID", "");

        TextView title = findViewById(R.id.title);
        title.setText(R.string.commonSlotsTitl);

        ImageButton back = findViewById(R.id.backToMain);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScheduleCommonSlots.this, ScheduleFinalize.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        offlineVenueAddress = findViewById(R.id.offlineVenue);
        offlineVenueAddress.setVisibility(View.GONE);

        finalSlotBtn = findViewById(R.id.saveFinalSlotBtn);

        //getting event mode for difference
        FirebaseDatabase.getInstance().getReference().child("events").child(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()) {
                            Event event = snapshot.getValue(Event.class);
                            eventMode = event.getMode();

                            if (eventMode.equals("Offline")) {
                                offlineVenueAddress.setVisibility(View.VISIBLE);
                            } else {
                                offlineVenueAddress.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        finalSlotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String venue = offlineVenueAddress.getText().toString().trim();

                if (venue.matches("") && eventMode.equals("Offline")) {
                    offlineVenueAddress.setText("");
                    offlineVenueAddress.requestFocus();
                    offlineVenueAddress.setError("Enter a Meeting Address!");
                } else {
                    ScheduleCommonSlotsAdapter scheduleCommonSlotsAdapter = new ScheduleCommonSlotsAdapter();
                    scheduleCommonSlotsAdapter.saveFinalSlot(venue, eventMode);
                }
            }
        });

        displayTimeSlots();
        displayDays();
        displayTextBoxes();
    }

    public void displayTimeSlots() {
        String time;
        timeSlots = new String[10];

        timeTextboxRecyclerView = findViewById(R.id.timeSlotList);
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

        dayTextboxRecyclerView = findViewById(R.id.weekDaysList);
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

                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("EEE");
                                    @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
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

    public void displayTextBoxes() {
        int id;

        for (int listNo = 1; listNo <= 7; listNo++) {
            id = getResources().getIdentifier("textBoxList" + listNo, "id", getPackageName());

            commonSlotTextBoxRecyclerView = findViewById(id);
            commonSlotTextBoxRecyclerView.setHasFixedSize(true);
            commonSlotTextBoxRecyclerView.setLayoutManager(new LinearLayoutManager(this));
            scheduleCommonSlotsAdapter = new ScheduleCommonSlotsAdapter(this, eventId, listNo, timeSlots, dates, ScheduleCommonSlots.this);
            commonSlotTextBoxRecyclerView.setAdapter(scheduleCommonSlotsAdapter);
            scheduleCommonSlotsAdapter.notifyDataSetChanged();
        }// end for loop
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ScheduleCommonSlots.this, ScheduleFinalize.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}