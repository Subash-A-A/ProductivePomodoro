package com.example.productivepomodoro;

import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class TodoList extends Fragment {

    ArrayList<TodoModel> todoModels = new ArrayList<>();
    private String[] testTasks = {"Assignments", "Workout", "Study", "Build Apps", "Demo Task", "Game!"};
    private HashMap<String, ArrayList<String>> taskMap = new HashMap<>();

    private RecyclerView recyclerView;
    private TodoRecyclerViewAdapter adapter;
    private FloatingActionButton addTaskButton;

    private TodoModel deletedModelInStack;

    TodoList(){
        setUpTodoModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View todoView = inflater.inflate(R.layout.todo_page, container, false);

        recyclerView = todoView.findViewById(R.id.todoRecycler);
        addTaskButton = todoView.findViewById(R.id.addTaskButton);

        adapter = new TodoRecyclerViewAdapter(inflater.getContext(), todoModels, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                todoModels.add(0, todoModels.get(0));
                adapter.notifyItemInserted(0);
            }
        });

        return todoView;
    }

    private void setUpTodoModel(){
        for (String testTask : testTasks) {
            ArrayList<String> subTasks = new ArrayList<>();
            for(int i = 1; i <= 5; i++){
                subTasks.add("Subtask: " + i);
            }

            taskMap.put(testTask, subTasks);
            todoModels.add(new TodoModel(testTask, subTasks));
        }
    }

    private ItemTouchHelper.SimpleCallback touchCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                    ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            int fromPos = viewHolder.getAdapterPosition();
            int toPos = target.getAdapterPosition();

            Collections.swap(todoModels, fromPos, toPos);
            adapter.notifyItemMoved(fromPos, toPos);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int viewPosition = viewHolder.getAdapterPosition();
            switch (direction){
                case ItemTouchHelper.LEFT:
                    deleteTask(viewPosition);
                    break;

                case ItemTouchHelper.RIGHT:
                    break;
            }
        }
    };

    public void deleteTask(int viewPosition){
        deletedModelInStack = todoModels.get(viewPosition);
        todoModels.remove(viewPosition);
        adapter.notifyItemRemoved(viewPosition);
        undoSnackBar(deletedModelInStack.getMainTaskName() + " was Deleted", viewPosition);
    }

    public void undoSnackBar(String message, int viewPosition){
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG)
                .setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        todoModels.add(viewPosition, deletedModelInStack);
                        adapter.notifyItemInserted(viewPosition);
                    }
                }).show();
    }
}
