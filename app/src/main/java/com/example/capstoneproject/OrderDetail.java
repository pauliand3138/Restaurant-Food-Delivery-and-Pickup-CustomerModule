package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
    TextView orderTotal;
    TextView orderRequest;
    TextView orderStatus;
    String orderIdValue = "";

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
        orderTotal = findViewById(R.id.order_price);
        orderRequest = findViewById(R.id.order_request);
        orderStatus = findViewById(R.id.order_status);
        cancelOrderButton = findViewById(R.id.cancelOrderButton);
        rateOrderButton = findViewById(R.id.rateButton);

        foodList = findViewById(R.id.foodList);
        foodList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        foodList.setLayoutManager(layoutManager);

        if(getIntent() != null) {
            orderIdValue = getIntent().getStringExtra("OrderId");
        }


        orderId.setText(String.format("Order ID:              ") + orderIdValue);
        orderPhone.setText(String.format("Contact No.            ") + Common.currentUser.getCustTelNo());
        orderAddress.setText(String.format("Address:                 ") + Common.currentOrder.getOrderAddress());
        orderTotal.setText(String.format("Order Total:            ") + Common.currentOrder.getOrderPrice());
        orderRequest.setText(String.format("Extra Request:       ") + Common.currentOrder.getOrderRequest());
        orderStatus.setText(String.format("Order Status:   ") + convertCodeToStatus(Common.currentOrder.getStatus()));

        OrderDetailAdapter adapter = new OrderDetailAdapter(Common.currentOrder.getFoods());
        adapter.notifyDataSetChanged();
        foodList.setAdapter(adapter);

        cancelOrderButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if(Common.currentOrder.getStatus().equals("-1")) {
                    Toast.makeText(OrderDetail.this, "Order is already cancelled", Toast.LENGTH_SHORT).show();
                } else if(Common.currentOrder.getStatus().equals("1")) {
                    Toast.makeText(OrderDetail.this, "Order is preparing. Unable to cancel order", Toast.LENGTH_SHORT).show();
                } else if(Common.currentOrder.getStatus().equals("2")) {
                    Toast.makeText(OrderDetail.this, "Order is already prepared. Unable to cancel order", Toast.LENGTH_SHORT).show();
                } else if(Common.currentOrder.getStatus().equals("3")) {
                    Toast.makeText(OrderDetail.this, "Order already complete. Unable to cancel order", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetail.this);
                    alertDialog.setTitle("Cancel Confirmation!");
                    alertDialog.setMessage("Are you sure to cancel this order?\nThis action cannot be undone");
                    alertDialog.setIcon(R.drawable.ic_baseline_warning_24);

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                                orders.child(orderIdValue).child("status").setValue("-1");
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
                    ;
                }
            }
        });

        rateOrderButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(!Common.currentOrder.getStatus().equals("3")) {
                    Toast.makeText(OrderDetail.this, "Only completed orders can be rated", Toast.LENGTH_SHORT).show();
                } else if(rateOrderButton.getText().toString().equals("Order Rated")) {
                    Toast.makeText(OrderDetail.this, "Order already has a rating!", Toast.LENGTH_SHORT).show();
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
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(R.color.white)
                .setCommentBackgroundColor(R.color.grey)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(OrderDetail.this)
                .show();
    }

    @Override
    public void onPositiveButtonClicked(int starValue, @NonNull String comment) {
        Rating rating = new Rating(orderIdValue, String.valueOf(starValue), comment, Common.currentUser.getCustID());

        //Rating table in Firebase
        ratings.child(orderIdValue).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ratings.child(orderIdValue).setValue(rating);

                Toast.makeText(OrderDetail.this, "Thanks for your rating!", Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(0, 0);
                startActivity(getIntent());
                overridePendingTransition(0, 0);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });;
    }

    @Override
    public void onNegativeButtonClicked() {

    }



    private String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On my way";
        else if(status.equals("2"))
            return "Order prepared, waiting for pickup";
        else if(status.equals("3"))
            return "Completed";
        else
            return "Cancelled";
    }


}