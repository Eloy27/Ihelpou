package com.example.ihelpou.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
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
import android.widget.ImageView;
import android.widget.ListView;
import com.example.ihelpou.activities.GestAvailableDaysActivity;
import com.example.ihelpou.activities.InitialActivity;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.R;
import com.example.ihelpou.classes.RecyclerAdapterAids;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;

public class OfferFragment extends Fragment {

    private ImageButton availabilityBtn;
    private GestClassDB gestClassDB = new GestClassDB();
    private ArrayList<Aid> listAidsAvailables = new ArrayList<>();
    private RecyclerView listAidsAvailablesRV;
    private User user;
    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();
    private EditText searchET;
    private ArrayList<Aid> listAidsAux = new ArrayList<>();

    public OfferFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offer, container, false);
        listAidsAvailablesRV = view.findViewById(R.id.listAidsRV);
        availabilityBtn = view.findViewById(R.id.availabilityBtn);
        searchET = view.findViewById(R.id.searchET);

        getUser(gestClassDB.getEmailActualUser(getContext()));
        listAidsAvailablesRV.setLayoutManager(new LinearLayoutManager(getContext()));

        gestClassDB.getAidsAccordingAvailability(listAidsAvailables, listAidsAvailablesRV, getContext(), availabilityBtn);
        gestClassDB.checkAvailability(availabilityBtn, getContext());

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

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listAidsAux.clear();
                if (searchET.getText().toString().length() == 0) {
                    gestClassDB.getAidsAccordingAvailability(listAidsAvailables, listAidsAvailablesRV, getContext(), availabilityBtn);
                } else {
                    for (Aid aid : listAidsAvailables) {
                        if (aid.getDescription().toLowerCase().contains(searchET.getText().toString().toLowerCase())) {
                            listAidsAux.add(aid);
                        }
                    }
                    RecyclerAdapterAids listAidsAdapter = new RecyclerAdapterAids(listAidsAux, getContext(), 'o', availabilityBtn);
                    listAidsAvailablesRV.setAdapter(listAidsAdapter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        return view;
    }

    public void getUser(String email){
        fsDB.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            for (QueryDocumentSnapshot objectUser : query.getResult()){
                                if (objectUser.getId().equals(email)) {
                                    user = objectUser.toObject(User.class);
                                    user.setEmail(email);
                                }
                            }
                        }
                    }
                });
    }

}