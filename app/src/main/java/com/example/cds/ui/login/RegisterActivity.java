package com.example.cds.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cds.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etName;
    private Button btnRegister;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etName = findViewById(R.id.etName);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);

        // Add real-time validation
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                        etEmail.setError("Enter a valid email");
                    } else {
                        etEmail.setError(null);
                    }
                }
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    if (s.length() < 6) {
                        etPassword.setError("Password must be ≥6 characters");
                    } else if (!s.toString().matches("[a-zA-Z0-9]+")) {
                        etPassword.setError("Only letters and numbers allowed");
                    } else {
                        etPassword.setError(null);
                    }
                }
            }
        });

        btnRegister.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String name = etName.getText().toString().trim();

        // Reset errors
        etName.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);

        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter a valid email");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be ≥6 characters");
            etPassword.requestFocus();
            return;
        }

        if (!password.matches("[a-zA-Z0-9]+")) {
            etPassword.setError("Only letters and numbers allowed");
            etPassword.requestFocus();
            return;
        }

        btnRegister.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Update user profile with display name
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(profileTask -> {
                                        if (profileTask.isSuccessful()) {
                                            // Save to Realtime Database
                                            saveUserToDatabase(user.getUid(), name, email);
                                            Toast.makeText(RegisterActivity.this,
                                                    "Registration successful!",
                                                    Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(com.example.cds.ui.login.RegisterActivity.this, com.example.cds.ui.login.StartActivity.class));
                                            finish();
                                        } else {
                                            btnRegister.setEnabled(true);
                                            progressBar.setVisibility(View.GONE);
                                            Toast.makeText(RegisterActivity.this,
                                                    "Failed to set display name: " + profileTask.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthUserCollisionException e) {
                            etEmail.setError("This email is already registered");
                            etEmail.requestFocus();
                            Toast.makeText(RegisterActivity.this, "Email already in use. Please login instead.",
                                    Toast.LENGTH_LONG).show();
                        } catch (FirebaseAuthWeakPasswordException e) {
                            etPassword.setError("Password is too weak");
                            etPassword.requestFocus();
                            Toast.makeText(RegisterActivity.this, "Please choose a stronger password",
                                    Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        btnRegister.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void saveUserToDatabase(String userId, String name, String email) {
        User user = new User(name, email);
        mDatabase.child("users").child(userId).setValue(user)
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(this,
                                "Failed to save user data: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public static class User {
        public String name, email;

        public User() {
            // Required for Firebase
        }

        public User(String name, String email) {
            this.name = name;
            this.email = email;
        }
    }
}