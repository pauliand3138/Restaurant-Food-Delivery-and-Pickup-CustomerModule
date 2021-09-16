package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capstoneproject.Interface.ItemClickListener;
import com.example.capstoneproject.Model.Food;
import com.example.capstoneproject.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String foodCatID="";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        //Firebase
        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        foodList = database.getReference("Food");

        recyclerView = findViewById(R.id.recycler_food);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        //Get Intent
        if(getIntent() != null)
            foodCatID = getIntent().getStringExtra("Food Category ID");

        if(!foodCatID.isEmpty() && foodCatID != null) {
            loadFoodList(foodCatID);
        }
    }

    private void loadFoodList(String foodCatID) {
        adapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class, R.layout.food_item, FoodViewHolder.class,
                foodList.orderByChild("foodCatID").equalTo(foodCatID)) { //Similar to SQL statement, SELECT * FROM Food WHERE foodCatID = xxx

            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int i) {
                foodViewHolder.txtfoodName.setText(food.getFoodName());
//                Picasso.with(getBaseContext()).load(food.getFoodImageURL()).resize(300,300).into(foodViewHolder.food_image);
                Glide.with(getBaseContext()).load(food.getFoodImageURL()).into(foodViewHolder.food_image);
                Food selectedFood = food;
                foodViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Start new Activity
                        Intent foodDetail = new Intent(FoodList.this, FoodDetail.class);
                        //Send food ID to new Activity
                        foodDetail.putExtra("Food ID", adapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };

        //Set adapter
        recyclerView.setAdapter(adapter);
    }
}