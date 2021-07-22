package com.lojanan.managementapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class PassForgot extends AppCompatActivity {

    private ImageView retLogin;
    private EditText emailText;
    private Button resetPass;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_forgot);

        emailText = findViewById(R.id.resetE);
        resetPass = findViewById(R.id.resetB);

        mAuth = FirebaseAuth.getInstance();
        resetPass.setOnClickListener(v -> resetPassword());

        retLogin = findViewById(R.id.returnB);
        retLogin.setOnClickListener(v -> {
            SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences1.edit();
            editor.putString("remember", "false"); // Unchecks checkbox to allow for safe return to login
            editor.apply();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                startActivity(new Intent(PassForgot.this,Login.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            },100); // Redirects user to login page with fade transition
        });
    }

    private void resetPassword() {
        String email = emailText.getText().toString().trim();
        if (email.isEmpty()){
            emailText.setError("Enter email");
            emailText.requestFocus(); // If email section is empty, error is shown
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Please provide a valid email");
            emailText.requestFocus(); // If email doesn't match any email within Firebase, error is shown
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                Toast.makeText(PassForgot.this, "Check email to reset password", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(PassForgot.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }); // Sends password reset email if successful
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Leave app")
                .setMessage("Are you sure you want to exit?") // Asks user if they wish to leave the app
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                    SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences1.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    PassForgot.super.onBackPressed();
                    finishAffinity(); // Closes app if user selects yes
                }).create().show();
    }
}