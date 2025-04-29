package com.example.zegovideocall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        TextInputLayout userNameLayout = findViewById(R.id.userNameLayout);
        TextInputLayout mailLayout = findViewById(R.id.mailIdLayout);
        TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);
        TextInputLayout confirmPasswordLayout = findViewById(R.id.confirmPasswordLayout);

        TextInputEditText userNameET = findViewById(R.id.userNameET);
        TextInputEditText mailIdET = findViewById(R.id.mailIdEt);
        TextInputEditText passwordET = findViewById(R.id.passwordET);
        TextInputEditText confirmPasswordET = findViewById(R.id.confirmPasswordET);

        MaterialButton signUp = findViewById(R.id.signUp);
        TextView loginLink = findViewById(R.id.loginLink);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersList");

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = Objects.requireNonNull(userNameET.getText()).toString();
                String mailId = Objects.requireNonNull(mailIdET.getText()).toString();
                String password = Objects.requireNonNull(passwordET.getText()).toString();
                String confirmPassword = Objects.requireNonNull(confirmPasswordET.getText()).toString();

                if (userName.isEmpty()) {
                    userNameLayout.setError("Please enter your user name");
                } else if (mailId.isEmpty()) {
                    mailLayout.setError("Please enter your mail ID");
                } else if (password.isEmpty()) {
                    passwordLayout.setError("Please enter your password");
                } else if (confirmPassword.isEmpty()) {
                    confirmPasswordLayout.setError("Please confirm your password");
                } else if (!password.equals(confirmPassword)) {
                    confirmPasswordLayout.setError("Passwords do not match");
                } else {
                    auth.createUserWithEmailAndPassword(mailId, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user = authResult.getUser();
                            if (user != null) {
                                user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(SignUpActivity.this, "Verification email sent!", Toast.LENGTH_SHORT).show();

                                        User newUser = new User();
                                        newUser.setUserName(userName);
                                        newUser.setUserID(user.getUid());
                                        newUser.setEmail(mailId); // Almacenar el correo electrónico

                                        reference.child(newUser.getUserID()).setValue(newUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(SignUpActivity.this, "User created successfully!", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignUpActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(SignUpActivity.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SignUpActivity.this, "Failed to create user", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}