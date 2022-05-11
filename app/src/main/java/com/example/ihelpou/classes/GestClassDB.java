package com.example.ihelpou.classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ihelpou.activities.BeginingActivity;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.AvailableDays;
import com.example.ihelpou.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalTime;
import java.util.ArrayList;

public class GestClassDB {

    final DatabaseReference databaseReference;

    public GestClassDB() {
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        databaseReference = fd.getReference();
    }

    public Task<Void> addUser(User user) {
        return databaseReference.child("User").push().setValue(user);
    }

    public Task<Void> addAid(Aid aid, User user) {
        return databaseReference.child("User").child(user.getKey().substring(user.getKey().lastIndexOf("/") + 1)).child("Aids").push().setValue(aid);
    }

    public Task<Void> addAvailableDays(AvailableDays availableDays, User user) {
        return databaseReference.child("User").child(user.getKey().substring(user.getKey().lastIndexOf("/") + 1)).child("AvailableDays").push().setValue(availableDays);
    }

    public void getAids(User user, ArrayList<Aid> listAids, RecyclerView listAidsRV, Context c) {
        databaseReference.child("User").child(user.getKey().substring(user.getKey().lastIndexOf("/") + 1)).child("Aids").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    databaseReference.child("Aids").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Aid aid = snapshot.getValue(Aid.class);
                            listAids.add(aid);
                            RecyclerAdapter listAidsAdapter = new RecyclerAdapter(listAids, c);
                            listAidsRV.setAdapter(listAidsAdapter);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void checkUser(String username, String password, Context c) {
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final int[] cont = {0};
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    databaseReference.child("User").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                            User user = snapshot.getValue(User.class);
                            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                                user = new User(String.valueOf(databaseReference.child("User").child(snapshot.getKey())),
                                        user.getName(), user.getUsername(), user.getPassword(), user.getSurname(),
                                        user.getPhone(), user.getAddress(), user.getAge(), user.getEmail());
                                Intent intent = new Intent(c, BeginingActivity.class);
                                intent.putExtra("user", user);
                                c.startActivity(intent);
                                cont[0]--;
                            }
                            cont[0]++;
                            if (cont[0] == dataSnapshot.getChildrenCount()) {
                                Toast.makeText(c, "This user doesn't exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void getAidsAccordingAvailability(User user, ArrayList<Aid> listAidsAvailables, RecyclerView listAidsAvailablesRV, Context c) {
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot objectUserID) {
                for (final DataSnapshot objectUser : objectUserID.getChildren()) {
                    databaseReference.child("User").child(objectUser.getKey()).child("Aids").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot objectAidID) {
                            for (final DataSnapshot objectAid : objectAidID.getChildren()) {
                                Aid aid = objectAid.getValue(Aid.class);
                                LocalTime aidST = LocalTime.parse(aid.getStartTime());
                                LocalTime aidFT = LocalTime.parse(aid.getFinishTime());

                                databaseReference.child("User").child(user.getKey().substring(user.getKey().lastIndexOf("/") + 1)).child("AvailableDays").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot objectAvDaysIdUserLoggedIn) {
                                        for (final DataSnapshot objectAvDaysUserLoggedIn : objectAvDaysIdUserLoggedIn.getChildren()) {
                                            AvailableDays availableDays = objectAvDaysUserLoggedIn.getValue(AvailableDays.class);
                                            LocalTime availableDaysST = LocalTime.parse(availableDays.getStartTime());
                                            LocalTime availableDaysFT = LocalTime.parse(availableDays.getFinishTime());

                                            Log.e("aidST", aidST+"");
                                            Log.e("aidFT", aidFT+"");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                /*listAids.add(aid);
                                RecyclerAdapter listAidsAdapter = new RecyclerAdapter(listAids);
                                listAidsRV.setAdapter(listAidsAdapter);*/
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
