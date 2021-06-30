package com.lojanan.managementapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    private EditText RegisterEmail, RegisterPass;
    private Button RegisterButton;
    private TextView RegisterReturn;
    private FirebaseAuth mAuth;

    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        progress = new ProgressDialog(this);

        RegisterEmail = findViewById(R.id.registerE);
        RegisterPass = findViewById(R.id.registerP);
        RegisterButton = findViewById(R.id.registerB);
        RegisterReturn = findViewById(R.id.registerR);

        RegisterReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    startActivity(new Intent(RegisterActivity.this,Login.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                },100);
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = RegisterEmail.getText().toString().trim();
                String password = RegisterPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)){
                    RegisterEmail.setError("Please enter an email address");
                    return;
                }
                if (TextUtils.isEmpty(password)){
                    RegisterPass.setError("Please enter a password");
                    return;
                } else {
                    progress.setMessage("Registration in progress");
                    progress.setCanceledOnTouchOutside(false);
                    progress.show();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<AuthResult> task) {

                            if (task.isSuccessful()){
                                Intent intent = new Intent(RegisterActivity.this, ToDoActivity.class);
                                startActivity(intent);
                                finish();
                                progress.dismiss();
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Registration failed" + error, Toast.LENGTH_SHORT).show();
                                progress.dismiss();
                            }
                        }
                    });
                }
            }
        });
    }
}