package com.lojanan.managementapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class KamarPortal extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kamar_portal);

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.kamarPortal);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.toDoList:
                    startActivity(new Intent(getApplicationContext(), ToDoActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.homePage:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    overridePendingTransition(0,0);
                    return true;
                case R.id.kamarPortal:
                    return true;
            }
            return false;
        });
    }
}