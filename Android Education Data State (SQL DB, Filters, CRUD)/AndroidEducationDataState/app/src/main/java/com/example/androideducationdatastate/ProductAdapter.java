package com.example.androideducationdatastate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>{

    // Class fields
    private ArrayList<ProductItem> productItems;
    private ProductAdapter.OnItemClickListener productListener;

    // Interface for Catch Notify for Listeners
    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
    // Set the listener
    public void setOnItemClickListener(ProductAdapter.OnItemClickListener listener){
        this.productListener = listener;
    }

    // Static class Holder to manage Student Adapter
    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        public TextView productName;
        public TextView productPrice;
        public ProductViewHolder(@NonNull View itemView, final ProductAdapter.OnItemClickListener listener) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);

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

    public ProductAdapter(ArrayList<ProductItem> productItems){
        this.productItems = productItems;
    }

    @NonNull
    @Override
    public ProductAdapter.ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.product_card_item, parent, false);
        ProductAdapter.ProductViewHolder svh = new ProductAdapter.ProductViewHolder(v, productListener);
        return svh;
    }

    // Each item is init to fields: fullName & gender in the RecyclerView
    @Override
    public void onBindViewHolder(@NonNull ProductAdapter.ProductViewHolder holder, int position) {
        ProductItem productItem = productItems.get(position);

        holder.productName.setText(productItem.getProductName());
        holder.productPrice.setText(productItem.getProductPrice());
    }

    // Return current List Size (index)
    @Override
    public int getItemCount() {
        return productItems.size();
    }
}
