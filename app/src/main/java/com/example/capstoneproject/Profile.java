package com.example.capstoneproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.capstoneproject.Common.Common;

public class Profile extends AppCompatActivity {

    EditText editName;
    EditText editPhone;

    Button updateProfileButton;
    Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        editName = findViewById(R.id.editTextName);
        editPhone = findViewById(R.id.editTextTelNo);
        updateProfileButton = findViewById(R.id.updateProfileButton);
        resetPasswordButton = findViewById(R.id.resetPasswordButton);

        editName.setText(Common.currentUser.getCustName());
        editPhone.setText(Common.currentUser.getCustTelNo());
    }
}