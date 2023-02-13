package com.example.productivepomodoro.Todo;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.productivepomodoro.InputDialog;
import com.example.productivepomodoro.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class TodoParent extends Fragment {
    Fragment fragmentToBeDisplayed;
    TodoList todoListOngoing;
    TodoList todoListCompleted;

    ArrayList<TodoModel> onGoingList;
    ArrayList<TodoModel> completedList;

    public enum TodoCategory{ONGOING, COMPLETED}

    public TodoParent(){
        onGoingList = new ArrayList<>();
        completedList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View todoMainView =  inflater.inflate(R.layout.todo_main_page, container, false);

        todoListCompleted = new TodoList(completedList, this, TodoCategory.COMPLETED);
        todoListOngoing = new TodoList(onGoingList, this, TodoCategory.ONGOING);

        fragmentToBeDisplayed = todoListCompleted;
        replaceTodoFragments();

        fragmentToBeDisplayed = todoListOngoing;
        replaceTodoFragments();

        FloatingActionButton addTaskButton = todoMainView.findViewById(R.id.addTaskButton);

        TabLayout tabLayout = todoMainView.findViewById(R.id.todoTabLayout);
//        FrameLayout frameLayout = todoMainView.findViewById(R.id.todoMainFrame);

        YoYo.with(Techniques.BounceInDown).duration(700).playOn(tabLayout);
        YoYo.with(Techniques.BounceInUp).duration(500).playOn(addTaskButton);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        todoListOngoing.clearSearch();

                        fragmentToBeDisplayed = todoListOngoing;
                        YoYo.with(Techniques.BounceInUp).duration(500).playOn(addTaskButton);
                        addTaskButton.setEnabled(true);
                        break;
                    case 1:
                        todoListCompleted.clearSearch();

                        fragmentToBeDisplayed = todoListCompleted;
                        addTaskButton.setEnabled(false);
                        YoYo.with(Techniques.SlideOutDown).duration(200).playOn(addTaskButton);
                        break;
                }

                replaceTodoFragments();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                YoYo.with(Techniques.Shake).duration(500).playOn(tab.view);
            }
        });

        addTaskButton.setOnClickListener(view -> openDialogue());

        return todoMainView;
    }
    public void openDialogue(){
        InputDialog dialog = new InputDialog(todoListOngoing, false);
        dialog.show(requireActivity().getSupportFragmentManager(), "input dialog");
    }

    private void replaceTodoFragments(){
        requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.todoMainFrame, fragmentToBeDisplayed)
                .commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void markAsCompleted(int onGoingPosition, TodoModel todoModel){
        String taskName = todoModel.getMainTaskName();
        String taskNote = todoModel.getTasksNote();
        int priority = todoModel.getTaskPriority();
        boolean checked = true;

        todoListCompleted.addTaskToList(new TodoModel(taskName, taskNote, checked, priority, LocalDateTime.now()));
        todoListOngoing.removeFromList(onGoingPosition);

        Toast.makeText(getContext(), taskName + " marked as Completed!", Toast.LENGTH_SHORT).show();
        Log.w("Todo", "Task Marked as Completed!");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void markAsOngoing(int completedPosition, TodoModel todoModel){
        String taskName = todoModel.getMainTaskName();
        String taskNote = todoModel.getTasksNote();
        int priority = todoModel.getTaskPriority();
        boolean checked = false;

        todoListOngoing.addTaskToList(new TodoModel(taskName, taskNote, checked, priority, LocalDateTime.now()));
        todoListCompleted.removeFromList(completedPosition);

        Toast.makeText(getContext(), taskName + " marked as Ongoing!", Toast.LENGTH_SHORT).show();
        Log.w("Todo", "Task Marked as Ongoing!");
    }

    public TodoList getTodoList(boolean getOngoing){
        if(getOngoing) return todoListOngoing;
        else return todoListCompleted;
    }
}
