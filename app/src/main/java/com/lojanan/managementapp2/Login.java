package com.lojanan.managementapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private EditText loginEmail, loginPass;
    private Button loginButton;
    private TextView loginRegister;

    private FirebaseAuth mAuth;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        loginEmail = findViewById(R.id.loginE);
        loginPass = findViewById(R.id.loginP); // This code finds the view for the method
        loginButton = findViewById(R.id.loginB);
        loginRegister = findViewById(R.id.loginR);

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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    loginEmail.setError("Email required");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    loginPass.setError("Password required");
                    return;
                } else {
                    progress.setMessage("Login in progress");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();

                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Intent intent = new Intent(Login.this, ToDoActivity.class);
                                startActivity(intent);
                                finish();
                                progress.dismiss();
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(Login.this, "Login failed", Toast.LENGTH_SHORT).show();
                                progress.dismiss();
                            }
                        }
                    });
                }
            }
        });

    }
}