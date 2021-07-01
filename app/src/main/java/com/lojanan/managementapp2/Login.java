package com.lojanan.managementapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
    private TextView loginRegister;
    private CheckBox remember;

    private FirebaseAuth fAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        mLoginEmail = findViewById(R.id.loginE);
        mLoginPass = findViewById(R.id.loginP); // This code finds the view for the method
        mLoginBtn = findViewById(R.id.loginB);
        loginRegister = findViewById(R.id.loginR);
        remember = findViewById(R.id.rememberMe);

        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
        String checkbox = preferences.getString("remember", "");
        if (checkbox.equals("true")){
            Intent intent = new Intent(Login.this, ToDoActivity.class);
            startActivity(intent);
        }else if (checkbox.equals("false")){
            Toast.makeText(this, "Please sign in", Toast.LENGTH_SHORT).show();
        }

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mLoginEmail.getText().toString().trim();
                String password = mLoginPass.getText().toString().trim();

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

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ToDoActivity.class));
                        }else {
                            Toast.makeText(Login.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        loginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    startActivity(new Intent(Login.this,RegisterActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                },100);
            }
        });

        remember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton rememberButton, boolean isChecked) {
                if (rememberButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE); //Used MODE_PRIVATE so that only this application is able to read the data
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                    Toast.makeText(Login.this, "Remember me: ON", Toast.LENGTH_SHORT).show();
                }else if (!rememberButton.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(Login.this, "Remember me: OFF", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}