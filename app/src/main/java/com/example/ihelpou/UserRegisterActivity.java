package com.example.ihelpou;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

public class UserRegisterActivity extends AppCompatActivity {

    private EditText nameET, usernameET, passwordET;
    private ImageButton backBtn, avatarBtn;
    private Button createBtn;
    GestUserDB gestUserDB = new GestUserDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_register);

        backBtn = findViewById(R.id.backBtn);
        nameET = findViewById(R.id.nameET);
        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passwordET);
        avatarBtn = findViewById(R.id.avatarBtn);
        avatarBtn.setImageResource(R.drawable.avatar);
        createBtn = findViewById(R.id.createBtn);


    }

    public void comeBack(View view){
        onBackPressed();
    }

    public void createUser(View view){
        User user = new User(nameET.getText().toString(), usernameET.getText().toString(), passwordET.getText().toString());
        gestUserDB.addUser(user).addOnSuccessListener(suc ->
        {
            Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(err ->
        {
            Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}