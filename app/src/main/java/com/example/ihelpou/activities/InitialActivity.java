package com.example.ihelpou.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ihelpou.R;
import com.example.ihelpou.classes.GestClassDB;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.os.Bundle;


public class InitialActivity extends AppCompatActivity {

    private NavController navController;
    private GestClassDB gestClassDB = new GestClassDB();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        //NAppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.navigation_request, R.id.navigation_offer, R.id.navigation_pending).build();
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }
}