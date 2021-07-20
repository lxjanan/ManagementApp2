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
import android.os.Handler;
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
    private android.widget.Button logout, nzqa, nbts;

    ImageView instagram, facebook;

    BottomNavigationView bottomNavigationView;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance(); // Gets data from firebase
        logout = findViewById(R.id.logoutB);

        instagram = findViewById(R.id.instagramB);
        facebook = findViewById(R.id.facebookB);
        nzqa = findViewById(R.id.nzqaBtn);
        nbts = findViewById(R.id.nbtsBtn);

        nzqa.setOnClickListener(v -> gotoUrl("https://www.nzqa.govt.nz/ncea/subjects/")); //This allows for user to go to these links on button click
        nbts.setOnClickListener(v -> gotoUrl("https://www.nobraintoosmall.co.nz/"));

        instagram.setOnClickListener(v -> gotoUrl("https://www.instagram.com/mountroskillgrammarschool/?hl=en"));
        facebook.setOnClickListener(v -> gotoUrl("https://www.facebook.com/MountRoskillGS/"));

        logout.setOnClickListener(v -> {
            new AlertDialog.Builder(this) //An alertdialog is built so that if user accidentally clicks sign out, they have the option return to the app
                    .setTitle("Sign out")
                    .setMessage("Are you sure you want to sign out?")
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            mAuth.signOut();
                            SharedPreferences preferences1 = getSharedPreferences("checkbox", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences1.edit();
                            editor.putString("remember", "false"); // Removes the stay signed in option so user has to relogin to access the app
                            editor.apply();
                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                Intent intent = new Intent(HomeActivity.this, Login.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                Toast.makeText(HomeActivity.this, "Signed Out", Toast.LENGTH_SHORT).show();
                                finish();
                            },100); // Transition for smoother user experience
                        }
                    }).create().show();
        }); // The logout button allows users to sign out. If user clicks sign out, the app will take them to login page and also if they exit, they will need to sign in again

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.homePage); // Sets the activity that the user is on as selected. This allows user to know which activity they are on

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
                    return true; // Transitions are used to create smooth user experience
            }
            return false;
        }); // The navigation bar is the same for every section however as I am using activity, the activity is selected
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    } // Code for opening the links. The s that is used is the link which I have used above on lines 53 to 57

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Leave app")
                .setMessage("Are you sure you want to exit?") // On back pressed, user will be asked if they want to leave the app.
                .setNegativeButton(android.R.string.no, null) // If they select no, they will return to current activity
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        HomeActivity.super.onBackPressed();
                        finishAffinity(); // If they select yes, then the app will close
                    }
                }).create().show();
    }
}