package com.lojanan.managementapp2;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private android.widget.Button logout;

    BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logoutB);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(MainActivity.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.homePage);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.toDoList:
                    startActivity(new Intent(getApplicationContext(), ToDoActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.homePage:
                    return true;
                case R.id.kamarPortal:
                    startActivity(new Intent(getApplicationContext(), KamarPortal.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }

//    public void logOut(View view) {
//        FirebaseAuth.getInstance().signOut();
//        Intent intent = new Intent(MainActivity.this, Login.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        startActivity(intent);
//        finish();
//    }
}