package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.capstoneproject.Model.CartDetail;
import com.example.capstoneproject.Model.Food;
import com.example.capstoneproject.database.Database;
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

    Food currentFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        //Firebase
        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        food = database.getReference("Food");

        numberButton = findViewById(R.id.numberButton);
        cartButton = findViewById(R.id.cartButton);

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Database(getBaseContext()).addToCart(new CartDetail(
                        foodID,
                        currentFood.getFoodName(),
                        numberButton.getNumber(),
                        currentFood.getFoodPrice()

                ));

                Toast.makeText(FoodDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

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
                currentFood = snapshot.getValue(Food.class);

                Glide.with(getBaseContext()).load(currentFood.getFoodImageURL()).into(foodImage);

                collapsingToolbarLayout.setTitle(currentFood.getFoodName());

                foodPrice.setText(currentFood.getFoodPrice());

                foodName.setText(currentFood.getFoodName());

                foodDesc.setText(currentFood.getFoodDesc());

                numberButton.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                    @Override
                    public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                        foodPrice.setText(String.format("%.2f",Float.parseFloat(currentFood.getFoodPrice()) * newValue));
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}