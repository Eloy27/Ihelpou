package com.example.ihelpou;

import android.content.Intent;
import android.os.Bundle;

import com.example.ihelpou.models.User;
import com.google.android.material.tabs.TabLayout;

import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ihelpou.ui.main.SectionsPagerAdapter;
import com.example.ihelpou.databinding.ActivityBeginingBinding;

public class BeginingActivity extends AppCompatActivity {

    private ActivityBeginingBinding binding;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBeginingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent i = getIntent();
        user = (User)i.getSerializableExtra("user");

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), user);
        ViewPager viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);

    }

    @Override
    public void onBackPressed() { }
}