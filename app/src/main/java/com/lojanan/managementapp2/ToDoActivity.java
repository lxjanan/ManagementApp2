package com.lojanan.managementapp2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        recyclerView = findViewById(R.id.recyclerV);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);

        FirebaseUser mUser = mAuth.getCurrentUser();
        String userID = mUser.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("tasks").child(userID);

        loader = new ProgressDialog(this);

        FloatingActionButton fab = findViewById(R.id.floatBtn);
        fab.setOnClickListener(v -> addTask());

        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setSelectedItemId(R.id.toDoList);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()){
                case R.id.toDoList:
                    return true;
                case R.id.homePage:
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
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
        alertDialog.setView(myView);

        final AlertDialog dialog = alertDialog.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); //This code removes the white part of the alertdialog
        dialog.setCancelable(false);

        final EditText task = myView.findViewById(R.id.task);
        final EditText description = myView.findViewById(R.id.description);
        Button save = myView.findViewById(R.id.saveBtn);
        Button cancel = myView.findViewById(R.id.cancelBtn);

        cancel.setOnClickListener(v -> dialog.dismiss());
        save.setOnClickListener(v -> {
            String mTask = task.getText().toString().trim();
            String mDescription = description.getText().toString().trim();
            String id = reference.push().getKey();
            String date = DateFormat.getDateInstance().format(new Date());

            if (TextUtils.isEmpty(mTask)) {
                task.setError("Task required");
                return;
            }
            if (TextUtils.isEmpty(mDescription)){
                description.setError("Description required");
                return;
            }else {
                loader.setMessage("Task is being added");
                loader.setCanceledOnTouchOutside(false);
                loader.show();

                Model model = new Model(mTask, mDescription, id, date);
                assert id != null;
                reference.child(id).setValue(model).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()){
                        Toast.makeText(ToDoActivity.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        String error = Objects.requireNonNull(task1.getException()).toString();
                        Toast.makeText(ToDoActivity.this,"Failed: " + error, Toast.LENGTH_SHORT).show();
                    }
                    loader.dismiss();
                });
            }
            dialog.dismiss();
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
                holder.setDesc(model.getDescription());

                holder.mView.setOnClickListener(v -> {
                    key = getRef(position).getKey();
                    task = model.getTask();
                    description = model.getDescription();

                    updateTask();
                });
            }

            @NonNull
            @Override
            public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.retrieved_layout, parent, false);
                view.getLayoutParams().height = parent.getMeasuredHeight() / 4; //This code is added so that the ViewHolder doesn't take up an entire page
                return new MyViewHolder(view);
            }
        };

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
            taskTextView.setText(task);
        }

        public void setDesc(String desc){
            TextView descTextView = mView.findViewById(R.id.descriptionView);
            descTextView.setText(desc);
        }
        public void setDate(String date){
            TextView dateTextView = mView.findViewById(R.id.dateView);
        }
    }
    private void updateTask(){
        AlertDialog.Builder dialogTask = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.update_task, null);
        dialogTask.setView(view);

        AlertDialog dialog = dialogTask.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText mTask = (EditText)view.findViewById(R.id.mEditTask);
        EditText mDesc = (EditText)view.findViewById(R.id.mEditDescription);

        mTask.setText(task);
        mTask.setSelection(task.length());

        mDesc.setText(description);
        mDesc.setSelection(description.length());

        Button deleteB = view.findViewById(R.id.deleteBtn);
        Button updateB = view.findViewById(R.id.updateBtn);

        updateB.setOnClickListener(v1 -> {
            task = mTask.getText().toString().trim();
            description = mDesc.getText().toString().trim();

            String date = DateFormat.getDateInstance().format(new Date());

            Model model = new Model(task, description, key, date);
            reference.child(key).setValue(model).addOnCompleteListener(task -> {

                if (task.isSuccessful()){
                    Toast.makeText(ToDoActivity.this, "Updated Task", Toast.LENGTH_SHORT).show();
                }else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(ToDoActivity.this, "Task update failed"+ error, Toast.LENGTH_SHORT).show();
                }

            });
            dialog.dismiss();

        });

        deleteB.setOnClickListener(v12 -> {
            reference.child(key).removeValue().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    Toast.makeText(ToDoActivity.this, "Task Deleted", Toast.LENGTH_SHORT).show();
                }else {
                    String error = Objects.requireNonNull(task.getException()).toString();
                    Toast.makeText(ToDoActivity.this, "Task delete failed"+ error, Toast.LENGTH_SHORT).show();
                }
            });

            dialog.dismiss();
        });
        dialog.show();
    }
}