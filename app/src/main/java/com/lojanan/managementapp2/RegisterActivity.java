package com.lojanan.managementapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

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
                SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences1.edit();
                editor.putString("remember", "false");
                editor.apply(); //This code allows the users to return to the login page
                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    startActivity(new Intent(RegisterActivity.this,Login.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                },100); //This code opens the login activity with a neat fade transition
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
                                Toast.makeText(RegisterActivity.this, "Registered successfully", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, Login.class);
                                startActivity(intent);
                                finish();
                            } else {
                                String error = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Registration failed" + error, Toast.LENGTH_SHORT).show(); }
                            progress.dismiss();
                        }
                    });
                }
            }
        });
    }
}