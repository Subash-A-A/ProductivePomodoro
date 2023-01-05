package com.example.productivepomodoro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.productivepomodoro.Todo.TodoList;
import com.example.productivepomodoro.Todo.TodoModel;

public class InputDialog extends AppCompatDialogFragment {
    private EditText editTextTask;
    private EditText editTextNote;
    private TodoList todoList;
    private boolean isEditingTask = false;

    private TextView taskName;
    private TextView taskNote;
    private int position;

    public InputDialog(TodoList todoList, boolean isEditingTask){
        this.todoList = todoList;
        this.isEditingTask = isEditingTask;
    }
    public InputDialog(TodoList todoList, boolean isEditingTask, TextView taskName, TextView taskNote, int position){
        this.todoList = todoList;
        this.isEditingTask = isEditingTask;
        this.taskName = taskName;
        this.taskNote = taskNote;
        this.position = position;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int theme = R.style.AlertDialogTheme;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), theme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.input_popup, null);

        editTextTask = view.findViewById(R.id.taskInput);
        editTextNote = view.findViewById(R.id.noteInput);

        if(isEditingTask) setText();

        builder.setView(view)
                .setTitle(isEditingTask ? "Edit Task" : "Add Task")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isEditingTask){
                            editTask();
                        }else{
                            addTask();
                        }
                    }
                });

        return builder.create();
    }

    private void addTask(){
        String taskName = editTextTask.getText().toString();
        String taskNote = editTextNote.getText().toString();
        todoList.addTask(taskName, taskNote);
    }
    private void editTask(){
        String taskNameStr = editTextTask.getText().toString();
        String taskNoteStr = editTextNote.getText().toString();

        todoList.replaceTodo(position, new TodoModel(taskNameStr, taskNoteStr, false));
    }
    private void setText(){
        editTextTask.setText(taskName.getText());
        editTextNote.setText(taskNote.getText());
    }
}
