package com.lojanan.managementapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class ToDoActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    BottomNavigationView bottomNavigationView;

    private DatabaseReference reference;

    private ProgressDialog loader;

    private String key = "";
    private String task;
    private String description;
    private String date;

    DatePickerDialog.OnDateSetListener setListener;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        FirebaseAuth mAuth = FirebaseAuth.getInstance(); // User data is gained

        recyclerView = findViewById(R.id.recyclerV);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm); // sets size of the LinearLayout on the ToDoActivity

        FirebaseUser mUser = mAuth.getCurrentUser();
        assert mUser != null;
        String userID = mUser.getUid(); // Gets user ID so that their data is presented
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(userID); // Gets user's previously saved data from Firebase

        loader = new ProgressDialog(this);

        FloatingActionButton fab = findViewById(R.id.floatBtn);
        fab.setOnClickListener(v -> addTask()); // When the floatingactionbutton is tapped, it will start the addTask() process

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.toDoList);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.toDoList:
                    return true; // Doesn't change activity
                case R.id.homePage:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class)); // Changes activity
                    overridePendingTransition(0,0);
                    return true;
                case R.id.kamarPortal:
                    startActivity(new Intent(getApplicationContext(), PortalActivity.class));
                    overridePendingTransition(0,0);
                    return true;
            }
            return false;
        });
    }

    private void addTask() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View myView = inflater.inflate(R.layout.addtask_file, null);
        alertDialog.setView(myView); // Sets the addtask_file xml as the view to be presented

        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //This code removes the white part of the alertdialog
        dialog.setCancelable(false);

        final EditText task = myView.findViewById(R.id.task);
        final EditText description = myView.findViewById(R.id.description);
        final EditText date = myView.findViewById(R.id.date); // This is where user can input

        Calendar calendar = Calendar.getInstance(); // Gets Calendar
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH); // Gets Year, Month & Day

        date.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(alertDialog.getContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    setListener,year,month,day);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        }); // When the edit text for the date is selected, the date picker dialog will open allowing users to select a date

        setListener = (view, year1, month1, dayOfMonth) -> {
            month1 = month1 +1; // Added 1 to the month as there is no month with 0. January = 1, February = 2 and so on
            String mDate = dayOfMonth+"/"+ month1 +"/"+ year1; // This is the string text for how the date will look
            date.setText(mDate); // This will be what user selects from date picker
        }; // The setListener sets the editText's text value as the user's selected date from the DatePickerDialog

        Button save = myView.findViewById(R.id.saveBtn);
        Button cancel = myView.findViewById(R.id.cancelBtn);

        cancel.setOnClickListener(v -> dialog.dismiss()); // When the cancel button is pressed, the Add Task Alertdialog will disappear
        save.setOnClickListener(v -> {
            String mTask = task.getText().toString().trim(); // Collects user input and converts to string
            String mDescription = description.getText().toString().trim();
            String id = reference.push().getKey();
            String mDate = date.getText().toString().trim();

            if (TextUtils.isEmpty(mTask)) {
                task.setError("Task required");
                return; //If user doesn't add a task title, it will ask them to write one
            }
            if (TextUtils.isEmpty(mDescription)){
                description.setError("Description required");
                return; //If user doesn't add description for their task, this code sets an error message to let them know
            }
            if (TextUtils.isEmpty(mDate)){
                Toast.makeText(alertDialog.getContext(), "Please select a date", Toast.LENGTH_SHORT).show();
                return; //If a date isn't selected, the app will notify the user with a Toast message
            } else {
                loader.setMessage("Task is being added");
                loader.setCanceledOnTouchOutside(false);
                loader.show(); // Shows loader to let user know that task is being added

                Model model = new Model(mTask, mDescription, id, mDate);
                assert id != null;
                reference.child(id).setValue(model).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Toast.makeText(ToDoActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        String error = Objects.requireNonNull(task1.getException()).toString();
                        Toast.makeText(ToDoActivity.this,"Failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                    loader.dismiss();
                }); //If user has added in a task, description and date, then a Toast message will be displayed to inform users if the addition was successful or not
            }
            dialog.dismiss(); //The dialog will be dismissed returning the users to the ToDoActivity
        });

        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Model> options = new FirebaseRecyclerOptions.Builder<Model>()
                .setQuery(reference, Model.class)
                .build();

        FirebaseRecyclerAdapter<Model, MyViewHolder> adapter = new FirebaseRecyclerAdapter<Model, MyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MyViewHolder holder, final int position, @NonNull Model model) {
                holder.setDate(model.getDate());
                holder.setTask(model.getTask());
                holder.setDesc(model.getDescription()); //Sets the task, description & date on the To-Do List in a cardview

                holder.mView.setOnClickListener(v -> {
                    key = getRef(position).getKey();
                    task = model.getTask();
                    description = model.getDescription();
                    date = model.getDate();

                    updateTask();
                }); //Collects data from Firebase regarding added task, description and date
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layout, parent, false); // The retrieved_layout is the xml file the tasks would be displayed as
                view.getLayoutParams().height = parent.getMeasuredHeight() / 4; //This code is added so that the ViewHolder doesn't take up an entire page
                return new MyViewHolder(view);
            }
        }; //Creates a ViewHolder that will be displayed on the To Do Activity

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mView = itemView;
        }
        public void setTask(String task){
            TextView taskTextView = mView.findViewById(R.id.taskView);
            taskTextView.setText(task); // Sets the user input for task
        }
        public void setDesc(String desc){
            TextView descTextView = mView.findViewById(R.id.descriptionView);
            descTextView.setText(desc); // Sets the user input for description
        }
        public void setDate(String date){
            TextView dateTextView = mView.findViewById(R.id.dateView);
            dateTextView.setText(date); // Sets the user input for date
        }
    } //Adds the data onto the recyclerview in the To Do xml file
    private void updateTask(){
        AlertDialog.Builder dialogTask = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_task, null);
        dialogTask.setView(view);

        AlertDialog dialog = dialogTask.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText mTask = view.findViewById(R.id.mEditTask);
        EditText mDesc = view.findViewById(R.id.mEditDescription);

        mTask.setText(task);
        mTask.setSelection(task.length());

        mDesc.setText(description);
        mDesc.setSelection(description.length());

        Button deleteB = view.findViewById(R.id.deleteBtn);
        Button updateB = view.findViewById(R.id.updateBtn);

        updateB.setOnClickListener(v1 -> {
            task = mTask.getText().toString().trim();
            description = mDesc.getText().toString().trim(); // Converts the input into string to be stored in database

            Model model = new Model(task, description, key, date);
            reference.child(key).setValue(model).addOnCompleteListener(task -> {

                if (task.isSuccessful()){
                    Toast.makeText(ToDoActivity.this, "Updated Task", Toast.LENGTH_SHORT).show(); // Lets user know that their task got updated
                }else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(ToDoActivity.this, "Task update failed"+ error, Toast.LENGTH_SHORT).show(); // Lets user know that their task didn't get updated
                }
            });
            dialog.dismiss();
            Handler handler = new Handler();
            handler.postDelayed(() -> {
                startActivity(getIntent());
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            },0);
        }); // Updates the task when update button is selected. Let's the user know if the task succeeded or failed.
        deleteB.setOnClickListener(v12 -> {
            reference.child(key).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(ToDoActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
                }else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(ToDoActivity.this, "Task delete failed"+ error, Toast.LENGTH_SHORT).show();
                }
            }); // Deletes the task when user selects the delete button. Lets the user know if deletion was successful or failed.

            dialog.dismiss();
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(ToDoActivity.this,HomeActivity.class));
        overridePendingTransition(0,0); //Returns to the homepage when back button is pressed
    }
}