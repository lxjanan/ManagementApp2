package com.lojanan.managementapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class PassForgot extends AppCompatActivity {

    private ImageView retLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pass_forgot);

        retLogin = findViewById(R.id.returnB);
        retLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PassForgot.this,Login.class);
                startActivity(intent);
            }
        });
    }
}