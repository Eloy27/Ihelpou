package com.example.ihelpou;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Console;

public class GestUserDB {

    private DatabaseReference databaseReference;

    public GestUserDB(){

        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        databaseReference = fd.getReference(User.class.getSimpleName());
    }

    public Task<Void> addUser(User user){
        return databaseReference.push().setValue(user);
    }

    public User selectUser(String username, String password){
        final User[] user = {null};

        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot object : dataSnapshot.getChildren()){
                    System.out.println(object.child("username"));
                    System.out.println(object.child("password"));

                    System.out.println(object.child("username").getValue().toString());
                    System.out.println(object.child("password").getValue().toString());

                    if (object.child("username").equals(username) && object.child("password").equals(password)){
                        user[0] = new User(object.child("name").getValue().toString(), object.child("username").getValue().toString(), object.child("password").getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return user[0];
    }
}
