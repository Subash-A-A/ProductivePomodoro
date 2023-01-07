package com.example.productivepomodoro.Todo;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.productivepomodoro.InputDialog;
import com.example.productivepomodoro.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.MyViewHolder> implements Filterable {
    Context context;
    ArrayList<TodoModel> todoModels;
    ArrayList<TodoModel> todoModelsFull;
    static TodoList todoList;

    public TodoRecyclerViewAdapter(Context context, ArrayList<TodoModel> todoModels, TodoList todoList){
        this.context = context;
        this.todoModels = todoModels;
        TodoRecyclerViewAdapter.todoList = todoList;
        this.todoModelsFull = new ArrayList<>(todoModels);
    }

    @NonNull
    @Override
    public TodoRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        todoList.clearSearch();
        this.todoModelsFull = new ArrayList<>(todoModels);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.todo_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoRecyclerViewAdapter.MyViewHolder holder, int position) {
        TodoModel todoModel = todoModels.get(position);
        holder.mainTaskName.setText(todoModel.getMainTaskName());
        holder.taskNotes.setText(todoModel.getTasksNote());
        holder.priorityRating.setRating(todoModel.getTaskPriority() + 1);

        if(todoModel.getTaskChecked()){
            holder.mainTaskName.setPaintFlags(holder.mainTaskName.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.deleteButton.setEnabled(false);
            holder.editButton.setEnabled(false);
            holder.deleteButton.setVisibility(View.INVISIBLE);
            holder.editButton.setVisibility(View.INVISIBLE);
            holder.taskNotes.setPaintFlags(holder.taskNotes.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            holder.taskNotes.setFocusable(false);
            holder.checkImage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return todoModels.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mainTaskName;
        FloatingActionButton deleteButton;
        FloatingActionButton editButton;
        ImageView checkImage;
        TextView taskNotes;
        RatingBar priorityRating;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mainTaskName = itemView.findViewById(R.id.mainTask);
            deleteButton = itemView.findViewById(R.id.rowDeleteButton);
            editButton = itemView.findViewById(R.id.rowEditButton);
            taskNotes = itemView.findViewById(R.id.editTextTextMultiLine);
            checkImage = itemView.findViewById(R.id.completedCheckImage);
            priorityRating = itemView.findViewById(R.id.priorityRating);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    todoList.deleteTask(getAdapterPosition());
                    Toast.makeText(itemView.getContext(), mainTaskName.getText() + " : Deleted!", Toast.LENGTH_SHORT).show();
                }
            });
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(itemView.getContext(), mainTaskName.getText() + " : Edited!", Toast.LENGTH_SHORT).show();
                    openDialogueEdit();
                }
            });
        }
        public void openDialogueEdit(){
            InputDialog dialog = new InputDialog(todoList, true, mainTaskName, taskNotes, getAdapterPosition());
            dialog.show(todoList.requireActivity().getSupportFragmentManager(), "input dialog");
        }
    }

    private Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<TodoModel> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(todoModelsFull);
            }else{
                String filterText = charSequence.toString().toLowerCase().trim();
                for (TodoModel todoModel: todoModelsFull) {
                    if(todoModel.getMainTaskName().toLowerCase().contains(filterText)){
                        filteredList.add(todoModel);
                    }
                }
            }

            FilterResults result = new FilterResults();
            result.values = filteredList;

            return result;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            todoModels.clear();
            todoModels.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public ArrayList<TodoModel> getTodoModelsFull(){
        return todoModelsFull;
    }
}
