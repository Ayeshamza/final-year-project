package com.example.planandmeet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DocumentsView extends AppCompatActivity {

    FloatingActionButton uploadFab;
    ArrayList<DocumentModel> documentModelList;
    ArrayList<String> documentIdList;
    ArrayList<String> userIDs;

    RecyclerView recyclerView;
    DocumentAdapter documentAdapter;
    Event event;
    DocumentModel documentModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_documents_view);

        ImageButton back = findViewById(R.id.backToMain);
        TextView title = findViewById(R.id.title);
        title.setText(R.string.documentsTitle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocumentsView.this, EventDetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //getting event id
        SharedPreferences sharedPreferences = getSharedPreferences("eventId", MODE_PRIVATE);
        String eventId = sharedPreferences.getString("eventID", "");

        uploadFab = findViewById(R.id.uploadFab);

        uploadFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DocumentsView.this, DocumentUploads.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        recyclerView = findViewById(R.id.recview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        documentModelList = new ArrayList<>();
        documentIdList = new ArrayList<>();
        userIDs = new ArrayList<>();
        event = new Event();
        documentModel = new DocumentModel();

        documentAdapter = new DocumentAdapter(this, documentModelList, documentIdList, eventId, userIDs);
        recyclerView.setAdapter(documentAdapter);

        //getting organizer id
        FirebaseDatabase.getInstance().getReference().child("events").child(eventId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                        userIDs.clear();
                        if (snapshot.exists()) {
                            event = snapshot.getValue(Event.class);
                            userIDs.addAll(event.getUserID());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        //getting documents
        FirebaseDatabase.getInstance().getReference().child("documents").child(eventId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        documentModelList.clear();
                        documentIdList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists()) {

                                documentModel = dataSnapshot.getValue(DocumentModel.class);
                                documentModelList.add(documentModel);

                                String docId = dataSnapshot.getKey();
                                documentIdList.add(docId);
                            }
                        }
                        documentAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DocumentsView.this, EventDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}