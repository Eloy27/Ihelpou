package com.example.ihelpou.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.ihelpou.R;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class HelpersActivity extends AppCompatActivity {

    private GestClassDB gestClassDB = new GestClassDB();
    private ArrayList<User> listHelpersAvailables = new ArrayList<>();
    private RecyclerView listHelpersAvailablesRV;
    private Aid aid;
    private User user;
    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpers);
        listHelpersAvailablesRV = findViewById(R.id.listHelpersAvailablesRV);

        Intent i = getIntent();
        aid = (Aid)i.getSerializableExtra("aid");
        getUser(gestClassDB.getEmailActualUser(this));

        listHelpersAvailablesRV.setLayoutManager(new LinearLayoutManager(this));
        gestClassDB.getHelpersAccordingAid(listHelpersAvailables, listHelpersAvailablesRV, aid, this);
    }

    public void comeBack(View view){
        Intent i = new Intent(this, InitialActivity.class);
        startActivity(i);
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