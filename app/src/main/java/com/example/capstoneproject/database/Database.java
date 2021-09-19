package com.example.capstoneproject.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.capstoneproject.Model.CartDetail;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

//Process SQLiteDatabase
public class Database extends SQLiteAssetHelper{
    private static final String DB_NAME = "CartSQLiteDatabase_v2.db";
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }



    @SuppressLint("Range")
    public List<CartDetail> getCarts() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"FoodID", "FoodName", "Quantity", "Price"};
        String sqlTable = "CartDetail";

        qb.setTables(sqlTable);
        Cursor c = qb.query(db, sqlSelect, null, null, null, null, null);

        final List<CartDetail> result = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                result.add(new CartDetail(c.getString(c.getColumnIndex("FoodID")),
                        c.getString(c.getColumnIndex("FoodName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price"))
                ));
            } while (c.moveToNext());
        }
        return result;
    }

    public void addToCart(CartDetail cartDetail) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO CartDetail(FoodID,FoodName,Quantity,Price) VALUES('%s','%s','%s','%s');",
                cartDetail.getFoodID(),
                cartDetail.getFoodName(),
                cartDetail.getQuantity(),
                cartDetail.getFoodPrice());
        db.execSQL(query);

    }

    public void cleanCart() {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM CartDetail");
        db.execSQL(query);
    }

}
