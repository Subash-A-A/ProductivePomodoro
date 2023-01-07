package com.example.productivepomodoro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.productivepomodoro.Todo.TodoList;
import com.example.productivepomodoro.Todo.TodoModel;

public class InputDialog extends AppCompatDialogFragment {
    private EditText editTextTask;
    private EditText editTextNote;
    private SeekBar prioritySeekBar;
    private TextView priorityDisplay;

    private TodoList todoList;
    private boolean isEditingTask = false;

    private TextView taskName;
    private TextView taskNote;
    private int position;

    String[] priorityNames = {"Very Low", "Low", "Normal", "High", "Very High"};

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
        prioritySeekBar = view.findViewById(R.id.prioritySeekBar);
        priorityDisplay = view.findViewById(R.id.priorityText);

        prioritySeekBar.setOnSeekBarChangeListener(listener);

        if(isEditingTask) fillInputFields();

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

    SeekBar.OnSeekBarChangeListener listener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            priorityDisplay.setText(priorityNames[i]);
            YoYo.with(Techniques.RubberBand).duration(200).playOn(priorityDisplay);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void addTask(){
        String taskName = editTextTask.getText().toString();
        String taskNote = editTextNote.getText().toString();
        int priority = prioritySeekBar.getProgress();

        todoList.addTask(taskName, taskNote, priority);
    }
    private void editTask(){
        String taskNameStr = editTextTask.getText().toString();
        String taskNoteStr = editTextNote.getText().toString();
        int priority = prioritySeekBar.getProgress();

        todoList.replaceTodo(position, new TodoModel(taskNameStr, taskNoteStr, false, priority));
    }
    private void fillInputFields(){
        editTextTask.setText(taskName.getText());
        editTextNote.setText(taskNote.getText());
    }
}
