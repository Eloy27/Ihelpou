package com.example.ihelpou.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.ihelpou.activities.GestAidActivity;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.R;
import com.example.ihelpou.classes.RecyclerAdapterAids;
import com.example.ihelpou.models.Aid;


import java.util.ArrayList;

public class RequestFragment extends Fragment {

    private ImageButton addAidBtn;
    private GestClassDB gestClassDB = new GestClassDB();
    private ArrayList<Aid> listAids = new ArrayList<>();
    private ArrayList<Aid> listAidsAux = new ArrayList<>();
    private RecyclerView listAidsRV;
    private EditText searchET;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerAdapterAids listAidsAdapter;
    private LinearLayout messageID;

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
        listAidsRV = view.findViewById(R.id.listAidsRV);
        addAidBtn = view.findViewById(R.id.addAidBtn);
        searchET = view.findViewById(R.id.searchET);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        messageID = view.findViewById(R.id.messageID);

        listAidsRV.setLayoutManager(new LinearLayoutManager(getContext()));
        gestClassDB.getAids(listAids, listAidsRV, getContext(), addAidBtn, messageID);

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listAidsAux.clear();
                if (searchET.getText().toString().length() == 0) {
                    gestClassDB.getAids(listAids, listAidsRV, getContext(), addAidBtn, messageID);
                } else {
                    for (Aid aid : listAids) {
                        if (aid.getDescription().toLowerCase().contains(searchET.getText().toString().toLowerCase())) {
                            listAidsAux.add(aid);
                        }
                    }
                    listAidsAdapter = new RecyclerAdapterAids(listAidsAux, getContext(), 'r',addAidBtn);
                    listAidsRV.setAdapter(listAidsAdapter);
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

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                gestClassDB.getAids(listAids, listAidsRV, getContext(), addAidBtn, messageID);
                gestClassDB.adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return view;
    }


}