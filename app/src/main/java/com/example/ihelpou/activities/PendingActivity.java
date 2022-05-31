package com.example.ihelpou.activities;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ihelpou.R;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.classes.ListAdapter;
import com.example.ihelpou.models.Aid;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class PendingActivity extends AppCompatActivity {

    private ListView listAidsLV;
    private GestClassDB gestClassDB = new GestClassDB();
    private ArrayList<Aid> listAids = new ArrayList<>();
    private ArrayList<Aid> listAidsAux = new ArrayList<>();
    private EditText searchET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);
        listAidsLV = findViewById(R.id.listAidsLV);
        gestClassDB.getPendingAids(listAids, listAidsLV, this);
        searchET = findViewById(R.id.searchET);

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listAidsAux.clear();
                if (searchET.getText().toString().length() == 0) {
                    gestClassDB.getPendingAids(listAids, listAidsLV, getApplicationContext());
                } else {
                    for (Aid aid : listAids) {
                        if (aid.getDescription().toLowerCase().contains(searchET.getText().toString().toLowerCase())) {
                            listAidsAux.add(aid);
                        }
                    }
                    ListAdapter listAidsAdapter = new ListAdapter(listAidsAux, getApplicationContext(), 'p', null);
                    listAidsLV.setAdapter(listAidsAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}