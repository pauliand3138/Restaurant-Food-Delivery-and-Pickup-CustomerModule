package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.capstoneproject.Common.Common;
import com.example.capstoneproject.Model.Customer;
import com.example.capstoneproject.Model.EncryptDecrypt;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Login extends AppCompatActivity {

    MaterialEditText usernameText;
    MaterialEditText passwordText;
    String encryptedPassword;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        loginButton = findViewById(R.id.loginButton);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference customerTable = database.getReference("Customer");


        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            encryptedPassword = EncryptDecrypt.encrypt(passwordText.getText().toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                customerTable.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //Check whether or not username is exist in database
                            if (snapshot.child(usernameText.getText().toString()).exists()) {

                                //Get customer information
                                Customer customer = snapshot.child(usernameText.getText().toString()).getValue(Customer.class);
                                customer.setCustID(usernameText.getText().toString());

                                if (customer.getCustPassword().equals(encryptedPassword)){
                                    Toast.makeText(Login.this, "Login successful!", Toast.LENGTH_SHORT).show();
                                    Intent orderTypeIntent = new Intent(Login.this, OrderType.class);
                                    Common.currentUser = customer;
                                    startActivity(orderTypeIntent);
                                    finish();

                                }else {
                                    Toast.makeText(Login.this, "Password incorrect! Please try again!", Toast.LENGTH_SHORT).show();
                                }

                            } else {

                                Toast.makeText(Login.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                 });
            }
        });
    }
}