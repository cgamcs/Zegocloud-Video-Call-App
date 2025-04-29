package com.example.zegovideocall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        TextInputLayout userNameLayout = findViewById(R.id.userNameLayout);
        TextInputLayout mailLayout = findViewById(R.id.mailIdLayout);
        TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);

        TextInputEditText userNameET = findViewById(R.id.userNameET);
        TextInputEditText mailIdET = findViewById(R.id.mailIdEt);
        TextInputEditText passwordET = findViewById(R.id.passwordET);

        MaterialButton login = findViewById(R.id.login);
        MaterialButton signUp = findViewById(R.id.signUp);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersList");

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.requireNonNull(userNameET.getText()).toString().isEmpty()) {
                    userNameLayout.setError("Please enter your user name");
                } else if (Objects.requireNonNull(mailIdET.getText()).toString().isEmpty()) {
                    mailLayout.setError("Please enter your mail ID");
                } else if (Objects.requireNonNull(passwordET.getText()).toString().isEmpty()) {
                    passwordLayout.setError("Please enter your password");
                } else {
                    auth.signInWithEmailAndPassword(mailIdET.getText().toString(), passwordET.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            Toast.makeText(LoginActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Objects.requireNonNull(userNameET.getText()).toString().isEmpty()) {
                    userNameLayout.setError("Please enter your user name");
                } else if (Objects.requireNonNull(mailIdET.getText()).toString().isEmpty()) {
                    mailLayout.setError("Please enter your mail ID");
                } else if (Objects.requireNonNull(passwordET.getText()).toString().isEmpty()) {
                    passwordLayout.setError("Please enter your password");
                } else {
                    auth.createUserWithEmailAndPassword(mailIdET.getText().toString(), passwordET.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            User user = new User();
                            user.setUserName(userNameET.getText().toString());
                            user.setUserID(Objects.requireNonNull(authResult.getUser()).getUid());

                            reference.child(user.getUserID()).setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(LoginActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(LoginActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(LoginActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}