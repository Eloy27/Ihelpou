package com.example.ihelpou.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.ihelpou.R;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;

import java.util.ArrayList;


public class PendingFragment extends Fragment {

    private User user;
    private GestClassDB gestClassDB = new GestClassDB();
    private ArrayList<Aid> listAids = new ArrayList<>();
    private ListView listAidsLV;


    public PendingFragment() {
    }

    public PendingFragment(User user) {
        this.user = user;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pending, container, false);
        listAidsLV = view.findViewById(R.id.listAidsLV);
        gestClassDB.getPendingAids(user, listAids, listAidsLV, getContext());


        return view;
    }
}