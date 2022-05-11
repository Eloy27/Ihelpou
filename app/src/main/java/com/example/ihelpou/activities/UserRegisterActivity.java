package com.example.ihelpou.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ihelpou.R;
import com.example.ihelpou.activities.MainActivity;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.models.User;

public class UserRegisterActivity extends AppCompatActivity {

    private EditText nameET, usernameET, passwordET, surnameET, addressET, phoneET, emailET, ageET;
    private ImageButton backBtn, avatarBtn;
    private Button createBtn;
    private GestClassDB gestClassDB = new GestClassDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        backBtn = findViewById(R.id.backBtn);
        nameET = findViewById(R.id.nameET);
        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passwordET);
        surnameET = findViewById(R.id.surnameET);
        addressET = findViewById(R.id.addressET);
        phoneET = findViewById(R.id.phoneET);
        emailET = findViewById(R.id.emailET);
        ageET = findViewById(R.id.ageET);

        avatarBtn = findViewById(R.id.avatarBtn);
        avatarBtn.setImageResource(R.drawable.avatar);
        createBtn = findViewById(R.id.createBtn);
    }

    public void comeBack(View view){
        onBackPressed();
    }

    public void createUser(View view){
        User user = new User(nameET.getText().toString(), usernameET.getText().toString(), passwordET.getText().toString(),
                surnameET.getText().toString(), phoneET.getText().toString(), addressET.getText().toString(),
                Integer.parseInt(ageET.getText().toString()), emailET.getText().toString());

        gestClassDB.addUser(user).addOnSuccessListener(suc ->
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(err ->
        {
            Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}