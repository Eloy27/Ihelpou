package com.example.ihelpou.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import com.example.ihelpou.activities.GestAidActivity;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.R;
import com.example.ihelpou.classes.ListAdapter;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.ArrayList;

public class RequestFragment extends Fragment {

    private ImageButton addAidBtn;
    private GestClassDB gestClassDB = new GestClassDB();
    private ArrayList<Aid> listAids = new ArrayList<>();
    private ArrayList<Aid> listAidsAux = new ArrayList<>();
    private ListView listAidsLV;
    private EditText searchET;

    public RequestFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        listAidsLV = view.findViewById(R.id.listAidsLV);
        addAidBtn = view.findViewById(R.id.addAidBtn);
        searchET = view.findViewById(R.id.searchET);

        gestClassDB.getAids(listAids, listAidsLV, getContext(), addAidBtn);

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listAidsAux.clear();
                if (searchET.getText().toString().length() == 0) {
                    gestClassDB.getAids(listAids, listAidsLV, getContext(), addAidBtn);
                } else {
                    for (Aid aid : listAids) {
                        if (aid.getDescription().toLowerCase().contains(searchET.getText().toString().toLowerCase())) {
                            listAidsAux.add(aid);
                        }
                    }
                    ListAdapter listAidsAdapter = new ListAdapter(listAidsAux, getContext(), 'r',addAidBtn);
                    listAidsLV.setAdapter(listAidsAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        addAidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), GestAidActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }


}