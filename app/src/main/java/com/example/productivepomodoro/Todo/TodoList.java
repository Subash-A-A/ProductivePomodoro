package com.example.productivepomodoro.Todo;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.productivepomodoro.R;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class TodoList extends Fragment implements AdapterView.OnItemSelectedListener {

    private ArrayList<TodoModel> todoModels;

    private RecyclerView recyclerView;
    private SearchView searchView;
    private Spinner sortSpinner;
    private TodoRecyclerViewAdapter adapter;
    TodoParent.TodoCategory category;

    public TodoParent todoParent;

    private int spinnerSelection;
    private TodoModel deletedModelInStack;

    TodoList(ArrayList<TodoModel> todoModels, TodoParent todoParent, TodoParent.TodoCategory category){
        this.todoModels = todoModels;
        this.todoParent = todoParent;
        this.category = category;
        this.spinnerSelection = 0;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View emptyTaskView = inflater.inflate(R.layout.empty_task_list, container, false);
        View todoView = inflater.inflate(R.layout.todo_page, container, false);

        searchView = todoView.findViewById(R.id.searchView);
        sortSpinner = todoView.findViewById(R.id.sortSpinner);

        YoYo.with(Techniques.BounceInDown).duration(500).playOn(searchView);
        YoYo.with(Techniques.BounceInDown).delay(100).duration(500).playOn(sortSpinner);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                getContext(), R.array.sort_arr, android.R.layout.simple_spinner_item);

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(spinnerAdapter);
        sortSpinner.setOnItemSelectedListener(this);

        sortSpinner.setSelection(spinnerSelection);

        recyclerView = todoView.findViewById(R.id.todoRecycler);

        adapter = new TodoRecyclerViewAdapter(inflater.getContext(), todoModels, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(touchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });

        return todoView;
    }

    private final ItemTouchHelper.SimpleCallback touchCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,
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
                    if(category == TodoParent.TodoCategory.ONGOING){
                        todoParent.markAsCompleted(viewPosition, todoModels.get(viewPosition));
                    }
                    else{
                        todoParent.markAsOngoing(viewPosition, todoModels.get(viewPosition));
                    }
                    break;
            }
        }
    };

    public void addTask(String taskName, String taskNote, int priority, LocalDateTime due){
        TodoModel newModel = new TodoModel(taskName, taskNote, false, priority, due);
        todoModels.add(0, newModel);
        adapter.notifyItemInserted(0);

        Toast.makeText(getContext(), "Task Added!", Toast.LENGTH_SHORT).show();
    }

    public void deleteTask(int viewPosition){
        deletedModelInStack = todoModels.get(viewPosition);
        removeFromList(viewPosition);
        undoSnackBar(deletedModelInStack.getMainTaskName() + " was Deleted", viewPosition);
        Log.w("Todo", "Task Deleted!");
    }

    public void undoSnackBar(String message, int viewPosition){
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG)
                .setAction("Undo", view -> {
                    addTaskToList(deletedModelInStack, viewPosition);
                }).show();
    }

    public void addTaskToList(TodoModel todoModel){
        todoModels.add(0, todoModel);
        adapter.notifyItemInserted(0);

        adapter.getTodoModelsFull().add(0, todoModel);
    }
    public void addTaskToList(TodoModel todoModel, int position){
        todoModels.add(position, todoModel);
        adapter.notifyItemInserted(position);

        adapter.getTodoModelsFull().add(position, todoModel);
    }

    public void removeFromList(int position){
        TodoModel todoToBeRemoved = todoModels.get(position);

        todoModels.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.getTodoModelsFull().remove(todoToBeRemoved);
    }
    public void replaceTodo(int position, TodoModel model){
        removeFromList(position);
        addTaskToList(model, position);
        Toast.makeText(getContext(),"Task Edited!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
        spinnerSelection = pos;
        switch (pos){
            case 0:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    todoModels.sort(new SortByDateOfCreation());
                }
                break;
            case 1:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    todoModels.sort(new SortByDueDate());
                }
                break;
            case 2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    todoModels.sort(new SortByName());
                }
                break;
            case 3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    todoModels.sort(new SortByPriority());
                }
                break;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void clearSearch(){
        if(!searchView.getQuery().toString().equals("")){
            searchView.setQuery("", false);
            searchView.clearFocus();
        }
    }
    static class SortByName implements Comparator<TodoModel> {
        @Override
        public int compare(TodoModel t1, TodoModel t2) {
            String name1 = t1.getMainTaskName().trim().toLowerCase();
            String name2 = t2.getMainTaskName().trim().toLowerCase();
            return name1.compareTo(name2);
        }
    }
    static class SortByPriority implements Comparator<TodoModel> {
        @Override
        public int compare(TodoModel t1, TodoModel t2) {
            return t2.getTaskPriority() - t1.getTaskPriority();
        }
    }
    static class SortByDateOfCreation implements Comparator<TodoModel> {
        @Override
        public int compare(TodoModel t1, TodoModel t2) {
            Date d1 = t1.getDateOfCreation();
            Date d2 = t2.getDateOfCreation();
            return d1.compareTo(d2);
        }
    }
    static class SortByDueDate implements Comparator<TodoModel> {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public int compare(TodoModel t1, TodoModel t2) {
            LocalDateTime d1 = t1.getDueDate();
            LocalDateTime d2 = t2.getDueDate();
            return d1.compareTo(d2);
        }
    }
}
