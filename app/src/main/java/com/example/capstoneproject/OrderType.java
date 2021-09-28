package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.capstoneproject.Common.Common;
import com.example.capstoneproject.Model.DeliverOrDineIn;

public class OrderType extends AppCompatActivity {

    Button deliveryButton;
    Button dineInButton;
    int backpress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_type);

        deliveryButton = findViewById(R.id.deliveryButton);
        dineInButton = findViewById(R.id.dineInButton);

        deliveryButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                DeliverOrDineIn orderType = new DeliverOrDineIn("Delivery");
                Intent homeIntent = new Intent(OrderType.this, Home.class);
                Common.currentOrderType = orderType;
                startActivity(homeIntent);
                finish();
            }
        });

        dineInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeliverOrDineIn orderType = new DeliverOrDineIn("Dine In");
                Intent homeIntent = new Intent(OrderType.this, Home.class);
                Common.currentOrderType = orderType;
                startActivity(homeIntent);
                finish();
            }
        });

    }
    public void onBackPressed(){
        backpress++;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

        if(backpress > 1) {
            Intent mainActivityIntent = new Intent(OrderType.this,MainActivity.class);
            startActivity(mainActivityIntent);
            finish();
        }

    }
}