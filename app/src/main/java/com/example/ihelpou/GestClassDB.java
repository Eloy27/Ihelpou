package com.example.ihelpou;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GestClassDB {

    final DatabaseReference databaseReference;
    protected boolean exists = false;

    public GestClassDB(){
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        databaseReference = fd.getReference();
    }

    public Task<Void> addUser(User user){
        return databaseReference.child("User").push().setValue(user);
    }

    public Task<Void> addAid(Aid aid, User user){
        //return databaseReference.child("User").push().setValue(aid);
        return null;
    }

    public void selectUser(String username, String password){
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.e("Key: ", String.valueOf(databaseReference.child("User").child(snapshot.getKey())));
                    databaseReference.child("User").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = snapshot.getValue(User.class);

                            Log.e("Username: ", user.getUsername());
                            Log.e("Password: ", user.getPassword());

                            Log.e("Username Introduced: ", username);
                            Log.e("Password Introduced: ", password);

                            if (user.getUsername().equals(username) && user.getPassword().equals(password)){
                                exists = true;
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
