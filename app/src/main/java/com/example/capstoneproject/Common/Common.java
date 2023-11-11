package com.example.capstoneproject.Common;

import android.text.format.DateFormat;

import com.example.capstoneproject.Model.Customer;
import com.example.capstoneproject.Model.DeliverOrPickup;
import com.example.capstoneproject.Model.Order;
import com.example.capstoneproject.Model.Restaurant;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class Common {

    public static Restaurant currentRestaurant;
    public static Customer currentUser;
    public static DeliverOrPickup currentOrderType;
    public static Order currentOrder;
    public static String getDate(long time) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
        calendar.setTimeInMillis(time);
        int dayCount = calendar.get(Calendar.DAY_OF_WEEK);
        StringBuilder date = new StringBuilder(DateFormat.format("dd-MM-yyyy HH:mm",calendar).toString());
        String day = "";

        switch(dayCount) {
            case 1:
                day = "Sun";
                break;
            case 2:
                day = "Mon";
                break;
            case 3:
                day = "Tue";
                break;
            case 4:
                day = "Wed";
                break;
            case 5:
                day = "Thu";
                break;
            case 6:
                day = "Fri";
                break;
            case 7:
                day = "Sat";
                break;
        }
        return day + " " +date.toString();
    }

    public static String convertCodeToStatus(String status) {
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
