package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneproject.Common.Common;
import com.example.capstoneproject.Model.Restaurant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    Button registerButton;
    Button loginButton;
    TextView titleTextView;
    TextView hoursTextView;
    TextView subtitleTextView;
    TextView lastOrderTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference restaurantTable = database.getReference("Restaurant");

        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);
        titleTextView = findViewById(R.id.titleTextView);
        hoursTextView = findViewById(R.id.hoursTextView);
        subtitleTextView = findViewById(R.id.subtitleTextView);
        lastOrderTextView = findViewById(R.id.lastOrderTextView);

        restaurantTable.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Restaurant restaurant = snapshot.getValue(Restaurant.class);
                Common.currentRestaurant = restaurant;
                titleTextView.setText(restaurant.getRestName());
                subtitleTextView.setText(restaurant.getRestSlogan());
                hoursTextView.setText(restaurant.getRestOpening() + " - " + restaurant.getRestClosing());
                lastOrderTextView.setText("(Last Order: " + restaurant.getRestLastOrderTime() + ")");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent login = new Intent(MainActivity.this, Login.class);
                startActivity(login);
            }
        });


        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent registration = new Intent(MainActivity.this, Registration.class);
                startActivity(registration);
            }
        });
    }


}