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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.productivepomodoro.Timer.Pomodoro;
import com.example.productivepomodoro.Todo.TodoList;
import com.example.productivepomodoro.Todo.TodoModel;

public class PomodoroInputDialog extends AppCompatDialogFragment {
    private EditText editFocusMinutes;
    private EditText editBreakMinutes;
    private EditText editLongBreakMinutes;
    private EditText editPomodoroSets;

    private boolean isEditingTask = false;

    private TextView taskName;
    private TextView taskNote;
    private int position;

    private Pomodoro pomodoro;

    public PomodoroInputDialog(Pomodoro pomodoro){
        this.pomodoro = pomodoro;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        int theme = R.style.AlertDialogTheme;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), theme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.pomodoro_input_settings, null);

        editFocusMinutes = view.findViewById(R.id.focusMinutes);
        editBreakMinutes = view.findViewById(R.id.breakMinutes);
        editLongBreakMinutes = view.findViewById(R.id.longBreakMinutes);
        editPomodoroSets = view.findViewById(R.id.pomodoroSets);

        builder.setView(view)
                .setTitle("Pomodoro Settings")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getContext(), "Settings Applied!", Toast.LENGTH_SHORT).show();
                    }
                });

        return builder.create();
    }

    private void addTask(){

    }
    private void editTask(){

    }
    private void fillInputFields(){
    }
}
