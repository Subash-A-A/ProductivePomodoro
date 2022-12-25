package com.example.productivepomodoro;

import android.content.Context;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class TodoRecyclerViewAdapter extends RecyclerView.Adapter<TodoRecyclerViewAdapter.MyViewHolder>{
    Context context;
    ArrayList<TodoModel> todoModels;
    static TodoList todoList;

    private TodoModel deletedModelInStack;

    public TodoRecyclerViewAdapter(Context context, ArrayList<TodoModel> todoModels, TodoList todoList){
        this.context = context;
        this.todoModels = todoModels;
        TodoRecyclerViewAdapter.todoList = todoList;
    }

    @NonNull
    @Override
    public TodoRecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.todo_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodoRecyclerViewAdapter.MyViewHolder holder, int position) {
        holder.mainTaskName.setText(todoModels.get(position).getMainTaskName());
    }

    @Override
    public int getItemCount() {
        return todoModels.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView mainTaskName;
        CheckBox taskState;
        FloatingActionButton deleteButton;
        FloatingActionButton editButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mainTaskName = itemView.findViewById(R.id.mainTask);
            taskState = itemView.findViewById(R.id.checkBox);
            deleteButton = itemView.findViewById(R.id.rowDeleteButton);
            editButton = itemView.findViewById(R.id.rowEditButton);

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
                }
            });
            taskState.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    Toast.makeText(itemView.getContext(), mainTaskName.getText() + " : " + checked, Toast.LENGTH_SHORT).show();
                    if(checked){
                        YoYo.with(Techniques.RubberBand).duration(500).playOn(itemView);
                    }
                    else{
                        YoYo.with(Techniques.Shake).duration(500).playOn(itemView);
                    }
                }
            });
        }
    }
}
