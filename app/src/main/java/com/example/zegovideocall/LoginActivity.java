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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("usersList");

        TextInputLayout userNameLayout = findViewById(R.id.userNameLayout);
        TextInputLayout passwordLayout = findViewById(R.id.passwordLayout);

        TextInputEditText userNameET = findViewById(R.id.userNameET);
        TextInputEditText passwordET = findViewById(R.id.passwordET);

        MaterialButton login = findViewById(R.id.login);
        TextView signUpLink = findViewById(R.id.signUpLink);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = Objects.requireNonNull(userNameET.getText()).toString();
                String password = Objects.requireNonNull(passwordET.getText()).toString();

                if (userName.isEmpty()) {
                    userNameLayout.setError("Please enter your user name");
                } else if (password.isEmpty()) {
                    passwordLayout.setError("Please enter your password");
                } else {
                    reference.orderByChild("userName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                                    User user = userSnapshot.getValue(User.class);
                                    if (user != null) {
                                        String email = user.getEmail();
                                        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                            @Override
                                            public void onSuccess(AuthResult authResult) {
                                                FirebaseUser firebaseUser = auth.getCurrentUser();
                                                if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                                                    Toast.makeText(LoginActivity.this, "Logged in successfully!", Toast.LENGTH_SHORT).show();

                                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Please verify your email address", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(LoginActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(LoginActivity.this, "Failed to login", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        signUpLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}