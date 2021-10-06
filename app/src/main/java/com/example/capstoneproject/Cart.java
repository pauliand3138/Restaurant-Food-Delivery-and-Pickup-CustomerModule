package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.capstoneproject.Common.Common;
import com.example.capstoneproject.Model.CartDetail;
import com.example.capstoneproject.Model.Order;
import com.example.capstoneproject.ViewHolder.CartAdapter;
import com.example.capstoneproject.database.Database;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import info.hoang8f.widget.FButton;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference orders;

    TextView txtTotalPrice;
    FButton btnPlace;

    List<CartDetail> cart = new ArrayList<>();

    CartAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase
        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        orders = database.getReference("Order");

        //Init
        recyclerView = findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        txtTotalPrice = findViewById(R.id.total);
        btnPlace = findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create new order
                showAlertDialog();
            }
        });

        loadListFood();
    }

    private void showAlertDialog() {

        //if orderType is delivery
        if (Common.currentOrderType.getOrderType().equals("Delivery")) {

            if(txtTotalPrice.getText().toString().equals("RM0.00")) {
                Toast.makeText(Cart.this, "Cart must not be empty!", Toast.LENGTH_SHORT).show();

            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
                alertDialog.setTitle("One more step!");
                alertDialog.setMessage("Enter your address: ");

                LayoutInflater inflater = this.getLayoutInflater();
                View order_address_alertdialog = inflater.inflate(R.layout.order_address_alertdialog,null);

                MaterialEditText addressText = order_address_alertdialog.findViewById(R.id.addressText);
                MaterialEditText requestText = order_address_alertdialog.findViewById(R.id.requestText);
                MaterialEditText telNoText = order_address_alertdialog.findViewById(R.id.telNoText);
                telNoText.setText(Common.currentUser.getCustTelNo());

                alertDialog.setView(order_address_alertdialog);
                alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if(addressText.getText().toString().isEmpty()) {
                            Toast.makeText(Cart.this, "Address must not be empty!", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();

                        } else {
                            Order order = new Order(
                                    Common.currentUser.getCustID(),
                                    telNoText.getText().toString(),
                                    addressText.getText().toString(),
                                    Common.currentOrderType.getOrderType(),
                                    txtTotalPrice.getText().toString(),
                                    requestText.getText().toString(),
                                    cart
                            );

                            //Submit order to firebase
                            orders.child(String.valueOf(System.currentTimeMillis()))
                                    .setValue(order);
                            //Delete cart after placing order
                            new Database(getBaseContext()).cleanCart();
                            Toast.makeText(Cart.this, "Thank you, order placed", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                    }//end onClick

                });//end alertDialog.setPositiveButton

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                alertDialog.show();
            }

        //if orderType is dine in
        } else {

            if(txtTotalPrice.getText().toString().equals("RM0.00")) {
                Toast.makeText(Cart.this, "Cart must not be empty!", Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
                alertDialog.setTitle("Order Confirmation!");
                alertDialog.setMessage("You will need to pickup your order from our restaurant at:\n\n" +
                        "1-Z, Lebuh Bukit Jambul,\nBukit Jambul,\n11900 Bayan Lepas,\nPulau Pinang");

                LayoutInflater inflater = this.getLayoutInflater();
                View order_request_alertdialog = inflater.inflate(R.layout.order_request_alertdialog,null);

                MaterialEditText requestText = order_request_alertdialog.findViewById(R.id.requestText);
                MaterialEditText telNoText = order_request_alertdialog.findViewById(R.id.telNoText);
                telNoText.setText(Common.currentUser.getCustTelNo());

                alertDialog.setView(order_request_alertdialog);
                alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Order order = new Order(
                                Common.currentUser.getCustID(),
                                telNoText.getText().toString(),
                                "Self-Collect Order",
                                Common.currentOrderType.getOrderType(),
                                txtTotalPrice.getText().toString(),
                                requestText.getText().toString(),
                                cart
                        );

                        //Submit order to firebase
                        orders.child(String.valueOf(System.currentTimeMillis()))
                                .setValue(order);
                        //Delete cart after placing order
                        new Database(getBaseContext()).cleanCart();
                        Toast.makeText(Cart.this, "Thank you, order placed", Toast.LENGTH_SHORT).show();
                        finish();
                    }//end onClick

                });//end alertDialog.setPositiveButton

                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                alertDialog.show();
            }

        }

    }

    private void loadListFood() {
        cart = new Database(this).getCarts();
        adapter = new CartAdapter(cart, this);
        recyclerView.setAdapter(adapter);

        //Calculate total price
        float total = 0;
        for (CartDetail cartDetail : cart) {
            float price;
            int quantity;
            price = Float.parseFloat(cartDetail.getFoodPrice());
            quantity = Integer.parseInt(cartDetail.getQuantity());

            total += price * quantity;
        }
        Locale locale = new Locale("en", "MY");
        //Get currency RM
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txtTotalPrice.setText(fmt.format(total));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Delete")) {
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int position) {
        //Remove item at List<CartDetail> by position
        cart.remove(position);

        //Delete all data from SQLite
        new Database(this).cleanCart();

        //Update new data after deleting item to SQLite
        for(CartDetail item: cart)
            new Database(this).addToCart(item);

        loadListFood();
    }
}