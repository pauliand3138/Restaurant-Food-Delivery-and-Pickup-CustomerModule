package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Registration extends AppCompatActivity {

    MaterialEditText nameText;
    MaterialEditText usernameText;
    MaterialEditText passwordText;
    MaterialEditText retypePasswordText;
    MaterialEditText telNoText;
    String encryptedPassword;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        nameText = findViewById(R.id.nameText);
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        retypePasswordText = findViewById(R.id.retypePasswordText);
        telNoText = findViewById(R.id.telNoText);
        registerButton = findViewById(R.id.registerButton);
//        encryptedPassword = findViewById(R.id.textView);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference customerTable = database.getReference("Customer");

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        encryptedPassword = EncryptDecrypt.encrypt(passwordText.getText().toString());
                    } catch (NoSuchPaddingException | NoSuchAlgorithmException | InvalidAlgorithmParameterException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                        e.printStackTrace();
                    }
                }

                customerTable.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (nameText.getText().toString().equals("") || usernameText.getText().toString().equals("") || passwordText.getText().toString().equals("") ||
                                retypePasswordText.getText().toString().equals("") || nameText.getText().toString().equals("")) {
                                Toast.makeText(Registration.this, "All fields must not be empty!", Toast.LENGTH_SHORT).show();

                            } else if ((passwordText.getText().toString().equals(retypePasswordText.getText().toString())) && (!passwordText.getText().toString().equals(""))) {
                                //Username already exist
                                if (snapshot.child(usernameText.getText().toString()).exists()) {
                                    Toast.makeText(Registration.this, "Username already exist!", Toast.LENGTH_SHORT).show();

                                }
                                else if (passwordText.getText().toString().length()  < 8 || retypePasswordText.getText().toString().length() < 8){
                                    Toast.makeText(Registration.this, "Password must have at least 8 characters!", Toast.LENGTH_SHORT).show();
                                }
                                else if(!passwordText.getText().toString().matches( "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,15}$") || !retypePasswordText.getText().toString().matches( "^(?=.*[0-9])(?=.*[a-z])(?=.*[!@#$%^&*+=?-]).{8,15}$")){
                                    Toast.makeText(Registration.this, "Password must contain at least 1 special character!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Customer customer = new Customer(nameText.getText().toString(), encryptedPassword, telNoText.getText().toString());
                                    customerTable.child(usernameText.getText().toString()).setValue(customer);
                                    Toast.makeText(Registration.this, "Registration successful! ", Toast.LENGTH_SHORT).show();
                                    finish(); //close activity
                                }

                            }

                            else {
                                Toast.makeText(Registration.this, "Both passwords must be the same!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

            }
        });
    }
//    private void computeMD5Hash(String password){
//        try {
//            //Create MD5 HASH
//            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
//            digest.update(password.getBytes());
//            byte messageDigest[] = digest.digest();
//
//            StringBuffer MD5Hash = new StringBuffer();
//            for (int i=0; i< messageDigest.length; i++)
//            {
//                String h = Integer.toHexString(0xff & messageDigest[i]);
//                while (h.length() < 2)
//                    h = "0" + h;
//                MD5Hash.append(h);
//            }
//            encryptedPassword.setText(MD5Hash);
//
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//        }
//    }
}