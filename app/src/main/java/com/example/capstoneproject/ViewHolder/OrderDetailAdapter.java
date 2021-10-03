package com.example.capstoneproject.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.capstoneproject.Cart;
import com.example.capstoneproject.Model.CartDetail;
import com.example.capstoneproject.OrderDetail;
import com.example.capstoneproject.R;

import java.util.List;

class MyViewHolder extends RecyclerView.ViewHolder {

    public TextView foodName;
    public TextView foodQuantity;
    public TextView foodPrice;

    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        foodName = itemView.findViewById(R.id.food_name);
        foodQuantity = itemView.findViewById(R.id.food_quantity);
        foodPrice = itemView.findViewById(R.id.food_price);

    }
}
public class OrderDetailAdapter extends RecyclerView.Adapter<MyViewHolder> {

    List<CartDetail> foods;

    public OrderDetailAdapter(List<CartDetail> foods) {
        this.foods = foods;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_detail_layout,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CartDetail foodList = foods.get(position);
        holder.foodName.setText    (String.format("Name:      %s",foodList.getFoodName()));
        holder.foodQuantity.setText(String.format("Quantity:  %s",foodList.getQuantity()));
        holder.foodPrice.setText   (String.format("Price:        RM%s",foodList.getFoodPrice()));

    }

    @Override
    public int getItemCount() {
        return foods.size();
    }
}