package com.example.ihelpou.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ihelpou.R;
import com.example.ihelpou.activities.MainActivity;
import com.example.ihelpou.activities.PendingActivity;
import com.example.ihelpou.activities.UserGestActivity;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


public class UserFragment extends Fragment {

    private Button pendingBtn, editProfileBtn, logOutBtn;
    private User user;
    private GestClassDB gestClassDB = new GestClassDB();
    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();
    private TextView nameTV, phoneTV, deleteAccountTV, textView;
    private LinearProgressIndicator lpi;
    private LinearLayout logingID;
    private RelativeLayout relativeLayout;

    public UserFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        pendingBtn = view.findViewById(R.id.pendingBtn);
        editProfileBtn = view.findViewById(R.id.editProfileBtn);
        logOutBtn = view.findViewById(R.id.logOutBtn);
        nameTV = view.findViewById(R.id.nameTV);
        phoneTV = view.findViewById(R.id.phoneTV);
        deleteAccountTV = view.findViewById(R.id.deleteAccountTV);
        textView = view.findViewById(R.id.textView);
        lpi = view.findViewById(R.id.linearIndicator);
        logingID = view.findViewById(R.id.logingID);
        relativeLayout = view.findViewById(R.id.relativeLayout);

        getUser(gestClassDB.getEmailActualUser(getContext()));

        pendingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), PendingActivity.class);
                startActivity(i);
            }
        });

        editProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), UserGestActivity.class);
                i.putExtra("where", "userfragment");
                startActivity(i);
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "You are going to log out the application";
                messageSure(message, true);
            }
        });


        deleteAccountTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = "You are going to delete your account";
                messageSure(message, false);
            }
        });

        return view;
    }


    public void messageSure(String message, Boolean control) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (control) {
                            relativeLayout.setVisibility(View.INVISIBLE);
                            logingID.setVisibility(View.VISIBLE);
                            lpi.setProgressCompat(20, true);
                            SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(getContext());
                            lpi.setProgressCompat(40, true);
                            SharedPreferences.Editor editor = pm.edit();
                            lpi.setProgressCompat(60, true);
                            editor.clear();
                            lpi.setProgressCompat(80, true);
                            editor.commit();
                            lpi.setProgressCompat(100, true);
                            Intent i = new Intent(getContext(), MainActivity.class);
                            startActivity(i);
                        }else{
                            textView.setText("Deleting your account...");
                            relativeLayout.setVisibility(View.INVISIBLE);
                            logingID.setVisibility(View.VISIBLE);
                            gestClassDB.deleteAccount(user, getContext(), lpi, relativeLayout, logingID);
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertMessage = alert.create();
        alertMessage.setTitle("Are you sure?");
        alertMessage.show();
    }

    public void getUser(String email) {
        fsDB.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            for (QueryDocumentSnapshot objectUser : query.getResult()) {
                                if (objectUser.getId().equals(email)) {
                                    user = objectUser.toObject(User.class);
                                    user.setEmail(email);
                                    nameTV.setText(user.getName());
                                    phoneTV.setText(user.getPhone());
                                }
                            }
                        }
                    }
                });
    }
}