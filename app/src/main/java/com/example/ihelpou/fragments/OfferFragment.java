package com.example.ihelpou.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
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
    private ListView listAidsAvailablesLV;
    private User user;
    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();

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
        listAidsAvailablesLV = view.findViewById(R.id.listAidsLV);
        availabilityBtn = view.findViewById(R.id.availabilityBtn);
        getUser(gestClassDB.getEmailActualUser(getContext()));
        gestClassDB.getAidsAccordingAvailability(listAidsAvailables, listAidsAvailablesLV, getContext());
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