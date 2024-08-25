package com.example.planandmeet;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<Event> eventsList;
    ArrayList<String> eventIdList, outDatedEventIdList;
    ArrayList<String> idd;
    FloatingActionButton joinFab, createFab;

    Event event = new Event();
    String userId;
    String eventKey;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
/*
        // fcm token settings for particular user
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            token = Objects.requireNonNull(task.getResult()).getToken();

                            Map<String, Object> map = new HashMap<>();
                            map.put("token", token);

                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(userId).updateChildren(map);
                        }
                    }
                });
 */
        drawerLayout = findViewById(R.id.drawer_layout);

        recyclerView = findViewById(R.id.eventsList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        eventsList = new ArrayList<>();
        eventIdList = new ArrayList<>();
        outDatedEventIdList = new ArrayList<>();
        idd = new ArrayList<>();

        myAdapter = new MyAdapter(this, eventsList, eventIdList, outDatedEventIdList, MainActivity.this);
        recyclerView.setAdapter(myAdapter);

        FirebaseDatabase.getInstance().getReference().child("events")
                .addValueEventListener(new ValueEventListener() {

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        eventsList.clear();
                        eventIdList.clear();
                        outDatedEventIdList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {

                                idd.clear();
                                idd = (ArrayList<String>) dataSnapshot.child("userID").getValue();

                                for (int a = 0; a < idd.size(); a++) {
                                    if (idd.get(a).equals(userId)) {
                                        event = dataSnapshot.getValue(Event.class);
                                        eventKey = dataSnapshot.getKey();

                                        //getting end dates to remove out dated events
                                        String enddDate = event.getEndDate();

                                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);

                                        Date currentDate = new Date();
                                        Date eventEndDate = null;

                                        try {
                                            eventEndDate = sdf1.parse(enddDate);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (eventEndDate.compareTo(currentDate) > 0) { //end date occurs after current date
                                            eventsList.add(event);
                                            eventIdList.add(eventKey);
                                        } else {
                                            outDatedEventIdList.add(eventKey);
                                        }
                                    }
                                }
                            }
                        }
                        myAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

        //create event FAB
        createFab = findViewById(R.id.createfab);
        createFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEvent.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //join event FAB
        joinFab = findViewById(R.id.joinfab);
        joinFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, JoinEvent.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }//end on create function

    public void ClickMenu(View view) {
        //open drawer
        openDrawer(drawerLayout);
    }

    public static void openDrawer(DrawerLayout drawerLayout) {
        // open drawer layout
        drawerLayout.openDrawer(GravityCompat.START);
    }

    public void ClickLogo(View view) {
        //close drawer
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        //close drawer layout
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            //when drawer is open
            //close the drawer
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public void ClickHome(View view) {
        //recreate activity
        recreate();
    }

    public void ClickUpcomingMeeting(View view) {
        //redirect activity
        redirectActivity(this, UpcomingMeeting.class);
    }

    public void ClickAboutUs(View view) {
        //redirect activity
        redirectActivity(this, AboutUs.class);
    }

    public void ClickProfile(View view) {
        //redirect activity
        redirectActivity(this, Profile.class);
    }

    public void ClickShare(View view) {
        Intent shareintent = new Intent();
        shareintent.setAction(Intent.ACTION_SEND);
        shareintent.putExtra(Intent.EXTRA_TEXT, "https://www.youtube.com/watch?v=WvJBXWiSkTU&t=1085s");
        shareintent.setType("text/plain");
        startActivity(Intent.createChooser(shareintent, "ShareVia"));
    }

    public void ClickLogout(View view) {
        //redirect activity
        logout(this);
    }

    public static void logout(Activity activity) {
        //initialize alert/pop_up dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Logout");
        builder.setMessage("Want to Logout?");

        //yes button
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                activity.finishAffinity();
                redirectActivity(activity, Login.class);
            }
        });

        //no button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //dismiss dialog
                dialog.dismiss();
            }
        });

        //show dialog
        builder.show();
    }

    public static void redirectActivity(Activity activity, Class aClass) {
        //redirecting intent
        Intent intent = new Intent(activity, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    protected void onPause() {
        super.onPause();
        //close drawer
        closeDrawer(drawerLayout);
    }

    public Activity shareActivity() {
        return MainActivity.this;
    }
}