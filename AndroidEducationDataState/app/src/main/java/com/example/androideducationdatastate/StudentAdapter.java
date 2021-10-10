package com.example.androideducationdatastate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    // Class fields
    private ArrayList<StudentItem> studentList;
    private OnItemClickListener studentListener;

    // Interface for Catch Notify for Listeners
    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
    // Set the listener
    public void setOnItemClickListener(OnItemClickListener listener){
        this.studentListener = listener;
    }

    // Static class Holder to manage Student Adapter
    public static class StudentViewHolder extends RecyclerView.ViewHolder{

        public TextView fullNameView;
        public TextView genderView;
        public StudentViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            fullNameView = itemView.findViewById(R.id.fullName);
            genderView = itemView.findViewById(R.id.gender);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if (listener != null){
                       int position = getAdapterPosition();
                       if (position != RecyclerView.NO_POSITION){
                           listener.OnItemClick(position);
                       }
                    }
                }
            });
        }
    }

    // Constructor for init the Adapter
    public StudentAdapter(ArrayList<StudentItem> studentList){
        this.studentList = studentList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.student_item, parent, false);
        StudentViewHolder svh = new StudentViewHolder(v, studentListener);
        return svh;
    }

    // Each item is init to fields: fullName & gender in the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        StudentItem studentItem = studentList.get(position);

        holder.fullNameView.setText(studentItem.getFullName());
        holder.genderView.setText(studentItem.getGender());
    }

    // Return current List Size (index)
    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
