package com.example.planandmeet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    SharedPreferences shareEventName, shareEventId, shareEventStartDate, shareEventEndDate, shareEventCreationDate;

    Activity activity;
    Context context;
    ArrayList<Event> eventsList;
    ArrayList<String> eventIdList, outDatedEventIdList;
    ArrayList<String> userIdList;

    EditText updateName, updateStartDate;
    //TextView updateSpinner;
    Button btnUpdate;
    private Spinner spinner;

    DatePickerDialog datePicker;
    final Calendar calendar = Calendar.getInstance();

    String currentUserId;
    String modeSelected;
    int spinnerFlag = 0;

    String token, title, msg;

    //final Calendar myCalendar = Calendar.getInstance();

    public MyAdapter(Context context, ArrayList<Event> eventsList, ArrayList<String> eventIdList, ArrayList<String> outDatedEventIdList, Activity activity) {
        this.context = context;
        this.eventsList = eventsList;
        this.eventIdList = eventIdList;
        this.outDatedEventIdList = outDatedEventIdList;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //removing out dated events
        if (!outDatedEventIdList.isEmpty()) {
            for (int a = 0; a < outDatedEventIdList.size(); a++) {
                deleteEvent(outDatedEventIdList.get(a));
            }
        }

        //Setting data on Main Activity
        Event event = eventsList.get(position);

        holder.name.setText(event.getName());
        holder.startDate.setText(event.getStartDate());
        holder.endDate.setText(event.getEndDate());
        holder.spinner.setText(event.getMode());
        holder.members.setText(Integer.toString(event.getUserID().size()));
        holder.eventMeet.setText(event.getMeetingOccurence());

        // current user id
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userIdList = event.getUserID();

        if (userIdList.get(0).equals(currentUserId)) {
            holder.btnLeave.setVisibility(View.GONE);
            holder.btnEdit.setVisibility(View.VISIBLE);
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnLeave.setVisibility(View.VISIBLE);
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }

        // cardView click
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent3 = new Intent(context, EventDetails.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent3);

                shareEventName = context.getSharedPreferences("eventName", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = shareEventName.edit();
                editor1.putString("name", event.getName());
                editor1.apply();

                shareEventCreationDate = context.getSharedPreferences("eventCreationDate", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor12 = shareEventCreationDate.edit();
                editor12.putString("creationDate", getDate(event.getCreationDate().toString()));
                editor12.apply();

                shareEventStartDate = context.getSharedPreferences("eventStartDate", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor11 = shareEventStartDate.edit();
                editor11.putString("startDate", event.getStartDate());
                editor11.apply();

                shareEventEndDate = context.getSharedPreferences("eventEndDate", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor111 = shareEventEndDate.edit();
                editor111.putString("endDate", event.getEndDate());
                editor111.apply();

                shareEventId = context.getSharedPreferences("eventId", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor2 = shareEventId.edit();
                editor2.putString("eventID", eventIdList.get(position));
                editor2.apply();
            }
        });

        // leave event
        holder.btnLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userIdList = new ArrayList<>();
                userIdList = event.getUserID();

                for (int a = 0; a < userIdList.size(); a++) {
                    if (userIdList.get(a).equals(currentUserId)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(holder.name.getContext());
                        builder.setTitle("Are You Sure?");

                        builder.setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                removeChat();
                                removeSchedule();
                                removeDocument();

                                userIdList.remove(currentUserId);

                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("userID", userIdList);

                                FirebaseDatabase.getInstance().getReference().child("events")
                                        .child(eventIdList.get(position)).updateChildren(hashMap)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(holder.name.getContext(), "Event Left Successfully", Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull @NotNull Exception e) {
                                                Toast.makeText(holder.name.getContext(), "Failed to Leave!", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });

                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.show();
                    }
                }
            }

            private void removeChat() {
                DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference().child("chats").child(eventIdList.get(position));
                chatReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {
                                ChatModel chatModel = dataSnapshot.getValue(ChatModel.class);
                                String senderId = chatModel.getSenderID();

                                if (senderId.equals(currentUserId)) {
                                    String chatKey = dataSnapshot.getKey();

                                    chatReference.child(chatKey).removeValue();
                                }

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            private void removeSchedule() {
                DatabaseReference scheduleReference = FirebaseDatabase.getInstance().getReference().child("scheduling").child(eventIdList.get(position));
                scheduleReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {
                                SchedulingModel schedulingModel = dataSnapshot.getValue(SchedulingModel.class);
                                String scheduleUserId = schedulingModel.getUserId();

                                if (scheduleUserId.equals(currentUserId)) {
                                    String scheduleKey = dataSnapshot.getKey();

                                    scheduleReference.child(scheduleKey).removeValue();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            private void removeDocument() {
                DatabaseReference documentReference = FirebaseDatabase.getInstance().getReference().child("documents").child(eventIdList.get(position));
                documentReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {
                                DocumentModel schedulingModel = dataSnapshot.getValue(DocumentModel.class);
                                String documentUserId = schedulingModel.getSenderId();

                                if (documentUserId.equals(currentUserId)) {
                                    String documentKey = dataSnapshot.getKey();
                                    documentReference.child(documentKey).removeValue();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });

        //Edit Button of Event
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {

                spinnerFlag = 0;

                final DialogPlus dialogPlus = DialogPlus.newDialog(holder.name.getContext())
                        .setContentHolder(new ViewHolder(R.layout.edit_pop_up))
                        .setExpanded(true, 1010)
                        .create();

                //setting data to update pop-up
                View view = dialogPlus.getHolderView();

                updateName = view.findViewById(R.id.editEventName);
                updateStartDate = view.findViewById(R.id.editEventStartDate);

                spinner = view.findViewById(R.id.editEventSelectMode);

                updateName.setText(event.getName());
                updateStartDate.setText(event.getStartDate());

                dialogPlus.show();

                final int day = calendar.get(Calendar.DAY_OF_MONTH);
                final int year = calendar.get(Calendar.YEAR);
                final int month = calendar.get(Calendar.MONTH);
                datePicker = new DatePickerDialog(v.getRootView().getContext());

                //start date
                updateStartDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        datePicker = new DatePickerDialog(v.getRootView().getContext(), new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                                // adding the selected date in the edittext
                                updateStartDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                            }
                        }, year, month, day);

                        // set maximum date to be selected as today
                        datePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());

                        // show the dialog
                        datePicker.show();
                    }
                });

                List<String> EventMode = new ArrayList<>();

                EventMode.add("Online");
                EventMode.add("Offline");

                ArrayAdapter<String> myAdapter = new ArrayAdapter<>(context.getApplicationContext(), android.R.layout.simple_spinner_item, EventMode);
                myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(myAdapter);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int positon, long id) {

                        if (spinnerFlag == 0) {
                            if (event.getMode().equals("Online")) {
                                spinner.setSelection(0);
                            } else {
                                spinner.setSelection(1);
                            }
                            modeSelected = spinner.getSelectedItem().toString();
                            spinnerFlag = 1;
                        } else {
                            modeSelected = spinner.getSelectedItem().toString();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                // update event in Firebase
                btnUpdate = view.findViewById(R.id.updateEventBtn);
                btnUpdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((updateName.getText().toString()).equals(event.getName()) && (updateStartDate.getText().toString()).equals(event.getStartDate()) && modeSelected.equals(event.getMode())) {
                            Toast.makeText(holder.name.getContext(), "Nothing Changed!", Toast.LENGTH_SHORT).show();
                        } else {
                            Map<String, Object> map = new HashMap<>();

                            map.put("name", updateName.getText().toString());
                            map.put("startDate", updateStartDate.getText().toString());
                            map.put("endDate", generateEndDate(updateStartDate.getText().toString()));
                            map.put("mode", modeSelected);

                            FirebaseDatabase.getInstance().getReference().child("events")
                                    .child(eventIdList.get(position)).updateChildren(map)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(holder.name.getContext(), "Event Updated Successfully", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();

                                            userIdList = event.getUserID();

                                            if (userIdList.size() > 1) {
                                                sendNotification();
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(holder.name.getContext(), "Data not Updated", Toast.LENGTH_SHORT).show();
                                            dialogPlus.dismiss();
                                        }
                                    });
                        }
                    }

                    private void sendNotification() {

                        FirebaseDatabase.getInstance().getReference().child("users")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            if (snapshot.exists()) {
                                                User user = dataSnapshot.getValue(User.class);
                                                String userKey = dataSnapshot.getKey();

                                                for (int a = 1; a < userIdList.size(); a++) {
                                                    if (userIdList.get(a).equals(userKey)) {

                                                        token = user.getToken();
                                                        title = "Plan&Meet";
                                                        msg = "Event " + event.getName() + " has been updated. Please check!";

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
                });
            }
        });

        // remove event from Firebase
        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.name.getContext());
                builder.setTitle("Are You Sure?");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEvent(eventIdList.get(position));
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(holder.name.getContext(), "Deletion Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        holder.shareHolderEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent shareintent = new Intent();
                shareintent.setAction(Intent.ACTION_SEND);
                shareintent.putExtra(Intent.EXTRA_TEXT, "You have been invited to Plan&Meet for an Event.\nEnter this Event Id to join the Event\n\n" + eventIdList.get(position));
                shareintent.setType("text/plain");
                context.startActivity(Intent.createChooser(shareintent, "ShareVia"));
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView shareHolderEvent;
        TextView name, startDate, endDate, spinner, members, eventMeet;     //for main activity
        Button btnEdit, btnDelete, btnLeave;      //for edit/delete Event
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.eventName);
            startDate = itemView.findViewById(R.id.startDate);
            endDate = itemView.findViewById(R.id.endDate);
            spinner = itemView.findViewById(R.id.eventMode);
            members = itemView.findViewById(R.id.eventMembers);
            eventMeet = itemView.findViewById(R.id.eventMeeting);

            btnEdit = itemView.findViewById(R.id.editEventBtn);
            btnDelete = itemView.findViewById(R.id.deleteEventBtn);
            btnLeave = itemView.findViewById(R.id.leaveEventBtn);
            shareHolderEvent = itemView.findViewById(R.id.shareHolderEvent);

            cardView = itemView.findViewById(R.id.clickCardView);
        }
    }

    private String generateEndDate(String startDate) {
        String output;

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(sdf.parse(startDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        c.add(Calendar.DATE, 6);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE

        output = sdf.format(c.getTime());

        return output;
    }

    private void deleteEvent(String eventId) {
        //removing event
        FirebaseDatabase.getInstance().getReference().child("events")
                .child(eventId).removeValue();

        //removing schedules
        FirebaseDatabase.getInstance().getReference().child("scheduling")
                .child(eventId).removeValue();

        //removing chat
        FirebaseDatabase.getInstance().getReference().child("chats")
                .child(eventId).removeValue();

        //removing documents
        FirebaseDatabase.getInstance().getReference().child("documents")
                .child(eventId).removeValue();
    }

    private String getDate(String time) {
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(time));
        String myFormat = "dd/MM/yy hh:mm a";
        return DateFormat.format(myFormat, calendar).toString();
    }
}