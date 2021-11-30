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
    private ArrayList<CompanyItem> companyList;
    private OnItemClickListener companyListener;

    // Interface for Catch Notify for Listeners
    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
    // Set the listener
    public void setOnItemClickListener(OnItemClickListener listener){
        this.companyListener = listener;
    }

    // Static class Holder to manage Student Adapter
    public static class StudentViewHolder extends RecyclerView.ViewHolder{

        public TextView companyName;
        public TextView companyFounder;
        public TextView companyProduct;
        public StudentViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            companyName = itemView.findViewById(R.id.companyName);
            companyFounder = itemView.findViewById(R.id.companyFounder);
            companyProduct = itemView.findViewById(R.id.companyProduct);

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
    public StudentAdapter(ArrayList<CompanyItem> companyList){
        this.companyList = companyList;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.student_item, parent, false);
        StudentViewHolder svh = new StudentViewHolder(v, companyListener);
        return svh;
    }

    // Each item is init to fields: fullName & gender in the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        CompanyItem companyItem = companyList.get(position);

        holder.companyName.setText(companyItem.getCompanyName());
        holder.companyFounder.setText(companyItem.getCompanyFounder());
        holder.companyProduct.setText(companyItem.getCompanyProduct());
    }

    // Return current List Size (index)
    @Override
    public int getItemCount() {
        return companyList.size();
    }
}
