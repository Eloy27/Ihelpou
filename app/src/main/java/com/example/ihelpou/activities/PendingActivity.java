package com.example.ihelpou.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ihelpou.R;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.classes.RecyclerAdapterAids;
import com.example.ihelpou.models.Aid;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;

public class PendingActivity extends AppCompatActivity {

    private RecyclerView listAidsRV;
    private GestClassDB gestClassDB = new GestClassDB();
    private ArrayList<Aid> listAids = new ArrayList<>();
    private ArrayList<Aid> listAidsAux = new ArrayList<>();
    private EditText searchET;
    private LinearLayout messageID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending);
        listAidsRV = findViewById(R.id.listAidsRV);
        searchET = findViewById(R.id.searchET);
        messageID = findViewById(R.id.messageID);

        gestClassDB.getPendingAids(listAids, listAidsRV, this, messageID);

        listAidsRV.setLayoutManager(new LinearLayoutManager(this));
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listAidsAux.clear();
                if (searchET.getText().toString().length() == 0) {
                    gestClassDB.getPendingAids(listAids, listAidsRV, getApplicationContext(), messageID);
                } else {
                    for (Aid aid : listAids) {
                        if (aid.getDescription().toLowerCase().contains(searchET.getText().toString().toLowerCase())) {
                            listAidsAux.add(aid);
                        }
                    }
                    RecyclerAdapterAids listAidsAdapter = new RecyclerAdapterAids(listAidsAux, getApplicationContext(), 'p', null);
                    listAidsRV.setAdapter(listAidsAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void comeBack(View view) {
        onBackPressed();
    }
}