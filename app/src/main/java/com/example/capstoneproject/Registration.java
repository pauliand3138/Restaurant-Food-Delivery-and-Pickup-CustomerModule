package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.capstoneproject.Common.Common;
import com.example.capstoneproject.Model.Customer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class Registration extends AppCompatActivity {

    MaterialEditText nameText;
    MaterialEditText usernameText;
    MaterialEditText passwordText;
    MaterialEditText retypePasswordText;
    MaterialEditText telNoText;
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

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference customerTable = database.getReference("Customer");

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                customerTable.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            if (passwordText.getText().toString().equals(retypePasswordText.getText().toString())) {
                                //Username already exist
                                if (snapshot.child(usernameText.getText().toString()).exists()) {
                                    Toast.makeText(Registration.this, "Username already exist!", Toast.LENGTH_SHORT).show();

                                } else {
                                    Customer customer = new Customer(nameText.getText().toString(), passwordText.getText().toString(), telNoText.getText().toString());
                                    customerTable.child(usernameText.getText().toString()).setValue(customer);
                                    Toast.makeText(Registration.this, "Registration Successful! ", Toast.LENGTH_SHORT).show();
                                    finish(); //close activity
                                }
                            } else {
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
}