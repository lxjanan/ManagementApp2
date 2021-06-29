package com.lojanan.managementapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Login extends AppCompatActivity {

    // We use private as it cannot be overridden
    private EditText loginEmail, loginPass;
    private Button loginButton;
    private TextView loginRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.loginE);
        loginPass = findViewById(R.id.loginP); // This code finds the view for the method
        loginButton = findViewById(R.id.loginB);
        loginRegister = findViewById(R.id.loginR);

        loginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}