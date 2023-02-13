package com.example.productivepomodoro.Todo;

import java.time.LocalDateTime;
import java.util.Date;

public class TodoModel {
    String mainTaskName;
    boolean taskChecked;
    String taskNote;
    int priority;
    Date dateOfCreation;
    LocalDateTime dueDate;

    public TodoModel(String mainTaskName, String taskNote, boolean taskChecked, int priority, LocalDateTime dueDate) {
        this.mainTaskName = mainTaskName;
        this.taskNote = taskNote;
        this.taskChecked = taskChecked;
        this.priority = priority;
        this.dueDate = dueDate;
        this.dateOfCreation = new Date();
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
    public int getTaskPriority() {
        return priority;
    }
    public Date getDateOfCreation() {
        return dateOfCreation;
    }
    public LocalDateTime getDueDate() {
        return dueDate;
    }
}
