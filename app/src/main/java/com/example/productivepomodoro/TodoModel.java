package com.example.productivepomodoro;

import android.view.View;
import android.widget.CheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TodoModel {
    String mainTaskName;
    ArrayList<String> subTasks;

    public TodoModel(String mainTaskName, ArrayList<String> subTasks) {
        this.mainTaskName = mainTaskName;
        this.subTasks = subTasks;
    }

    public String getMainTaskName() {
        return mainTaskName;
    }
}
