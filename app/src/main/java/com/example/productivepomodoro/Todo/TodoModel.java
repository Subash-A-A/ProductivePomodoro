package com.example.productivepomodoro.Todo;

import android.view.View;
import android.widget.CheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TodoModel {
    String mainTaskName;
    boolean taskChecked;
    String taskNote;

    public TodoModel(String mainTaskName, String taskNote, boolean taskChecked) {
        this.mainTaskName = mainTaskName;
        this.taskNote = taskNote;
        this.taskChecked = taskChecked;
    }

    public String getMainTaskName() {
        return mainTaskName;
    }
    public String getTasksNote() {
        return taskNote;
    }
    public boolean getTaskChecked() {
        return taskChecked;
    }
}
