package com.example.productivepomodoro.Todo;

public class TodoModel {
    String mainTaskName;
    boolean taskChecked;
    String taskNote;
    int priority;

    public TodoModel(String mainTaskName, String taskNote, boolean taskChecked, int priority) {
        this.mainTaskName = mainTaskName;
        this.taskNote = taskNote;
        this.taskChecked = taskChecked;
        this.priority = priority;
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
}
