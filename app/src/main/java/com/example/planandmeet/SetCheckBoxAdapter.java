package com.example.planandmeet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SetCheckBoxAdapter extends RecyclerView.Adapter<SetCheckBoxAdapter.MyViewHolder> {

    @SuppressLint("StaticFieldLeak")
    public static Context context;
    int listNo;
    public static String eventId;
    String[] timeSlots, dates;
    public static String userID;
    public static String scheduleKey;
    String time;
    int activityFlag;

    //to disable outdated time slots
    Date selectedSlot;
    String makeSelectedSlot;

    public static ArrayList<String> timeListDay1 = new ArrayList<>();
    public static ArrayList<String> timeListDay2 = new ArrayList<>();
    public static ArrayList<String> timeListDay3 = new ArrayList<>();
    public static ArrayList<String> timeListDay4 = new ArrayList<>();
    public static ArrayList<String> timeListDay5 = new ArrayList<>();
    public static ArrayList<String> timeListDay6 = new ArrayList<>();
    public static ArrayList<String> timeListDay7 = new ArrayList<>();

    //constructor for Scheduling Class
    public SetCheckBoxAdapter(Context context, int listNo, String eventId, String[] timeSlots, int activityFlag, String userID, String[] dates) {
        SetCheckBoxAdapter.context = context;
        this.listNo = listNo;
        SetCheckBoxAdapter.eventId = eventId;
        this.timeSlots = timeSlots;
        this.activityFlag = activityFlag;
        SetCheckBoxAdapter.userID = userID;
        this.dates = dates;
    }

    //constructor for ScheduleFinalize Class
    public SetCheckBoxAdapter(Context context, int listNo, String eventId, String[] timeSlots, int activityFlag, String userID) {
        SetCheckBoxAdapter.context = context;
        this.listNo = listNo;
        SetCheckBoxAdapter.eventId = eventId;
        this.timeSlots = timeSlots;
        this.activityFlag = activityFlag;
        SetCheckBoxAdapter.userID = userID;
    }

    public SetCheckBoxAdapter() {
    }

    @NonNull
    @NotNull
    @Override
    public SetCheckBoxAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View checkboxView = LayoutInflater.from(context).inflate(R.layout.checkbox_item, parent, false);
        return new SetCheckBoxAdapter.MyViewHolder(checkboxView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SetCheckBoxAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        timeListDay1.clear();
        timeListDay2.clear();
        timeListDay3.clear();
        timeListDay4.clear();
        timeListDay5.clear();
        timeListDay6.clear();
        timeListDay7.clear();

        if (activityFlag == 0) {        //from scheduling.class
            loadSavedSchedule(holder, position, userID);

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);
            Date currentDateTime = new Date();

            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    selectedSlot = null;

                    switch (listNo) {
                        case 1: {
                            time = sharePositionGetTime(position);

                            try {
                                makeSelectedSlot = dates[0].concat(" ").concat(time).concat(":00");
                                selectedSlot = sdf1.parse(makeSelectedSlot);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (selectedSlot.compareTo(currentDateTime) == 0 || selectedSlot.compareTo(currentDateTime) < 0) {
                                holder.checkBox.setChecked(false);
                                holder.checkBox.setClickable(false);
                                Toast.makeText(context.getApplicationContext(), "Not a Valid Slot!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (holder.checkBox.isChecked()) {
                                    timeListDay1.add(time);
                                } else {
                                    for (int a = 0; a < timeListDay1.size(); a++) {
                                        if (time.equals(timeListDay1.get(a))) {
                                            timeListDay1.remove(time);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case 2: {
                            time = sharePositionGetTime(position);

                            try {
                                makeSelectedSlot = dates[1].concat(" ").concat(time).concat(":00");
                                selectedSlot = sdf1.parse(makeSelectedSlot);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (selectedSlot.compareTo(currentDateTime) == 0 || selectedSlot.compareTo(currentDateTime) < 0) {
                                holder.checkBox.setChecked(false);
                                holder.checkBox.setClickable(false);
                                Toast.makeText(context.getApplicationContext(), "Not a Valid Slot!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (holder.checkBox.isChecked()) {
                                    timeListDay2.add(time);
                                } else {
                                    for (int a = 0; a < timeListDay2.size(); a++) {
                                        if (time.equals(timeListDay2.get(a))) {
                                            timeListDay2.remove(time);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case 3: {
                            time = sharePositionGetTime(position);

                            try {
                                makeSelectedSlot = dates[2].concat(" ").concat(time).concat(":00");
                                selectedSlot = sdf1.parse(makeSelectedSlot);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (selectedSlot.compareTo(currentDateTime) == 0 || selectedSlot.compareTo(currentDateTime) < 0) {
                                holder.checkBox.setChecked(false);
                                holder.checkBox.setClickable(false);
                                Toast.makeText(context.getApplicationContext(), "Not a Valid Slot!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (holder.checkBox.isChecked()) {
                                    timeListDay3.add(time);
                                } else {
                                    for (int a = 0; a < timeListDay3.size(); a++) {
                                        if (time.equals(timeListDay3.get(a))) {
                                            timeListDay3.remove(time);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case 4: {
                            time = sharePositionGetTime(position);

                            try {
                                makeSelectedSlot = dates[3].concat(" ").concat(time).concat(":00");
                                selectedSlot = sdf1.parse(makeSelectedSlot);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (selectedSlot.compareTo(currentDateTime) == 0 || selectedSlot.compareTo(currentDateTime) < 0) {
                                holder.checkBox.setChecked(false);
                                holder.checkBox.setClickable(false);
                                Toast.makeText(context.getApplicationContext(), "Not a Valid Slot!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (holder.checkBox.isChecked()) {
                                    timeListDay4.add(time);
                                } else {
                                    for (int a = 0; a < timeListDay4.size(); a++) {
                                        if (time.equals(timeListDay4.get(a))) {
                                            timeListDay4.remove(time);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case 5: {
                            time = sharePositionGetTime(position);

                            try {
                                makeSelectedSlot = dates[4].concat(" ").concat(time).concat(":00");
                                selectedSlot = sdf1.parse(makeSelectedSlot);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (selectedSlot.compareTo(currentDateTime) == 0 || selectedSlot.compareTo(currentDateTime) < 0) {
                                holder.checkBox.setChecked(false);
                                holder.checkBox.setClickable(false);
                                Toast.makeText(context.getApplicationContext(), "Not a Valid Slot!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (holder.checkBox.isChecked()) {
                                    timeListDay5.add(time);
                                } else {
                                    for (int a = 0; a < timeListDay5.size(); a++) {
                                        if (time.equals(timeListDay5.get(a))) {
                                            timeListDay5.remove(time);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case 6: {
                            time = sharePositionGetTime(position);

                            try {
                                makeSelectedSlot = dates[5].concat(" ").concat(time).concat(":00");
                                selectedSlot = sdf1.parse(makeSelectedSlot);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (selectedSlot.compareTo(currentDateTime) == 0 || selectedSlot.compareTo(currentDateTime) < 0) {
                                holder.checkBox.setChecked(false);
                                holder.checkBox.setClickable(false);
                                Toast.makeText(context.getApplicationContext(), "Not a Valid Slot!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (holder.checkBox.isChecked()) {
                                    timeListDay6.add(time);
                                } else {
                                    for (int a = 0; a < timeListDay6.size(); a++) {
                                        if (time.equals(timeListDay6.get(a))) {
                                            timeListDay6.remove(time);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        case 7: {
                            time = sharePositionGetTime(position);

                            try {
                                makeSelectedSlot = dates[6].concat(" ").concat(time).concat(":00");
                                selectedSlot = sdf1.parse(makeSelectedSlot);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (selectedSlot.compareTo(currentDateTime) == 0 || selectedSlot.compareTo(currentDateTime) < 0) {
                                holder.checkBox.setChecked(false);
                                holder.checkBox.setClickable(false);
                                Toast.makeText(context.getApplicationContext(), "Not a Valid Slot!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (holder.checkBox.isChecked()) {
                                    timeListDay7.add(time);
                                } else {
                                    for (int a = 0; a < timeListDay7.size(); a++) {
                                        if (time.equals(timeListDay7.get(a))) {
                                            timeListDay7.remove(time);
                                        }
                                    }
                                }
                            }
                            break;
                        }
                        default:
                            throw new IllegalStateException("Unexpected value: " + listNo);
                    }
                }
            });
        } else {        //from scheduleFinalize.class
            holder.checkBox.setClickable(false);
            loadSavedSchedule(holder, position, userID);
        }
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CheckBox checkBox;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    private String sharePositionGetTime(int position) {
        TimeTextViewAdapter timeTextViewAdapter = new TimeTextViewAdapter();
        return timeTextViewAdapter.getTimeAtPosition(position);
    }

    //called from Scheduling class
    public void saveScheduleToDatabase() {
        int flag = 0;
        SchedulingModel schedulingModel = new SchedulingModel();

        if (!timeListDay1.isEmpty()) {
            schedulingModel.setDay1Schedule(timeListDay1);
            flag = 1;
        }
        if (!timeListDay2.isEmpty()) {
            schedulingModel.setDay2Schedule(timeListDay2);
            flag = 1;
        }
        if (!timeListDay3.isEmpty()) {
            schedulingModel.setDay3Schedule(timeListDay3);
            flag = 1;
        }
        if (!timeListDay4.isEmpty()) {
            schedulingModel.setDay4Schedule(timeListDay4);
            flag = 1;
        }
        if (!timeListDay5.isEmpty()) {
            schedulingModel.setDay5Schedule(timeListDay5);
            flag = 1;
        }
        if (!timeListDay6.isEmpty()) {
            schedulingModel.setDay6Schedule(timeListDay6);
            flag = 1;
        }
        if (!timeListDay7.isEmpty()) {
            schedulingModel.setDay7Schedule(timeListDay7);
            flag = 1;
        }

        schedulingModel.setUserId(userID);

        if (flag == 1) {
            FirebaseDatabase.getInstance().getReference().child("scheduling")
                    .child(eventId).push().setValue(schedulingModel);

            Intent intent = new Intent(context.getApplicationContext(), EventDetails.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            Toast.makeText(context.getApplicationContext(), "Schedule saved successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context.getApplicationContext(), "Please select at least 1 slot!", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateScheduleToDatabase(String currentUserID) {
        Map<String, Object> map = new HashMap<>();

        map.put("day1Schedule", timeListDay1);
        map.put("day2Schedule", timeListDay2);
        map.put("day3Schedule", timeListDay3);
        map.put("day4Schedule", timeListDay4);
        map.put("day5Schedule", timeListDay5);
        map.put("day6Schedule", timeListDay6);
        map.put("day7Schedule", timeListDay7);

        DatabaseReference scheduleUpdateReference = FirebaseDatabase.getInstance().getReference().child("scheduling").child(eventId);
        scheduleUpdateReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.exists()) {

                        scheduleKey = dataSnapshot.getKey();

                        SchedulingModel schedulingModel = new SchedulingModel();
                        schedulingModel = dataSnapshot.getValue(SchedulingModel.class);

                        if (schedulingModel.getUserId().equals(currentUserID)) {
                            scheduleUpdateReference.child(scheduleKey).updateChildren(map);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Intent intent = new Intent(context.getApplicationContext(), EventDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public void loadSavedSchedule(@NonNull @NotNull SetCheckBoxAdapter.MyViewHolder holder, int position, String userID) {

        FirebaseDatabase.getInstance().getReference().child("scheduling").child(eventId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {

                                scheduleKey = dataSnapshot.getKey();

                                SchedulingModel schedulingModel = new SchedulingModel();
                                schedulingModel = dataSnapshot.getValue(SchedulingModel.class);

                                if (schedulingModel.getUserId().equals(userID)) {
                                    if (dataSnapshot.hasChild("day1Schedule") && listNo == 1) {
                                        timeListDay1 = schedulingModel.getDay1Schedule();
                                        checkCheckedSlots(timeListDay1);
                                    }
                                    if (dataSnapshot.hasChild("day2Schedule") && listNo == 2) {
                                        timeListDay2 = schedulingModel.getDay2Schedule();
                                        checkCheckedSlots(timeListDay2);
                                    }
                                    if (dataSnapshot.hasChild("day3Schedule") && listNo == 3) {
                                        timeListDay3 = schedulingModel.getDay3Schedule();
                                        checkCheckedSlots(timeListDay3);
                                    }
                                    if (dataSnapshot.hasChild("day4Schedule") && listNo == 4) {
                                        timeListDay4 = schedulingModel.getDay4Schedule();
                                        checkCheckedSlots(timeListDay4);
                                    }
                                    if (dataSnapshot.hasChild("day5Schedule") && listNo == 5) {
                                        timeListDay5 = schedulingModel.getDay5Schedule();
                                        checkCheckedSlots(timeListDay5);
                                    }
                                    if (dataSnapshot.hasChild("day6Schedule") && listNo == 6) {
                                        timeListDay6 = schedulingModel.getDay6Schedule();
                                        checkCheckedSlots(timeListDay6);
                                    }
                                    if (dataSnapshot.hasChild("day7Schedule") && listNo == 7) {
                                        timeListDay7 = schedulingModel.getDay7Schedule();
                                        checkCheckedSlots(timeListDay7);
                                    }
                                }
                            }
                        }
                    }

                    private void checkCheckedSlots(ArrayList<String> timeListDay) {
                        for (int i = 0; i < timeListDay.size(); i++) {
                            for (int j = 0; j < timeSlots.length; j++) {
                                if (timeListDay.get(i).equals(timeSlots[j])) {
                                    if (position == j) {
                                        holder.checkBox.setChecked(true);
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
}