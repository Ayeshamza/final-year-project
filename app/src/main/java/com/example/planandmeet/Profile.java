package com.example.planandmeet;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    DatabaseReference reference;
    String userId, name, email;
    int profileFlag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ImageButton back = findViewById(R.id.backToMain);
        TextView title = findViewById(R.id.title);
        title.setText(R.string.profileTitle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Profile.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        EditText nameEditText = findViewById(R.id.name);
        TextView emailTextView = findViewById(R.id.email);
        EditText oldPass = findViewById(R.id.oldPass);
        EditText newPass = findViewById(R.id.newPass);
        EditText confirmPass = findViewById(R.id.confirmPass);
        Button updatePass = findViewById(R.id.updatePass);
        TextView changePass = findViewById(R.id.changePass);

        profileFlag = 0;
        oldPass.setVisibility(View.GONE);
        newPass.setVisibility(View.GONE);
        confirmPass.setVisibility(View.GONE);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference("users").child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userProfile = snapshot.getValue(User.class);

                        if (userProfile != null) {
                            name = userProfile.getName();
                            email = userProfile.getEmail();

                            nameEditText.setText(name);
                            emailTextView.setText(email);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Profile.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                    }
                });

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profileFlag = 1;
                oldPass.setVisibility(View.VISIBLE);
                newPass.setVisibility(View.VISIBLE);
                confirmPass.setVisibility(View.VISIBLE);
            }
        });

        updatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userName = nameEditText.getText().toString();
                String oldpass = oldPass.getText().toString();
                String newpass = newPass.getText().toString();
                String confirmpass = confirmPass.getText().toString();

                if (userName.isEmpty()) {
                    nameEditText.requestFocus();
                    nameEditText.setError("Enter Name!");
                } else {
                    if (profileFlag == 1) {
                        if (oldpass.isEmpty()) {
                            oldPass.requestFocus();
                            oldPass.setError("Enter Old Password!");
                        } else if (newpass.isEmpty()) {
                            newPass.requestFocus();
                            newPass.setError("Enter New Password!");
                        } else if (confirmpass.isEmpty()) {
                            confirmPass.requestFocus();
                            confirmPass.setError("Enter New Password!");
                        } else if (newpass.length() < 8) {
                            newPass.requestFocus();
                            newPass.setError("Password length must be greater than 7 characters!");
                        } else if (!newpass.equals(confirmpass)) {
                            confirmPass.requestFocus();
                            confirmPass.setError("Password does not match!");
                        } else {
                            FirebaseUser currentUser;
                            currentUser = FirebaseAuth.getInstance().getCurrentUser();
                            String email = currentUser.getEmail();

                            AuthCredential credential = EmailAuthProvider.getCredential(email, oldpass);

                            currentUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        currentUser.updatePassword(String.valueOf(newpass)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(Profile.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("name", userName);

                                                    FirebaseDatabase.getInstance().getReference().child("users").child(userId).updateChildren(map);

                                                    Toast.makeText(Profile.this, "Profile Updated successfully!", Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(Profile.this, MainActivity.class);
                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                }
                                            }
                                        });
                                    } else {
                                        Toast.makeText(Profile.this, "Old Password Do Not Match!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    } else {
                        Map<String, Object> map = new HashMap<>();
                        map.put("name", userName);

                        FirebaseDatabase.getInstance().getReference().child("users").child(userId).updateChildren(map);

                        Toast.makeText(Profile.this, "Name Updated Successfully!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(Profile.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Profile.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}