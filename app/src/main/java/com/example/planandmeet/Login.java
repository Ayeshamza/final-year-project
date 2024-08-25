package com.example.planandmeet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Login extends AppCompatActivity {

    EditText inputEmail, enterPassword;
    CheckBox showPass;
    Button loginBtn;
    String emailRegularExp = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
    ProgressDialog progressDialog;

    public static String oldToken, newToken;

    User user = new User();
    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        checkConnection();

        inputEmail = findViewById(R.id.resetMail);
        enterPassword = findViewById(R.id.enterPassword);
        loginBtn = findViewById(R.id.loginBtn);
        showPass = findViewById(R.id.passShow);

        progressDialog = new ProgressDialog(this);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        showPass.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    //show
                    enterPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //hide
                    enterPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    private void performLogin() {
        String email = inputEmail.getText().toString();
        String password = enterPassword.getText().toString();

        if (email.isEmpty()) {
            inputEmail.requestFocus();
            inputEmail.setError("Enter Your Email!");
        } else if (!email.matches(emailRegularExp)) {
            inputEmail.requestFocus();
            inputEmail.setError("Enter Valid Email!");
        } else if (password.isEmpty()) {
            enterPassword.requestFocus();
            enterPassword.setError("Enter Your Password!");
        } else if (password.length() < 8) {
            enterPassword.requestFocus();
            enterPassword.setError("Enter Valid Password");
        } else {
            progressDialog.setMessage("Please Wait...");
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetwork = manager.getActiveNetworkInfo();

                    if (task.isSuccessful()) {

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                        if (user.isEmailVerified()) {
                            progressDialog.dismiss();
                            nextActivity();
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        } else {
                            progressDialog.dismiss();
                            user.sendEmailVerification();
                            Toast.makeText(Login.this, "Check Your Email For Verification", Toast.LENGTH_SHORT).show();
                        }
                    } else if (null == activeNetwork) {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "Please Check your Internet Connection", Toast.LENGTH_LONG).show();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Login.this, "Email and Password Do Not Match", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void nextActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        //for token update
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // fcm token settings for particular user
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (task.isSuccessful()) {
                            newToken = Objects.requireNonNull(task.getResult()).getToken();

                            //getting user token
                            FirebaseDatabase.getInstance().getReference().child("users").child(currentUser)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                            user = snapshot.getValue(User.class);

                                            oldToken = user.getToken();

                                            if (!oldToken.equals(newToken)) {

                                                Map<String, Object> map = new HashMap<>();
                                                map.put("token", newToken);

                                                FirebaseDatabase.getInstance().getReference("users")
                                                        .child(currentUser).updateChildren(map);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                        }
                    }
                });
    }

    public void moveToForgotPassword(View view) {
        Intent intent = new Intent(Login.this, ForgotPassword.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void toSignUp(View view) {
        Intent intent = new Intent(Login.this, Signup.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void checkConnection() {

        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (null == activeNetwork) {
            Toast.makeText(this, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
}