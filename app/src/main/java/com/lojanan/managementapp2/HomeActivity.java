package com.lojanan.managementapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private android.widget.Button logout;

    ImageView instagram, facebook;

    BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        logout = findViewById(R.id.logoutB);

        instagram = findViewById(R.id.instagramB);
        facebook = findViewById(R.id.facebookB);

        instagram.setOnClickListener(v -> gotoUrl("https://www.instagram.com/mountroskillgrammarschool/?hl=en"));
        facebook.setOnClickListener(v -> gotoUrl("https://www.facebook.com/MountRoskillGS/"));

        logout.setOnClickListener(v -> {
            mAuth.signOut();
            SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences1.edit();
            editor.putString("remember", "false");
            editor.apply();
            Intent intent = new Intent(HomeActivity.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Toast.makeText(HomeActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
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
                    startActivity(new Intent(getApplicationContext(), PortalActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Leave app")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        HomeActivity.super.onBackPressed();
                        finishAffinity();
                    }
                }).create().show();
    }
}