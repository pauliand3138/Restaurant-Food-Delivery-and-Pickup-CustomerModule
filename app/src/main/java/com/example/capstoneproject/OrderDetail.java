package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.disklrucache.DiskLruCache;
import com.example.capstoneproject.Common.Common;
import com.example.capstoneproject.Model.Rating;
import com.example.capstoneproject.ViewHolder.OrderDetailAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import org.w3c.dom.Text;

import java.util.Arrays;

public class OrderDetail extends AppCompatActivity implements RatingDialogListener {

    Button cancelOrderButton;
    Button rateOrderButton;

    TextView orderId;
    TextView orderPhone;
    TextView orderAddress;
    TextView orderTime;
    TextView orderTotal;
    TextView orderRequest;
    TextView orderStatus;
    ImageView statusImage;
    TextView orderSchedule;
    String orderIdValue = "";
    String orderedFoods = "";
    RecyclerView foodList;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference orders;
    DatabaseReference ratings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        orders = database.getReference("Order");
        ratings = database.getReference("Rating");

        orderId = findViewById(R.id.order_id);
        orderPhone = findViewById(R.id.order_phone);
        orderAddress = findViewById(R.id.order_address);
        orderTime = findViewById(R.id.order_time);
        orderTotal = findViewById(R.id.order_price);
        orderRequest = findViewById(R.id.order_request);
        orderStatus = findViewById(R.id.order_status);
        cancelOrderButton = findViewById(R.id.cancelOrderButton);
        rateOrderButton = findViewById(R.id.rateButton);
        statusImage = findViewById(R.id.status_image);
        orderSchedule = findViewById(R.id.order_schedule);
        foodList = findViewById(R.id.foodList);
        foodList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        foodList.setLayoutManager(layoutManager);

        if(getIntent() != null) {
            orderIdValue = getIntent().getStringExtra("OrderId");
        }

        ratings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(orderIdValue).exists()) {
                    rateOrderButton.setText("Order Rated");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentOrder.getFoods());
        adapter.notifyDataSetChanged();
        foodList.setAdapter(adapter);

        orderId.setText(String.format("Order # ") + orderIdValue);
        orderPhone.setText(Common.currentOrder.getOrderTelNo());
        orderTime.setText(Common.getDate(Long.parseLong(orderIdValue)));
        orderAddress.setText(Common.currentOrder.getOrderAddress());
        if(Common.currentOrder.getScheduledTime().equals("")) {
            orderSchedule.setText("Now");
        } else {
            orderSchedule.setText(Common.currentOrder.getScheduledTime());
        }
        orderTotal.setText(Common.currentOrder.getOrderPrice());
        if (Common.currentOrder.getOrderRequest().equals("")) {
            orderRequest.setText("None");
        } else {
            orderRequest.setText(Common.currentOrder.getOrderRequest());
        }

        //Set Image displayed for different order status
        orderStatus.setText(convertCodeToStatus(Common.currentOrder.getStatus()));
        if(convertCodeToStatus(Common.currentOrder.getStatus()).equals("Placed")) {
            statusImage.setImageResource(R.drawable.placedimage_trans);
        }
        else if (convertCodeToStatus(Common.currentOrder.getStatus()).equals("Preparing")) {
            statusImage.setImageResource(R.drawable.preparingimage_trans);
        }
        else if (convertCodeToStatus(Common.currentOrder.getStatus()).equals("Delivering")) {
            statusImage.setImageResource(R.drawable.deliveringimage_trans);
        }
        else if (convertCodeToStatus(Common.currentOrder.getStatus()).equals("Ready to Pickup")) {
            statusImage.setImageResource(R.drawable.readytopickupimage_trans);
        }
        else if (convertCodeToStatus(Common.currentOrder.getStatus()).equals("Completed")) {
            statusImage.setImageResource(R.drawable.completed_v2);
        }
        else {
            statusImage.setImageResource(R.drawable.cancelledimage_trans);
        }





        orderedFoods = TextUtils.join(", ", adapter.getFoodsName());


        cancelOrderButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                //Display Toast message if order status is not "Placed"
                if(Common.currentOrder.getStatus().equals("-1") || Common.currentOrder.getStatus().equals("-2") ) {
                    Toast.makeText(OrderDetail.this, "Order is already cancelled", Toast.LENGTH_SHORT).show();
                }
                else if(Common.currentOrder.getStatus().equals("1")) {
                    Toast.makeText(OrderDetail.this, "Unable to cancel preparing orders", Toast.LENGTH_SHORT).show();
                }
                else if((Common.currentOrder.getStatus().equals("2")) || (Common.currentOrder.getStatus().equals("3"))){
                    Toast.makeText(OrderDetail.this, "Unable to cancel prepared orders", Toast.LENGTH_SHORT).show();
                }
                else if(Common.currentOrder.getStatus().equals("4")) {
                    Toast.makeText(OrderDetail.this, "Unable to cancel completed orders", Toast.LENGTH_SHORT).show();
                }
                else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetail.this);
                    alertDialog.setTitle("Cancel Confirmation!");
                    alertDialog.setMessage("Are you sure to cancel this order?\nThis action cannot be undone");
                    alertDialog.setIcon(R.drawable.ic_baseline_warning_24);

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                                orders.child(orderIdValue).child("status").setValue("-1");
                                orders.child(orderIdValue).child("custIDStatusFilter").setValue(Common.currentUser.getCustID() + "-1");
                                orders.child(orderIdValue).child("adminFilter").setValue("-1");
                                Common.currentOrder.setStatus("-1");
                                Toast.makeText(OrderDetail.this, "Order cancelled", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();

                                //refresh activity
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);

                        }
                    });
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        });

        rateOrderButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!Common.currentOrder.getStatus().equals("4")) {
                    Toast.makeText(OrderDetail.this, "Only completed orders can be rated", Toast.LENGTH_SHORT).show();
                } else if (rateOrderButton.getText().equals("Order Rated")) {
                    Toast.makeText(OrderDetail.this,"Order already has a rating!",Toast.LENGTH_SHORT).show();
                } else {
                    showRatingDialog();

                }

            }
        });
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Needs Improvement","Moderate","Good","Excellent"))
                .setDefaultRating(5)
                .setTitle("Rate this order")
                .setDescription("How's your order? We would like to have your honest feedback!")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Comment")
                .setHintTextColor(R.color.white)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.grey)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(OrderDetail.this)
                .show();
    }

    @Override
    public void onPositiveButtonClicked(int starValue, @NonNull String comment) {

        StringBuilder builder = new StringBuilder(Common.currentUser.getCustID());
        String repeat = new String(new char[builder.length()-4]).replace("\0","*");
        builder.replace(4,builder.length(),repeat);

        ratings.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Rating rating = new Rating(orderIdValue, String.valueOf(starValue), comment.trim(), builder.toString(), orderedFoods);
                ratings.child(orderIdValue).setValue(rating);
                Toast.makeText(OrderDetail.this, "Order rated successfully!", Toast.LENGTH_SHORT).show();

                //refresh activity
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onNegativeButtonClicked() {

    }



    private String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "Preparing";
        else if(status.equals("2"))
            return "Delivering";
        else if(status.equals("3"))
            return "Ready to Pickup";
        else if(status.equals("4"))
            return "Completed";
        else if(status.equals("-1"))
            return "Cancelled by You";
        else
            return "Cancelled by Restaurant";
    }


}