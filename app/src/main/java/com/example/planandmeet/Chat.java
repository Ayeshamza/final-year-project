package com.example.planandmeet;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;

public class Chat extends AppCompatActivity {

    private ImageButton sendMessageButton;
    private EditText messageInput;
    private String currentUserID;

    RecyclerView recyclerView;
    DatabaseReference chatReference;
    ChatAdapter chatAdapter;

    ChatModel chatModel;
    ArrayList<ChatModel> chatModelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ImageButton back = findViewById(R.id.backToMain);
        TextView title = findViewById(R.id.title);
        title.setText(R.string.chatTitle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Chat.this, EventDetails.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        chatModel = new ChatModel();

        sendMessageButton = findViewById(R.id.sendMessage);
        messageInput = findViewById(R.id.inputMessage);

        //getting event id
        SharedPreferences sharedPreferences = getSharedPreferences("eventId", MODE_PRIVATE);
        String eventId = sharedPreferences.getString("eventID", "");

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatReference = FirebaseDatabase.getInstance().getReference().child("chats").child(eventId);

        //sending messages
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageInput.getText().toString().trim();

                if (!message.matches("")) {
                    chatModel.setMessage(message);
                    chatModel.setSenderID(currentUserID);
                    chatModel.setTimestamp(new Date().getTime());

                    chatReference.push().setValue(chatModel);
                }
                messageInput.setText("");
            }
        });

        recyclerView = findViewById(R.id.chatView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        chatModelList = new ArrayList<>();

        chatAdapter = new ChatAdapter(this, chatModelList);
        recyclerView.setAdapter(chatAdapter);

        chatReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatModelList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.exists()) {

                        chatModel = dataSnapshot.getValue(ChatModel.class);
                        chatModelList.add(chatModel);
                    }
                }
                chatAdapter.notifyDataSetChanged();

                //setting chat view to the end
                if (recyclerView.getAdapter().getItemCount() != 0) {
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Chat.this, EventDetails.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}