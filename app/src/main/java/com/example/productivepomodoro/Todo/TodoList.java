package com.example.productivepomodoro.Todo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.example.productivepomodoro.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Collections;

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

    private boolean tabShown = true;

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
                TabLayout tabLayout = todoParent.getTabLayout();
                if(newText.equals("")){
//                    tabLayout.setVisibility(View.VISIBLE);

                } else{
                    todoParent.canSwitchTabs = false;
//                    tabLayout.setVisibility(View.INVISIBLE);
                }

                adapter.getFilter().filter(newText);
                return false;
            }
        });

//        return (todoModels.isEmpty()) ? emptyTaskView:todoView;
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

    public void addTask(String taskName, String taskNote){
        TodoModel newModel = new TodoModel(taskName, taskNote, false);
        todoModels.add(0, newModel);
        adapter.notifyItemInserted(0);
    }

    public void deleteTask(int viewPosition){
        deletedModelInStack = todoModels.get(viewPosition);
        removeFromList(viewPosition);
        undoSnackBar(deletedModelInStack.getMainTaskName() + " was Deleted", viewPosition);
    }

    public void undoSnackBar(String message, int viewPosition){
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_LONG)
                .setAction("Undo", view -> {
                    todoModels.add(viewPosition, deletedModelInStack);
                    adapter.notifyItemInserted(viewPosition);
                }).show();
    }

    public void addTaskToList(TodoModel todoModel){
        todoModels.add(0, todoModel);
        adapter.notifyItemInserted(0);
    }

    public void removeFromList(int position){
        todoModels.remove(position);
        adapter.notifyItemRemoved(position);
    }
    public void replaceTodo(int position, TodoModel model){
        todoModels.remove(position);
        adapter.notifyItemRemoved(position);
        todoModels.add(position, model);
        adapter.notifyItemInserted(position);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long l) {
        spinnerSelection = pos;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public void clearSearch(){
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);
    }
}
