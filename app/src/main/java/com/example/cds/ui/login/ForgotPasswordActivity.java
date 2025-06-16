package com.example.cds.ui.login;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cds.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

public class ForgotPasswordActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static final String TAG = "ForgotPasswordActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        Button resetButton = findViewById(R.id.resetPasswordButton);
        EditText emailEditText = findViewById(R.id.emailEditText);

        resetButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            if (email.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this,
                        "Please enter your email",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Password reset email queued - check SPAM folder");
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "If email not received, check your SPAM folder",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            // Handle specific error codes
                            if (task.getException() instanceof FirebaseAuthException) {
                                String errorCode = ((FirebaseAuthException) task.getException()).getErrorCode();
                                Log.e(TAG, "Password reset error: " + errorCode);

                                // You can add specific error handling here if needed
                                // For example:
                                if (errorCode.equals("ERROR_USER_NOT_FOUND")) {
                                    Toast.makeText(ForgotPasswordActivity.this,
                                            "No account found with this email",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ForgotPasswordActivity.this,
                                            "Failed to send reset email: " + errorCode,
                                            Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Failed to send reset email",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}