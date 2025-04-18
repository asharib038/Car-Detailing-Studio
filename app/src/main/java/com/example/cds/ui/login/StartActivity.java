package com.example.cds.ui.login;



import com.example.cds.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StartActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Initialize views
        tvWelcome = findViewById(R.id.tvWelcome);

        // Check authentication status
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // User not logged in, redirect to Login
            startActivity(new Intent(this, com.example.cds.ui.login.LoginActivity.class));
            finish();
            return;
        }

        // First try to get name from Firebase Auth profile
        String displayName = currentUser.getDisplayName();
        if (displayName != null && !displayName.isEmpty()) {
            tvWelcome.setText("Welcome, " + displayName + "!");
        } else {
            // Fallback to Realtime Database if Auth profile name is missing
            loadUserData(currentUser.getUid());
        }
    }

    private void loadUserData(String userId) {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String name = snapshot.child("name").getValue(String.class);
                            tvWelcome.setText("Welcome, " + (name != null ? name : "User") + "!");
                        } else {
                            tvWelcome.setText("Welcome!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tvWelcome.setText("Welcome! " + "!");
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_profile) {
            startActivity(new Intent(this, com.example.cds.ui.login.ProfileActivity.class));
            return true;
        } else if (item.getItemId() == R.id.menu_logout) {
            mAuth.signOut();
            startActivity(new Intent(this, com.example.cds.ui.login.LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Double-check authentication when activity resumes
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, com.example.cds.ui.login.LoginActivity.class));
            finish();
        }
    }
}