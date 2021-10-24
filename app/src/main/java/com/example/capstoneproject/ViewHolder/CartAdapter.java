package com.example.capstoneproject.ViewHolder;

import android.content.Context;
import android.graphics.Color;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.example.capstoneproject.Interface.ItemClickListener;
import com.example.capstoneproject.Model.CartDetail;
import com.example.capstoneproject.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    ,View.OnCreateContextMenuListener{

    public TextView foodName,foodPrice;
    public ImageView foodQuantity;

    private ItemClickListener itemClickListener;

    public void setTxt_cart_name(TextView txt_cart_name) {
        this.foodName = txt_cart_name;
    }

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        foodName = itemView.findViewById(R.id.foodName);
        foodPrice = itemView.findViewById(R.id.foodPrice);
        foodQuantity = itemView.findViewById(R.id.foodQuantity);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select action");
        contextMenu.add(0,0,getAdapterPosition(),"Delete");
    }
}

public class CartAdapter extends RecyclerView.Adapter<CartViewHolder>{

    private List<CartDetail> listData = new ArrayList<>();
    private Context context; //The location where we want to show our food selected

    public CartAdapter(List<CartDetail> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    //Set layout of food ordered in cart
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        //Apply layout from cart_layout.xml to cart.xml
        View itemView = inflater.inflate(R.layout.cart_layout,parent,false);
        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        //TextDrawable library is used to display quantity selected for a certain food that user picked
        TextDrawable drawable = TextDrawable.builder().beginConfig().fontSize(50).endConfig()
                .buildRound(""+listData.get(position).getQuantity()+"x", Color.parseColor("#2B77ED"));

        //Set the foodQuantity value in the cardview holder in cart_layout.xml
        holder.foodQuantity.setImageDrawable(drawable);

        //To get currency "RM"
        Locale locale = new Locale("en","MY");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        float price;
        float itemPrice;
        int quantity;

        //Convert String to respective data types and set value into the cardview holder
        itemPrice = Float.parseFloat(listData.get(position).getFoodPrice());
        quantity = Integer.parseInt(listData.get(position).getQuantity());
        price = itemPrice * quantity;

        holder.foodPrice.setText(fmt.format(price));

        holder.foodName.setText(listData.get(position).getFoodName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
