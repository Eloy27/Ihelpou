package com.example.ihelpou.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.ihelpou.R;
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
        createBtn = findViewById(R.id.okBtn);

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(emailET.getText().toString(), nameET.getText().toString(), usernameET.getText().toString(), passwordET.getText().toString(),
                        surnameET.getText().toString(), phoneET.getText().toString(), addressET.getText().toString(),
                        Integer.parseInt(ageET.getText().toString()));

                gestClassDB.registerUser(user, getApplicationContext());
            }
        });
    }

    public void comeBack(View view){
        onBackPressed();
    }

}