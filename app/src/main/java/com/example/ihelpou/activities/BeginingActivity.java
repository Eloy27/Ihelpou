package com.example.ihelpou.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.AvailableDays;
import com.example.ihelpou.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ihelpou.ui.main.SectionsPagerAdapter;
import com.example.ihelpou.databinding.ActivityBeginingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class BeginingActivity extends AppCompatActivity {

    private ActivityBeginingBinding binding;
    private User user;
    private GestClassDB gestClassDB = new GestClassDB();
    public static String where = "Request";
    private FirebaseAuth fsAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBeginingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        user = (User) i.getSerializableExtra("user");

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), user, 2);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

        checkPendingAid(binding, this, user);

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                where = tab.getText().toString();
                if (tab.getText().equals("Offer")) {
                    gestClassDB.checkAvailability(user, null, getApplicationContext(), "BeginingActivity");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    public void checkPendingAid(ActivityBeginingBinding binding, Context c, User user) {
        fsDB.collection("User").document(user.getEmail()).collection("AvailableDay")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            for (QueryDocumentSnapshot objectAvailableDay : query.getResult()) {
                                ObjectMapper mapper = new ObjectMapper();
                                AvailableDays availableDays = new AvailableDays(objectAvailableDay.getId(), mapper.convertValue(objectAvailableDay.get("day"), ArrayList.class), String.valueOf(objectAvailableDay.get("startTime")), String.valueOf(objectAvailableDay.get("finishTime")));
                                for (HashMap mapDay : availableDays.getAvailableDays()) {
                                    fsDB.collection("User")
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> query) {
                                                    if (query.isSuccessful()) {
                                                        for (QueryDocumentSnapshot objectUser : query.getResult()) {
                                                            fsDB.collection("User").document(objectUser.getId()).collection("Aid")
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> query) {
                                                                            if (query.isSuccessful()) {
                                                                                for (QueryDocumentSnapshot objectAid : query.getResult()) {
                                                                                    if (mapDay.get("idAid").toString().equals(objectAid.getId())) {
                                                                                        Aid aid = objectAid.toObject(Aid.class);
                                                                                        aid.setKey(objectAid.getId());
                                                                                        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(c, getSupportFragmentManager(), user, 3);
                                                                                        ViewPager viewPager = binding.viewPager;
                                                                                        viewPager.setAdapter(sectionsPagerAdapter);
                                                                                        TabLayout tabs = binding.tabs;
                                                                                        tabs.setupWithViewPager(viewPager);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            Toast.makeText(c, "Unexpected Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}