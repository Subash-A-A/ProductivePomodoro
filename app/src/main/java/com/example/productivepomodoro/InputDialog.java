package com.example.productivepomodoro;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.productivepomodoro.Todo.TodoList;
import com.example.productivepomodoro.Todo.TodoModel;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;

public class InputDialog extends AppCompatDialogFragment {
    private EditText editTextTask;
    private EditText editTextNote;
    private SeekBar prioritySeekBar;
    private TextView priorityDisplay;
    private Button datePick;
    private TextView dueDateText;

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
        int dateTheme = R.style.DateTheme;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), theme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.input_popup, null);

        editTextTask = view.findViewById(R.id.taskInput);
        editTextNote = view.findViewById(R.id.noteInput);
        prioritySeekBar = view.findViewById(R.id.prioritySeekBar);
        priorityDisplay = view.findViewById(R.id.priorityText);
        datePick = view.findViewById(R.id.dueDateButton);
        dueDateText = view.findViewById(R.id.dueDateText);

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
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(isEditingTask){
                            editTask();
                        }else{
                            addTask();
                        }
                    }
                });

        datePick.setOnClickListener(view1 -> {
            final Calendar c = Calendar.getInstance();
            int dueYear = c.get(Calendar.YEAR);
            int dueMonth = c.get(Calendar.MONTH);
            int dueDay = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getContext(),
                    dateTheme,
                    (datePicker, year, monthOfYear, dayOfMonth) -> {
                        dueDateText.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                    }, dueYear, dueMonth, dueDay
            );
            dialog.show();
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void addTask(){
        String taskName = editTextTask.getText().toString();
        String taskNote = editTextNote.getText().toString();
        int priority = prioritySeekBar.getProgress();
        String[] dateArr = dueDateText.getText().toString().split("-");
        Log.w("InputTodo", dateArr.length + "");
        Log.w("InputTodo", dueDateText.getText().toString());
        LocalDateTime due = LocalDateTime.MAX;
        if(!dueDateText.getText().toString().equals("No Due Date")){
            due = LocalDateTime.of(parseInt(dateArr[2]), parseInt(dateArr[1]), parseInt(dateArr[0]), 00, 00, 00);
        }
        todoList.addTask(taskName, taskNote, priority, due);
        Log.w("Todo", "New Task Added!");
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void editTask(){
        String taskNameStr = editTextTask.getText().toString();
        String taskNoteStr = editTextNote.getText().toString();
        int priority = prioritySeekBar.getProgress();

        todoList.replaceTodo(position, new TodoModel(taskNameStr, taskNoteStr, false, priority, LocalDateTime.now()));
        Log.w("Todo", "Task Edited!");
    }
    private void fillInputFields(){
        editTextTask.setText(taskName.getText());
        editTextNote.setText(taskNote.getText());
    }
    private int parseInt(String s){
        return Integer.parseInt(s);
    }
}
