package com.lojanan.managementapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        retLogin = findViewById(R.id.returnB);
        retLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PassForgot.this,Login.class);
                startActivity(intent);
            }
        });
    }

    private void resetPassword() {
        String email = emailText.getText().toString().trim();
        if (email.isEmpty()){
            emailText.setError("Enter email");
            emailText.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Please provide a valid email");
            emailText.requestFocus();
            return;
        }

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(PassForgot.this, "Check email to reset password", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PassForgot.this, "Something went wrong", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}