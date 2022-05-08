package com.example.ihelpou.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.ihelpou.BeginingActivity;
import com.example.ihelpou.GestAid;
import com.example.ihelpou.GestClassDB;
import com.example.ihelpou.R;
import com.example.ihelpou.RecyclerAdapter;
import com.example.ihelpou.UserRegisterActivity;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;

import java.util.ArrayList;

public class AidsFragment extends Fragment {

    private ImageButton addAidBtn;
    private User user;
    private GestClassDB gestClassDB = new GestClassDB();
    ArrayList<Aid> listAids = new ArrayList<>();
    RecyclerView listAidsRV;

    public AidsFragment() {
    }

    public AidsFragment(User user) {
        this.user = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aids, container, false);
        listAidsRV = view.findViewById(R.id.listAidsRV);
        addAidBtn = view.findViewById(R.id.addAidBtn);

        listAidsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        gestClassDB.getAids(user, listAids, listAidsRV);

        addAidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GestAid.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });
        return view;
    }
}