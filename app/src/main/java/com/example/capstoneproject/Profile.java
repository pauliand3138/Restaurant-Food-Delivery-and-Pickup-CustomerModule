package com.example.capstoneproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.capstoneproject.Common.Common;
import com.example.capstoneproject.Model.Customer;
import com.example.capstoneproject.Model.Rating;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;

public class Profile extends AppCompatActivity {

    EditText editName;
    EditText editPhone;

    Button updateProfileButton;
    Button resetPasswordButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://capstoneproject-c2dbe-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference customerTable = database.getReference("Customer");

        editName = findViewById(R.id.editTextName);
        editPhone = findViewById(R.id.editTextTelNo);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);
        updateProfileButton.setEnabled(false);

        editName.setText(Common.currentUser.getCustName());
        editPhone.setText(Common.currentUser.getCustTelNo());

        //Enable button when name is changed
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!editName.getText().toString().trim().equals(Common.currentUser.getCustName())) {   //trim() removes ending and starting white spaces
                    updateProfileButton.setEnabled(true);
                } else {
                    updateProfileButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Enable button when name is changed
        editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!editPhone.getText().toString().trim().equals(Common.currentUser.getCustTelNo())) {
                    updateProfileButton.setEnabled(true);
                } else {
                    updateProfileButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Update values in Firebase after clicking "Update Profile" button
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!editName.getText().toString().isEmpty() && !editPhone.getText().toString().isEmpty()) {
                    Customer currentUser = Common.currentUser;
                    String custID = currentUser.getCustID();
                    String newName = editName.getText().toString().trim();
                    String newPhone = editPhone.getText().toString().trim();

                    customerTable.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            customerTable.child(custID).child("custName").setValue(newName);
                            customerTable.child(custID).child("custTelNo").setValue(newPhone);
                            currentUser.setCustName(newName);
                            currentUser.setCustTelNo(newPhone);
                            Toast.makeText(Profile.this,"Profile updated successfully!", Toast.LENGTH_SHORT).show();

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
                } else {
                    Toast.makeText(Profile.this,"All fields must not be empty!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(Profile.this);
                alertDialog.setTitle("Reset Password");
                alertDialog.setMessage("Please fill in the info below:");

                LayoutInflater inflater = Profile.this.getLayoutInflater();
                View reset_password_alertdialog = inflater.inflate(R.layout.reset_password_alertdialog,null);

                MaterialEditText currentPasswordTxt = reset_password_alertdialog.findViewById(R.id.currentPasswordText);
                MaterialEditText newPasswordTxt = reset_password_alertdialog.findViewById(R.id.newPasswordText);
                MaterialEditText retypePasswordTxt = reset_password_alertdialog.findViewById(R.id.retypeNewPasswordText);

                alertDialog.setView(reset_password_alertdialog);
                alertDialog.setIcon(R.drawable.ic_baseline_lock_24);

                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String currentPassword = Common.currentUser.getCustPassword();

                        //Empty fields
                        if(currentPasswordTxt.getText().toString().equals("") || newPasswordTxt.getText().toString().equals("") ||
                           retypePasswordTxt.getText().toString().equals("")) {
                            Toast.makeText(Profile.this, "All fields must not be empty!", Toast.LENGTH_SHORT).show();

                        //Current password equals to new password
                        } else if(currentPassword.equals(newPasswordTxt.getText().toString())) {
                            Toast.makeText(Profile.this, "New password must not be the same as Current password!", Toast.LENGTH_SHORT).show();

                        //Current password is invalid
                        } else if (!currentPassword.equals(currentPasswordTxt.getText().toString())) {
                            Toast.makeText(Profile.this, "Current password is invalid!", Toast.LENGTH_SHORT).show();

                        //New password and retype new password is different
                        } else if (!newPasswordTxt.getText().toString().equals(retypePasswordTxt.getText().toString())) {
                            Toast.makeText(Profile.this, "Both new and retype new password must be the same!", Toast.LENGTH_SHORT).show();

                        //Valid entry
                        } else {
                            customerTable.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    customerTable.child(Common.currentUser.getCustID()).child("custPassword").setValue(newPasswordTxt.getText().toString());
                                    Common.currentUser.setCustPassword(newPasswordTxt.getText().toString());
                                    Toast.makeText(Profile.this,"Password changed successfully!",Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

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
            }
        });
    }
}