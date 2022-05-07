package com.example.ihelpou;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText usernameET, passwordET;
    private Button loginBtn, registerBtn;
    GestClassDB gestClassDB = new GestClassDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameET = findViewById(R.id.usernameET);
        passwordET = findViewById(R.id.passwordET);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }

    public void chooseActivity(View view) {
        Intent intent = new Intent(this, UserRegisterActivity.class);
        startActivity(intent);
    }
    
    public void btnLogin(View view){
        gestClassDB.selectUser(usernameET.getText().toString(), passwordET.getText().toString());
        if (gestClassDB.exists){
            Intent intent = new Intent(this, BeginingActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(this, "This user doesn't exist", Toast.LENGTH_SHORT).show();
        }
    }
}