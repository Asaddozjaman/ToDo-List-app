package com.example.todolist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TaskAdapter taskAdapter;
    private ArrayList<Task> taskList;
    private FloatingActionButton fabAddTask;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views
        recyclerView = findViewById(R.id.recyclerView);
        fabAddTask = findViewById(R.id.fabAddTask);

        // Initialize Database and Load Tasks
        databaseHelper = new DatabaseHelper(this);
        taskList = databaseHelper.getAllTasks();

        // Initialize Adapter
        taskAdapter = new TaskAdapter(taskList, new TaskAdapter.OnTaskActionListener() {
            @Override
            public void onEdit(int position) {
                showEditTaskDialog(position);
            }

            @Override
            public void onDelete(int position) {
                Task task = taskList.get(position);
                databaseHelper.deleteTask(task.getId());
                taskList.remove(position);
                taskAdapter.notifyItemRemoved(position);
                Toast.makeText(MainActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(taskAdapter);

        // Floating Action Button Listener
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTaskDialog();
            }
        });
    }

    private void showAddTaskDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add New Task");

        // Input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String taskText = input.getText().toString().trim();
                if (!taskText.isEmpty()) {
                    long id = databaseHelper.addTask(taskText);
                    Task task = new Task((int) id, taskText);
                    taskList.add(task);
                    taskAdapter.notifyItemInserted(taskList.size() - 1);
                } else {
                    Toast.makeText(MainActivity.this, "Task cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void showEditTaskDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit Task");

        // Input field
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(taskList.get(position).getTask());
        builder.setView(input);

        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedTaskText = input.getText().toString().trim();
                if (!updatedTaskText.isEmpty()) {
                    Task task = taskList.get(position);
                    task.setTask(updatedTaskText);
                    databaseHelper.updateTask(task.getId(), updatedTaskText);
                    taskAdapter.notifyItemChanged(position);
                } else {
                    Toast.makeText(MainActivity.this, "Task cannot be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }
}
