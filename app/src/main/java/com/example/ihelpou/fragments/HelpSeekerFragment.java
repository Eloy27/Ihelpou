package com.example.ihelpou.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ihelpou.activities.GestAidActivity;
import com.example.ihelpou.activities.GestAvailableDaysActivity;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.R;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;

import java.util.ArrayList;

public class HelpSeekerFragment extends Fragment {

    private Button addAvailabilityBtn;
    private User user;
    private GestClassDB gestClassDB = new GestClassDB();
    private ArrayList<Aid> listAidsAvailables = new ArrayList<>();
    private RecyclerView listAidsAvailablesRV;

    public HelpSeekerFragment() {
    }

    public HelpSeekerFragment(User user) {
        this.user = user;
    }

    public static HelpSeekerFragment newInstance(String param1, String param2) {
        HelpSeekerFragment fragment = new HelpSeekerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_seeker, container, false);
        listAidsAvailablesRV = view.findViewById(R.id.listAidsAvailablesRV);
        addAvailabilityBtn = view.findViewById(R.id.addAvailabilityBtn);

        listAidsAvailablesRV.setLayoutManager(new LinearLayoutManager(getContext()));
        gestClassDB.getAidsAccordingAvailability(user, listAidsAvailables, listAidsAvailablesRV, getContext());


        addAvailabilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GestAvailableDaysActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        return view;
    }
}