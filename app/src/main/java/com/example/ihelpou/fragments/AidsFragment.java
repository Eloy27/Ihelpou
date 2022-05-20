package com.example.ihelpou.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ihelpou.activities.BeginingActivity;
import com.example.ihelpou.activities.GestAidActivity;
import com.example.ihelpou.activities.HelpersActivity;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.R;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;

import java.util.ArrayList;

public class AidsFragment extends Fragment {

    private ImageButton addAidBtn;
    private User user;
    private GestClassDB gestClassDB = new GestClassDB();
    private ArrayList<Aid> listAids = new ArrayList<>();
    private ListView listAidsLV;

    public AidsFragment() {
    }

    public AidsFragment(User user) {
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_aids, container, false);
        listAidsLV = view.findViewById(R.id.listAidsLV);
        addAidBtn = view.findViewById(R.id.addAidBtn);

        gestClassDB.getAids(user, listAids, listAidsLV, getContext());

        addAidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), GestAidActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        listAidsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Aid aid = listAids.get(position);
                Intent intent = new Intent(getContext(), HelpersActivity.class);
                intent.putExtra("aid", aid);
                intent.putExtra("user", user);
                startActivity(intent);

            }
        });

        return view;
    }

}