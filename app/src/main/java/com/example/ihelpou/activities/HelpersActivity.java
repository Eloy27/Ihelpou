package com.example.ihelpou.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ihelpou.R;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class HelpersActivity extends AppCompatActivity {

    private GestClassDB gestClassDB = new GestClassDB();
    private ArrayList<User> listHelpersAvailables = new ArrayList<>();
    private RecyclerView listHelpersAvailablesRV;
    private Aid aid;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpers);
        listHelpersAvailablesRV = findViewById(R.id.listHelpersAvailablesRV);

        Intent i = getIntent();
        aid = (Aid)i.getSerializableExtra("aid");
        user = (User)i.getSerializableExtra("user");

        listHelpersAvailablesRV.setLayoutManager(new LinearLayoutManager(this));
        gestClassDB.getHelpersAccordingAid(listHelpersAvailables, listHelpersAvailablesRV, aid, user, this);

    }

    public void comeBack(View view){
        Intent i = new Intent(this, BeginingActivity.class);
        i.putExtra("user", user);
        startActivity(i);
    }
}