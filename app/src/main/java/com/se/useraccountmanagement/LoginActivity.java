package com.se.useraccountmanagement;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int RC_GOOGLE_SIGN_IN = 9001;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private TextInputEditText emailEditText, passwordEditText;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        initializeViews();

        // Configure Google Sign In
        configureGoogleSignIn();

        // Set click listeners
        setupClickListeners();
    }

    private void initializeViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        MaterialButton loginButton = findViewById(R.id.loginButton);
        SignInButton googleSignInButton = findViewById(R.id.googleSignInButton);
        TextView forgotPasswordText = findViewById(R.id.forgotPasswordText);
        MaterialButton signUpButton = findViewById(R.id.signUpButton);
        progressBar = findViewById(R.id.progressBar);

        // Configure Google Sign-In button
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);
        googleSignInButton.setColorScheme(SignInButton.COLOR_DARK);
    }

    private void configureGoogleSignIn() {
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .requestProfile()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        } catch (Exception e) {
            Log.e(TAG, "Google Sign-In configuration failed", e);
            Toast.makeText(this, "Google Sign-In configuration error", Toast.LENGTH_LONG).show();
        }
    }

    private void setupClickListeners() {
        findViewById(R.id.loginButton).setOnClickListener(v -> loginWithEmail());
        findViewById(R.id.googleSignInButton).setOnClickListener(v -> signInWithGoogle());
        findViewById(R.id.forgotPasswordText).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
        });
        findViewById(R.id.signUpButton).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void loginWithEmail() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty()) {
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Please enter a valid email");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            return;
        }

        if (password.length() < 6) {
            passwordEditText.setError("Password must be at least 6 characters");
            passwordEditText.requestFocus();
            return;
        }

        showProgress(true);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        navigateToMainActivity();
                    } else {
                        handleLoginError(task.getException());
                    }
                });
    }

    private void signInWithGoogle() {
        showProgress(true);
        try {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
        } catch (Exception e) {
            showProgress(false);
            Toast.makeText(this, "Google Sign-In failed to start", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Google Sign-In initiation error", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null && account.getIdToken() != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                } else {
                    showProgress(false);
                    Toast.makeText(this, "Google sign-in failed: No account data", Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                showProgress(false);
                handleGoogleSignInError(e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    showProgress(false);
                    if (task.isSuccessful()) {
                        navigateToMainActivity();
                    } else {
                        handleFirebaseAuthError(task.getException());
                    }
                });
    }

    private void handleLoginError(Exception exception) {
        try {
            throw exception;
        } catch (FirebaseAuthInvalidUserException e) {
            emailEditText.setError("No account found with this email");
            emailEditText.requestFocus();
        } catch (FirebaseAuthInvalidCredentialsException e) {
            passwordEditText.setError("Invalid password");
            passwordEditText.requestFocus();
        } catch (Exception e) {
            Toast.makeText(LoginActivity.this,
                    "Login failed: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Login error", e);
        }
    }

    private void handleGoogleSignInError(ApiException e) {
        String errorMessage = "Google sign-in failed";
        switch (e.getStatusCode()) {
            case 10:  // DEVELOPER_ERROR
                errorMessage = "Configuration error. Contact support.";
                break;
            case 12501: // SIGN_IN_CANCELLED
                return;  // User canceled, don't show error
            case 7:     // NETWORK_ERROR
                errorMessage = "Network error. Check your connection.";
                break;
            case 12502: // INTERNAL_ERROR
                errorMessage = "Internal error. Try again later.";
                break;
        }
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Log.e(TAG, "Google sign-in error: " + e.getStatusCode(), e);
    }

    private void handleFirebaseAuthError(Exception exception) {
        String error = exception != null ? exception.getMessage() : "Unknown error";

        if (error.contains("already linked")) {
            Toast.makeText(this,
                    "This email is already registered with another method",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,
                    "Authentication failed: " + error,
                    Toast.LENGTH_SHORT).show();
        }
        Log.e(TAG, "Firebase auth error", exception);
    }

    private void navigateToMainActivity() {
        Toast.makeText(this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showProgress(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        findViewById(R.id.loginButton).setEnabled(!show);
        findViewById(R.id.googleSignInButton).setEnabled(!show);
        findViewById(R.id.signUpButton).setEnabled(!show);
        findViewById(R.id.forgotPasswordText).setEnabled(!show);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already signed in
        if (mAuth.getCurrentUser() != null) {
            navigateToMainActivity();
        }
    }
}