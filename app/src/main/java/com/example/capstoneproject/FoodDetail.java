package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.disklrucache.DiskLruCache;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.capstoneproject.Model.Food;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FoodDetail extends AppCompatActivity {

    TextView foodName;
    TextView foodPrice;
    TextView foodDesc;
    ImageView foodImage;

    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton cartButton;
    ElegantNumberButton numberButton;

    String foodID = "";

    FirebaseDatabase database;
    DatabaseReference food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase
        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        food = database.getReference("Food");

        numberButton = findViewById(R.id.numberButton);
        cartButton = findViewById(R.id.cartButton);
        foodDesc = findViewById(R.id.foodDesc);
        foodName = findViewById(R.id.foodName);
        foodPrice = findViewById(R.id.foodPrice);
        foodImage = findViewById(R.id.foodImage);
        collapsingToolbarLayout = findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Get foodID from previous Activity Intent
        if(getIntent() != null) {
            foodID = getIntent().getStringExtra("Food ID");
        }

        if(!foodID.isEmpty()) {
            getDetailFood(foodID);
        }
    }

    public void getDetailFood(String foodID) {
        food.child(foodID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Food food = snapshot.getValue(Food.class);

                Glide.with(getBaseContext()).load(food.getFoodImageURL()).into(foodImage);

                collapsingToolbarLayout.setTitle(food.getFoodName());

                foodPrice.setText(food.getFoodPrice());

                foodName.setText(food.getFoodName());

                foodDesc.setText(food.getFoodDesc());

                numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                    @Override
                    public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                        foodPrice.setText(String.format("%.2f",Float.parseFloat(food.getFoodPrice()) * newValue));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}