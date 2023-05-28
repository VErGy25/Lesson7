package ru.mirea.makhorkin.firebaseauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import ru.mirea.makhorkin.firebaseauth.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity{
    private static final String TAG = MainActivity.class.getSimpleName();
    private ActivityMainBinding binding;
    // START declare_auth
    private FirebaseAuth mAuth;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
// Initialization views
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
// [START initialize_auth] Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
// [END initialize_auth]
        binding.btnCreateAccount.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (binding.btnCreateAccount.getText().toString() == getString(R.string.create_account)){
                    createAccount(binding.editEmail.getText().toString(), binding.editPassword.getText().toString());
                } else {
                    sendEmailVerification();
                }

            }
        });
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(binding.btnSignIn.getText().toString() == getString(R.string.sign_out)){
                    signOut();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
// Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser user = mAuth.getCurrentUser();
        updateUI(user);
    }

    // [END on_start_check_user]
    private void updateUI(FirebaseUser user) {
        if (user != null) {

            binding.statusTextView.setText(getString(R.string.emailpassword_status_fmt, user.getEmail(), user.isEmailVerified()));
            binding.detailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
            binding.editEmail.setVisibility(View.GONE);
            binding.editPassword.setVisibility(View.GONE);
            binding.btnSignIn.setText(getString(R.string.sign_out));
            binding.btnSignIn.setVisibility(View.VISIBLE);
            binding.btnCreateAccount.setText(getText(R.string.verify_email));
        } else {
            binding.btnSignIn.setText(getString(R.string.sign_in));
            binding.btnCreateAccount.setText(getText(R.string.create_account));
            binding.statusTextView.setText(R.string.signed_out);
            binding.detailTextView.setText(null);
            binding.editEmail.setVisibility(View.VISIBLE);
            binding.editPassword.setVisibility(View.VISIBLE);
        }
    }

    // [START on_start_check_user]
    private void sendEmailVerification() {
        // Disable button
        binding.btnCreateAccount.setEnabled(false);
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        Objects.requireNonNull(user).sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {

                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // [START_EXCLUDE]
                        // Re-enable button
                        binding.btnCreateAccount.setEnabled(true);
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Verification email sent to" + user.getEmail(), Toast.LENGTH_LONG).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification" + task.getException());
                            Toast.makeText(MainActivity.this, "Failed to send Verification email", Toast.LENGTH_LONG).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if(! validateForm(email)){
            Log.d(TAG, "Invalid email form");
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "createAccount is successful");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            Log.w(TAG, "createAccount is fail", task.getException());
                            Toast.makeText(MainActivity.this, "account creation failed", Toast.LENGTH_LONG).show();
                            updateUI(null);

                        }
                    }
                });
    }

    private boolean validateForm(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

}