package com.example.capstoneproject.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.capstoneproject.Common.Common;
import com.example.capstoneproject.Model.Order;
import com.example.capstoneproject.OrderStatus;
import com.example.capstoneproject.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

public class ListenOrder extends Service implements ChildEventListener {

    FirebaseDatabase database;
    DatabaseReference orders;


    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate() {
        super.onCreate();
        database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        orders = database.getReference("Order");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        orders.addChildEventListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        Order order = snapshot.getValue(Order.class);
        showNotification(snapshot.getKey(),order);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showNotification(String key, Order order) {
        Intent intent = new Intent(this, OrderStatus.class);
        intent.putExtra("custID", order.getCustID());
        PendingIntent contentIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if( order.getCustID().equals(Common.currentUser.getCustID())){

            if (!order.getStatus().equals("-1")){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel =
                            new NotificationChannel("foodStatus", "foodStatus", NotificationManager.IMPORTANCE_DEFAULT);
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);

                }

                Notification.Builder notification = new Notification.Builder(this, "foodStatus");

                String text = "";

                if (Integer.parseInt(order.getStatus()) >= 0 && Integer.parseInt(order.getStatus()) <= 4)
                    text = " is ";
                else
                    text = " has been ";

                notification.setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setWhen(System.currentTimeMillis())
                        .setTicker("INTI Restaurant")
                        .setContentInfo("Your order was updated")
                        .setContentText("Your order #" + key + text + Common.convertCodeToStatus(order.getStatus()).toLowerCase())
                        .setContentIntent(contentIntent)
                        .setContentInfo("Info")
                        .setSmallIcon(R.drawable.init_logo);

                NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(1, notification.build());
            }
        }
    }

    @Override
    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

    }

    @Override
    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}