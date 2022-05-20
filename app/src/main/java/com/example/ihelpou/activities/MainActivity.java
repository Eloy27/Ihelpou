package com.example.ihelpou.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ihelpou.R;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.models.User;

public class MainActivity extends AppCompatActivity {

    private EditText emailET, passwordET;
    private Button loginBtn, registerBtn;
    private GestClassDB gestClassDB = new GestClassDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserRegisterActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User(emailET.getText().toString(), passwordET.getText().toString());
                gestClassDB.login(user, getApplication());
            }
        });

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }
}