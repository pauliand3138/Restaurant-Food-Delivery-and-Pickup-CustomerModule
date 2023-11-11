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
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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

    String restOpening = Common.currentRestaurant.getRestOpening().substring(0, Common.currentRestaurant.getRestOpening().length() - 3);
    String restLastOrder = Common.currentRestaurant.getRestLastOrderTime().substring(0, Common.currentRestaurant.getRestLastOrderTime().length() - 3);

    String[] openingHourMinute = restOpening.split(":");
    double openingHour = Double.parseDouble(openingHourMinute[0]);
    int openingMinute = Integer.parseInt(openingHourMinute[1]);

    String[] lastOrderHourMinute = restLastOrder.split(":");
    double lastOrderHour = Double.parseDouble(lastOrderHourMinute[0]);
    int lastOrderMinute = Integer.parseInt(lastOrderHourMinute[1]);

    Calendar timeNow;
    double currentHour;
    double currentMinute;


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
                timeNow = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));

                currentHour = timeNow.get(Calendar.HOUR_OF_DAY);
                currentMinute = timeNow.get(Calendar.MINUTE);

                if ((currentHour > lastOrderHour) || ((currentHour == lastOrderHour) && (currentMinute > lastOrderMinute))) {
                    Toast.makeText(Cart.this,"Sorry, our restaurant is closed.",Toast.LENGTH_SHORT).show();
                } else {
                    //Create new order
                    showAlertDialog();
                }
            }
        });

        loadListFood();


    }

    private void showAlertDialog() {

        //if orderType is delivery
        if (Common.currentOrderType.getOrderType().equals("Delivery")) {

            if(txtTotalPrice.getText().toString().equals("RM 0.00")) {
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

                Spinner spinner = order_address_alertdialog.findViewById(R.id.spinner);

                timeNow = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));

                currentHour = timeNow.get(Calendar.HOUR_OF_DAY);
                currentMinute = timeNow.get(Calendar.MINUTE);

                int count = 1;

                double scheduledOpen;
                double scheduledClose;

            //Drop down list logic - start
                if (currentHour < openingHour) {
                    if(openingMinute > 0) {
                        scheduledOpen = openingHour + 0.5;
                    } else {
                        scheduledOpen = openingHour;
                    }
                    currentMinute = openingMinute;
                } else {
                    scheduledOpen = currentHour+1;
                }

                if (lastOrderMinute > 0) {
                    scheduledClose = lastOrderHour + 0.5;
                } else {
                    scheduledClose = lastOrderHour;
                }

                List<String> timeChoice = new ArrayList<>();
                String amPM = "";
                timeChoice.add("Choose a time:");

                for(double i = scheduledOpen; i <= scheduledClose; i+=0.5) {
                    if (i < 12) {
                        amPM = "AM";
                    } else {
                        amPM = "PM";
                    }
                    if (!(currentMinute >= 30)) {
                        if (count % 2 == 0) {
                            timeChoice.add((int)i + ":" + "30 " + amPM);
                        } else {
                            timeChoice.add((int)i + ":" + "00 " + amPM);
                        }
                    } else {
                        if (count == 1) {
                            timeChoice.add((int)i + ":" + "30 " + amPM);
                            count++;
                            i++;
                        }
                        if (count % 2 == 0) {
                            timeChoice.add((int)i + ":" + "00 " + amPM);
                        } else {
                            timeChoice.add((int)i + ":" + "30 " + amPM);
                        }
                    }
                    count++;
                }
            //Drop down list logic - end

                currentMinute = timeNow.get(Calendar.MINUTE);
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(Cart.this, android.R.layout.simple_list_item_1, timeChoice);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);

                RadioGroup radioGroup = order_address_alertdialog.findViewById(R.id.radioGroup);
                RadioButton deliverNowButton = order_address_alertdialog.findViewById(R.id.radioDeliveryNow);

                deliverNowButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        spinner.setVisibility(View.INVISIBLE);
                    }
                });
                RadioButton deliverLaterButton = order_address_alertdialog.findViewById(R.id.radioDeliveryLater);
                deliverLaterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        spinner.setVisibility(View.VISIBLE);
                    }
                });
                if ((currentHour < openingHour) || ((currentHour == openingHour) && (currentMinute < openingMinute))) {
                    deliverNowButton.setEnabled(false);
                    deliverLaterButton.setChecked(true);
                    spinner.setVisibility(View.VISIBLE);
                }
                alertDialog.setView(order_address_alertdialog);
                alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Order order = new Order();
                        if(addressText.getText().toString().isEmpty()) {
                            Toast.makeText(Cart.this, "Address must not be empty!", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();

                        } else if (deliverLaterButton.isChecked() && spinner.getSelectedItem().toString().equals("Choose a time:")) {
                            Toast.makeText(Cart.this, "Please choose a specific delivery time!", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        } else {
                            if(deliverNowButton.isChecked()) {
                                order = new Order(
                                        Common.currentUser.getCustID(),
                                        telNoText.getText().toString(),
                                        addressText.getText().toString(),
                                        Common.currentOrderType.getOrderType(),
                                        txtTotalPrice.getText().toString(),
                                        requestText.getText().toString(),
                                        cart,
                                        "false",
                                        "",
                                        "false"
                                );
                            } else if (deliverLaterButton.isChecked()) {
                                order = new Order(
                                        Common.currentUser.getCustID(),
                                        telNoText.getText().toString(),
                                        addressText.getText().toString(),
                                        Common.currentOrderType.getOrderType(),
                                        txtTotalPrice.getText().toString(),
                                        requestText.getText().toString(),
                                        cart,
                                        "true",
                                        spinner.getSelectedItem().toString(),
                                        "false"
                                );
                            }


                            //Submit order to firebase
                            String id = String.valueOf(System.currentTimeMillis());
                            orders.child(id).setValue(order);
                            orders.child(id).child("notification").setValue("false");


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

            if(txtTotalPrice.getText().toString().equals("RM 0.00")) {
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

                Spinner spinner = order_request_alertdialog.findViewById(R.id.spinner);

                timeNow = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));

                currentHour = timeNow.get(Calendar.HOUR_OF_DAY);
                currentMinute = timeNow.get(Calendar.MINUTE);

                int count = 1;

                double scheduledOpen;
                double scheduledClose;

            //Drop down list logic - start
                if (currentHour < openingHour) {
                    if(openingMinute > 0) {
                        scheduledOpen = openingHour + 0.5;
                    } else {
                        scheduledOpen = openingHour;
                    }
                    currentMinute = openingMinute;
                } else {
                    scheduledOpen = currentHour+1;
                }

                if (lastOrderMinute > 0) {
                    scheduledClose = lastOrderHour + 0.5;
                } else {
                    scheduledClose = lastOrderHour;
                }

                List<String> timeChoice = new ArrayList<>();
                String amPM = "";
                timeChoice.add("Choose a time:");


                for(double i = scheduledOpen; i <= scheduledClose; i+=0.5) {
                    if (i < 12) {
                        amPM = "AM";
                    } else {
                        amPM = "PM";
                    }
                    if (!(currentMinute >= 30)) {
                        if (count % 2 == 0) {
                            timeChoice.add((int)i + ":" + "30 " + amPM);
                        } else {
                            timeChoice.add((int)i + ":" + "00 " + amPM);
                        }
                    } else {
                        if (count == 1) {
                            timeChoice.add((int)i + ":" + "30 " + amPM);
                            count++;
                            i++;
                        }
                        if (count % 2 == 0) {
                            timeChoice.add((int)i + ":" + "00 " + amPM);
                        } else {
                            timeChoice.add((int)i + ":" + "30 " + amPM);
                        }
                    }
                    count++;
                }
            //Drop down list logic - end

                currentMinute = timeNow.get(Calendar.MINUTE);
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(Cart.this, android.R.layout.simple_list_item_1, timeChoice);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(spinnerAdapter);

                RadioGroup radioGroup = order_request_alertdialog.findViewById(R.id.radioGroup);
                RadioButton collectNowButton = order_request_alertdialog.findViewById(R.id.radioCollectNow);
                collectNowButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        spinner.setVisibility(View.INVISIBLE);
                    }
                });
                RadioButton collectLaterButton = order_request_alertdialog.findViewById(R.id.radioCollectLater);
                collectLaterButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        spinner.setVisibility(View.VISIBLE);
                    }
                });
                if ((currentHour < openingHour) || ((currentHour == openingHour) && (currentMinute < openingMinute))) {
                    collectNowButton.setEnabled(false);
                    collectLaterButton.setChecked(true);
                    spinner.setVisibility(View.VISIBLE);
                }
                alertDialog.setView(order_request_alertdialog);
                alertDialog.setIcon(R.drawable.ic_baseline_shopping_cart_24);

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Order order = new Order();
                        if (collectLaterButton.isChecked() && spinner.getSelectedItem().toString().equals("Choose a time:")) {
                            Toast.makeText(Cart.this, "Please choose a specific collect time!", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        } else {
                            if (collectNowButton.isChecked()) {
                                order = new Order(
                                        Common.currentUser.getCustID(),
                                        telNoText.getText().toString(),
                                        "Self-Collect Order",
                                        Common.currentOrderType.getOrderType(),
                                        txtTotalPrice.getText().toString(),
                                        requestText.getText().toString(),
                                        cart,
                                        "false",
                                        "",
                                        "false"
                                );
                            } else if (collectLaterButton.isChecked()) {
                                order = new Order(
                                        Common.currentUser.getCustID(),
                                        telNoText.getText().toString(),
                                        "Self-Collect Order",
                                        Common.currentOrderType.getOrderType(),
                                        txtTotalPrice.getText().toString(),
                                        requestText.getText().toString(),
                                        cart,
                                        "true",
                                        spinner.getSelectedItem().toString(),
                                        "false"
                                );
                            }

                            //Submit order to firebase
                            String id = String.valueOf(System.currentTimeMillis());
                            orders.child(id).setValue(order);
                            orders.child(id).child("notification").setValue("false");

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

        txtTotalPrice.setText("RM " + String.format("%.2f",total));
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if(item.getTitle().equals("Delete")) {
            deleteCart(item.getOrder());
        }
        return true;
    }

    private void deleteCart(int position) {

        CartDetail deletedItem = cart.get(position);

        Toast.makeText(Cart.this, deletedItem.getQuantity() + "x " + deletedItem.getFoodName() +
                        " is deleted", Toast.LENGTH_SHORT).show();

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