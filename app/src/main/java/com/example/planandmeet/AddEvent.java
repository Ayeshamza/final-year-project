
package com.example.planandmeet;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class AddEvent extends AppCompatActivity {

    private EditText EventName, StartDate;
    TextView selectedMode;
    Button CreateEvent;
    ArrayList<String> userIDList;

    final Calendar calendar = Calendar.getInstance();
    DatePickerDialog datePicker;

    DatabaseReference reference;

    Event event;
    String userId;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        ImageButton back = findViewById(R.id.backToMain);
        TextView title = findViewById(R.id.title);
        title.setText(R.string.createEventTitle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddEvent.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        datePicker = new DatePickerDialog(AddEvent.this);

        event = new Event();

        reference = FirebaseDatabase.getInstance().getReference().child("events");

        userIDList = new ArrayList<>();

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        userIDList.add(userId);

        Spinner spinner = findViewById(R.id.eventMode);
        EventName = findViewById(R.id.eventName);
        StartDate = findViewById(R.id.startDate);
        selectedMode = findViewById(R.id.selectedMode);
        CreateEvent = findViewById(R.id.createEventBtn);

        //start date
        StartDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                StartDate.setError(null);

                datePicker = new DatePickerDialog(AddEvent.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(android.widget.DatePicker view, int year, int month, int dayOfMonth) {
                        // adding the selected date in the edittext
                        StartDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);

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

        ArrayAdapter<String> myAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, EventMode);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(myAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMode.setText(parent.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        CreateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String eName = EventName.getText().toString();
                String stDate = StartDate.getText().toString();
                String mode = selectedMode.getText().toString();

                if (eName.isEmpty()) {
                    EventName.requestFocus();
                    EventName.setError("Enter Event Name!");
                } else if (stDate.isEmpty()) {
                    StartDate.requestFocus();
                    StartDate.setError("Enter Starting Date!");
                } else {
                    event.setMode(mode);
                    event.setName(eName);
                    event.setStartDate(stDate);
                    event.setEndDate(generateEndDate(stDate));
                    event.setUserID(userIDList);
                    event.setMeetingOccurence("Not Set Yet");
                    event.setCreationDate(new Date().getTime());
                    event.setVenue("Meeting Place Not Set Yet");

                    reference.push().setValue(event);

                    Toast.makeText(AddEvent.this, "Event Created Successfully for Next 7 Days!", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(AddEvent.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    private String generateEndDate(String startDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();

        try {
            c.setTime(sdf.parse(startDate));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        c.add(Calendar.DATE, 6);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE

        return sdf.format(c.getTime());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(AddEvent.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}