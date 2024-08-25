package com.example.planandmeet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Signup extends AppCompatActivity {

    EditText inputName, inputEmail, enterPassword, enterConfirmPassword;
    Button signUpBtn;
    String emailRegularExp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    ProgressDialog progressDialog;
    public static String token = "";

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        inputName = findViewById(R.id.inputName);
        inputEmail = findViewById(R.id.resetMail);
        enterPassword = findViewById(R.id.enterPassword);
        enterConfirmPassword = findViewById(R.id.enterConfirmPassword);
        signUpBtn = findViewById(R.id.signUpBtn);
        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performAuth();
            }
        });
    }

    private void performAuth() {
        String name = inputName.getText().toString();
        String email = inputEmail.getText().toString();
        String password = enterPassword.getText().toString();
        String confirmPassword = enterConfirmPassword.getText().toString();

        if (name.isEmpty()) {
            inputName.requestFocus();
            inputName.setError("Enter Your Name!");
        } else if (email.isEmpty()) {
            inputName.requestFocus();
            inputName.setError("Enter Your Email!");
        } else if (!email.matches(emailRegularExp)) {
            inputEmail.requestFocus();
            inputEmail.setError("Enter Valid Email Address!");
        } else if (password.isEmpty()) {
            enterPassword.requestFocus();
            enterPassword.setError("Enter a Password");
        } else if (password.length() < 8) {
            enterPassword.requestFocus();
            enterPassword.setError("Password length must be greater than 7 characters!");
        } else if (confirmPassword.isEmpty()) {
            enterConfirmPassword.requestFocus();
            enterConfirmPassword.setError("Confirm Your Password");
        } else if (!password.equals(confirmPassword)) {
            enterConfirmPassword.requestFocus();
            enterConfirmPassword.setError("Password does not match!");
        } else {
            progressDialog.setMessage("Please Wait...");
            progressDialog.setTitle("Sign-Up");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        User user = new User();

                        user.setName(name);
                        user.setEmail(email);
                        user.setToken(token);

                        //sending data to database
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

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
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .updateChildren(map);
                                                    }
                                                }
                                            });

                                    progressDialog.dismiss();
                                    nextActivity();
                                    Toast.makeText(Signup.this, "Sign-Up Successful", Toast.LENGTH_SHORT).show();
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(Signup.this, "Sign-Up Failed!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Signup.this, "Email Already Exists!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void nextActivity() {
        Intent intent = new Intent(Signup.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Signup.this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}