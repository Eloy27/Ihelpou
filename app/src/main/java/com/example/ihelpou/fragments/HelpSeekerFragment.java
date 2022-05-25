package com.example.ihelpou.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.ihelpou.activities.GestAvailableDaysActivity;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.R;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;

import java.util.ArrayList;

public class HelpSeekerFragment extends Fragment {

    private ImageButton availabilityBtn;
    private User user;
    private GestClassDB gestClassDB = new GestClassDB();
    private ArrayList<Aid> listAidsAvailables = new ArrayList<>();
    private ListView listAidsAvailablesLV;

    public HelpSeekerFragment() {
    }

    public HelpSeekerFragment(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_help_seeker, container, false);
        listAidsAvailablesLV = view.findViewById(R.id.listAidsLV);
        availabilityBtn = view.findViewById(R.id.availabilityBtn);

        gestClassDB.getAidsAccordingAvailability(user, listAidsAvailables, listAidsAvailablesLV, getContext());
        gestClassDB.checkAvailability(user, availabilityBtn, getContext(), "HelpSeekerFragment");

        availabilityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GestAvailableDaysActivity.class);
                intent.putExtra("user", user);
                if (!availabilityBtn.getDrawable().getConstantState().equals(ResourcesCompat.getDrawable(getResources(), R.drawable.add, null).getConstantState())) {
                    intent.putExtra("openEdit", "yes");
                    gestClassDB.sendAvailability(user, intent, getContext());
                } else {
                    startActivity(intent);
                }
            }
        });
        return view;

    }
}