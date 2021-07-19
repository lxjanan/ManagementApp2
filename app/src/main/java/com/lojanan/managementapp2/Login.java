package com.lojanan.managementapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

public class Login extends AppCompatActivity {

    // We use private as it cannot be overridden
    private EditText mLoginEmail, mLoginPass;
    private Button mLoginBtn;
    private TextView loginRegister, forgotPassword;
    private CheckBox remember;

    private FirebaseAuth fAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Will display the login view xml

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        mLoginEmail = findViewById(R.id.loginE);
        mLoginPass = findViewById(R.id.loginP); // This code allows for the view to be displayed on the user's screen. In this case, the password login will be displayed
        mLoginBtn = findViewById(R.id.loginB);
        loginRegister = findViewById(R.id.loginR);
        remember = findViewById(R.id.rememberMe);
        forgotPassword = findViewById(R.id.forgotP);

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");
        if (checkbox.equals("true")){
            Intent intent = new Intent(Login.this, HomeActivity.class); //Causes user to automatically login
            startActivity(intent);
        }else if (checkbox.equals("false")){
            Toast.makeText(this, "Please sign in", Toast.LENGTH_SHORT).show(); //User has to login when app starts up
        }

        mLoginBtn.setOnClickListener(v -> {
            String email = mLoginEmail.getText().toString().trim();
            String password = mLoginPass.getText().toString().trim(); //Converts user input into string

            if (TextUtils.isEmpty(email)) {
                mLoginEmail.setError("Email required");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                mLoginPass.setError("Password required");
                return;
            }
            if (password.length() < 6) {
                mLoginPass.setError("Password must be more than 6 characters");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        startActivity(new Intent(Login.this, HomeActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    },100);
                }else {
                    Toast.makeText(Login.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        });

        loginRegister.setOnClickListener(v -> {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                startActivity(new Intent(Login.this,RegisterActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            },100);
        });

        remember.setOnCheckedChangeListener((rememberButton, isChecked) -> {
            if (rememberButton.isChecked()) {
                SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE); //Used MODE_PRIVATE so that only this application is able to read the data
                SharedPreferences.Editor editor = preferences1.edit();
                editor.putString("remember", "true");
                editor.apply();
                Toast.makeText(Login.this, "Remember me: ON", Toast.LENGTH_SHORT).show();
            }else if (!rememberButton.isChecked()) {
                SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences1.edit();
                editor.putString("remember", "false");
                editor.apply();
                Toast.makeText(Login.this, "Remember me: OFF", Toast.LENGTH_SHORT).show();
            }
        }); // This code lets the user stay signed in if they check the checkbox

        forgotPassword.setOnClickListener(v -> {
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                startActivity(new Intent(Login.this,PassForgot.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            },100);
        }); // Opens the forgot password activity which will allow users to reset their password
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Leave app")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, (arg0, arg1) -> {
                    SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences1.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Login.super.onBackPressed();
                    finishAffinity();
                }).create().show();
    }
}