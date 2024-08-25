
package com.example.planandmeet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class ScheduleCommonSlotsAdapter extends RecyclerView.Adapter<ScheduleCommonSlotsAdapter.MyViewHolder> {

    public static Context context;
    public static String eventId;
    int listNo;
    String[] timeSlots, dates;
    public static int colorFlag;
    public static String finalDate = "";
    public static String finalTime = "";
    public static ArrayList<String> eventsUserIdList;
    public static String msg, title, token, eventName, meetingTime;

    //to disable outdated time slots
    public static Date selectedSlot;
    public static String makeSelectedSlot;

    public static Activity activity;
    Event event;
    User user;

    public static ArrayList<String> timeListDay1 = new ArrayList<>();
    public static ArrayList<String> timeListDay2 = new ArrayList<>();
    public static ArrayList<String> timeListDay3 = new ArrayList<>();
    public static ArrayList<String> timeListDay4 = new ArrayList<>();
    public static ArrayList<String> timeListDay5 = new ArrayList<>();
    public static ArrayList<String> timeListDay6 = new ArrayList<>();
    public static ArrayList<String> timeListDay7 = new ArrayList<>();

    public ScheduleCommonSlotsAdapter(Context context, String eventId, int listNo, String[] timeSlots, String[] dates, Activity activity) {
        ScheduleCommonSlotsAdapter.context = context;
        ScheduleCommonSlotsAdapter.eventId = eventId;
        this.listNo = listNo;
        this.timeSlots = timeSlots;
        this.dates = dates;
        this.activity = activity;
    }

    public ScheduleCommonSlotsAdapter() {
    }

    @NonNull
    @NotNull
    @Override
    public ScheduleCommonSlotsAdapter.MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View textboxView = LayoutInflater.from(context).inflate(R.layout.textview_item, parent, false);
        return new ScheduleCommonSlotsAdapter.MyViewHolder(textboxView);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ScheduleCommonSlotsAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        colorFlag = 0;

        timeListDay1.clear();
        timeListDay2.clear();
        timeListDay3.clear();
        timeListDay4.clear();
        timeListDay5.clear();
        timeListDay6.clear();
        timeListDay7.clear();

        FirebaseDatabase.getInstance().getReference().child("scheduling").child(eventId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {

                                SchedulingModel schedulingModel = new SchedulingModel();
                                schedulingModel = dataSnapshot.getValue(SchedulingModel.class);

                                if (dataSnapshot.hasChild("day1Schedule") && listNo == 1) {
                                    timeListDay1 = schedulingModel.getDay1Schedule();
                                    setCommomlots(timeListDay1);
                                }
                                if (dataSnapshot.hasChild("day2Schedule") && listNo == 2) {
                                    timeListDay2 = schedulingModel.getDay2Schedule();
                                    setCommomlots(timeListDay2);
                                }
                                if (dataSnapshot.hasChild("day3Schedule") && listNo == 3) {
                                    timeListDay3 = schedulingModel.getDay3Schedule();
                                    setCommomlots(timeListDay3);
                                }
                                if (dataSnapshot.hasChild("day4Schedule") && listNo == 4) {
                                    timeListDay4 = schedulingModel.getDay4Schedule();
                                    setCommomlots(timeListDay4);
                                }
                                if (dataSnapshot.hasChild("day5Schedule") && listNo == 5) {
                                    timeListDay5 = schedulingModel.getDay5Schedule();
                                    setCommomlots(timeListDay5);
                                }
                                if (dataSnapshot.hasChild("day6Schedule") && listNo == 6) {
                                    timeListDay6 = schedulingModel.getDay6Schedule();
                                    setCommomlots(timeListDay6);
                                }
                                if (dataSnapshot.hasChild("day7Schedule") && listNo == 7) {
                                    timeListDay7 = schedulingModel.getDay7Schedule();
                                    setCommomlots(timeListDay7);
                                }
                            }
                        }
                    }

                    private void setCommomlots(ArrayList<String> timeListDay) {
                        int counter = 0;

                        for (int i = 0; i < timeListDay.size(); i++) {
                            for (int j = 0; j < timeSlots.length; j++) {
                                if (timeListDay.get(i).equals(timeSlots[j])) {
                                    if (position == j) {
                                        if (holder.textView.getText().equals("-")) {
                                            holder.textView.setText(String.valueOf(++counter));
                                        } else {
                                            counter = Integer.parseInt(holder.textView.getText().toString());

                                            holder.textView.setText(String.valueOf(++counter));
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

        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (colorFlag == 0) {

                    if (holder.textView.getText().equals("-")) {
                        Toast.makeText(context.getApplicationContext(), "Not a valid slot!", Toast.LENGTH_SHORT).show();
                    } else {
                        finalTime = timeSlots[position];

                        holder.textView.setTextColor(Color.RED);
                        colorFlag++;

                        switch (listNo) {
                            case 1: {
                                finalDate = dates[0];
                                break;
                            }
                            case 2: {
                                finalDate = dates[1];
                                break;
                            }
                            case 3: {
                                finalDate = dates[2];
                                break;
                            }
                            case 4: {
                                finalDate = dates[3];
                                break;
                            }
                            case 5: {
                                finalDate = dates[4];
                                break;
                            }
                            case 6: {
                                finalDate = dates[5];
                                break;
                            }
                            case 7: {
                                finalDate = dates[6];
                                break;
                            }
                            default:
                                throw new IllegalStateException("Unexpected value: " + listNo);
                        }
                    }
                } else {
                    if (holder.textView.getCurrentTextColor() == Color.RED) {
                        holder.textView.setTextColor(Color.BLACK);
                        colorFlag--;
                    } else {
                        Toast.makeText(context.getApplicationContext(), "You can't select more than 1 slot", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.commonSlotTextview);
        }
    }

    public void saveFinalSlot(String venue, String eventMode) {
        if (finalDate.isEmpty() && finalTime.isEmpty() && colorFlag == 0) {
            Toast.makeText(context.getApplicationContext(), "Please select a slot!", Toast.LENGTH_SHORT).show();
        } else {

            //to disable outdated time slots
            selectedSlot = null;
            makeSelectedSlot = null;
            Date currentDateTime = new Date();

            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.ENGLISH);

            String finalDateTime = finalDate.concat(" ").concat(finalTime);

            try {
                makeSelectedSlot = finalDateTime.concat(":00");
                selectedSlot = sdf1.parse(makeSelectedSlot);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (selectedSlot.compareTo(currentDateTime) == 0 || selectedSlot.compareTo(currentDateTime) < 0) {
                Toast.makeText(context.getApplicationContext(), "Not a Valid Slot!", Toast.LENGTH_SHORT).show();
            } else {
                DatabaseReference eventReference = FirebaseDatabase.getInstance().getReference().child("events").child(eventId);
                eventReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild("meetingOccurence")) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("meetingOccurence", finalDateTime);

                            if (eventMode.equals("Offline")) {
                                map.put("venue", venue);
                            }
                            eventReference.updateChildren(map);

                            Intent intent = new Intent(context.getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);

                            //schedule saved notification
                            sendNotification();

                            //on meeting start notification
                            Intent intent1 = new Intent(context.getApplicationContext(), ForMeetingNotificationBroadcast.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intent1, 0);
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            alarmManager.set(AlarmManager.RTC_WAKEUP, selectedSlot.getTime(), pendingIntent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
    }

    private void sendNotification() {
        eventsUserIdList = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        //getting user ids list of event
        databaseReference.child("events").child(eventId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                event = new Event();
                event = snapshot.getValue(Event.class);
                eventsUserIdList = event.getUserID();
                eventName = event.getName();
                meetingTime = event.getMeetingOccurence();
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

                        for (int a = 1; a < eventsUserIdList.size(); a++) {
                            if (eventsUserIdList.get(a).equals(userKey)) {

                                token = user.getToken();
                                title = "Plan&Meet";
                                msg = "Event " + eventName + " Meeting Has Been Finalized At " + meetingTime;

                                FcmNotificationsSender fcmNotificationsSender = new FcmNotificationsSender(
                                        token,
                                        msg,
                                        title,
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