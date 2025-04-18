package com.example.cds.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cds.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    // UI elements
    private TextView tvName, tvEmail;

    // Firebase instances
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Database
        // Option 1: For default database
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Option 2: If using a specific database URL (replace with yours)
        // FirebaseDatabase database = FirebaseDatabase.getInstance("https://your-project-id.firebaseio.com/");
        // mDatabase = database.getReference();

        // Initialize views
        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);

        loadUserProfile();
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Set email from Auth
            tvEmail.setText(currentUser.getEmail());

            // Get user details from Realtime Database
            mDatabase.child("users").child(currentUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String name = dataSnapshot.child("name").getValue(String.class);
                                tvName.setText(name != null ? name : "No name set");
                            } else {
                                tvName.setText("Name not found");
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(ProfileActivity.this,
                                    "Database error: " + databaseError.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // User not logged in, redirect to login
            startActivity(new Intent(this, com.example.cds.ui.login.LoginActivity.class));
            finish();
        }
    }
}