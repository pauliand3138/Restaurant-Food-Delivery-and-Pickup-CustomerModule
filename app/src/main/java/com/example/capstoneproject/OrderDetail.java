package com.example.capstoneproject;

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

import com.example.capstoneproject.Common.Common;
import com.example.capstoneproject.ViewHolder.OrderDetailAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class OrderDetail extends AppCompatActivity {

    Button cancelOrderButton;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = findViewById(R.id.order_id);
        orderPhone = findViewById(R.id.order_phone);
        orderAddress = findViewById(R.id.order_address);
        orderTotal = findViewById(R.id.order_price);
        orderRequest = findViewById(R.id.order_request);
        orderStatus = findViewById(R.id.order_status);
        cancelOrderButton = findViewById(R.id.cancelOrderButton);

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
                } else if(!Common.currentOrder.getStatus().equals("0")) {
                    Toast.makeText(OrderDetail.this, "Order is preparing. Unable to cancel order", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderDetail.this);
                    alertDialog.setTitle("Cancel Confirmation!");
                    alertDialog.setMessage("Are you sure to cancel this order?\nThis action cannot be undone");
                    alertDialog.setIcon(R.drawable.ic_baseline_warning_24);

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(!Common.currentOrder.getStatus().equals("0")) {
                                Toast.makeText(OrderDetail.this, Common.currentOrder.getStatus(), Toast.LENGTH_SHORT).show();
                            } else {
                                database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
                                orders = database.getReference("Order");
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
        ;

    }

    private String convertCodeToStatus(String status) {
        if(status.equals("0"))
            return "Placed";
        else if(status.equals("1"))
            return "On my way";
        else if(status.equals("2"))
            return "Order prepared, waiting for pickup";
        else if(status.equals("3"))
            return "Delivered";
        else
            return "Cancelled";
    }
}