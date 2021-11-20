package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.capstoneproject.Interface.ItemClickListener;
import com.example.capstoneproject.Model.Food;
import com.example.capstoneproject.ViewHolder.FoodViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FoodList extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference foodList;

    String foodCatID="";

    FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;

    //Search Functionality
    FirebaseRecyclerAdapter<Food, FoodViewHolder> searchAdapter;
    java.util.List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

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

        //Search
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.search_bar);
        materialSearchBar.setHint("Search Food");
        loadSuggest(); //Write function to load suggest from firebase
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //When user type their text, we will change suggest list
                List<String> suggest = new ArrayList<String>();
                for(String search:suggestList){ //loop in suggest list
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase()))
                        suggest.add(search);
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //When search bar is close
                //Restore the original adapter
                if(!enabled)
                    recyclerView.setAdapter(adapter);
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //When finish searching
                //Show result of search adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });
    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Food, FoodViewHolder>(
                Food.class,
                R.layout.food_item,
                FoodViewHolder.class,
                foodList.orderByChild("foodName").equalTo(text.toString()) //compare name
        ) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int position) {
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
                        foodDetail.putExtra("Food ID", searchAdapter.getRef(position).getKey());
                        startActivity(foodDetail);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);
    }

    private void loadSuggest() {
                foodList.orderByChild("foodCatID").equalTo(foodCatID)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    Food item = postSnapshot.getValue(Food.class);
                                    suggestList.add(item.getFoodName()); //Add Name of food to suggest list
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
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
