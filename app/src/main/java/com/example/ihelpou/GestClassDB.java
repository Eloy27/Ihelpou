package com.example.ihelpou;

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
    private User objectUser = null;

    public GestClassDB(){
        FirebaseDatabase fd = FirebaseDatabase.getInstance();
        databaseReference = fd.getReference();
    }

    public Task<Void> addUser(User user){
        return databaseReference.child("User").push().setValue(user);
    }

    public Task<Void> addAid(Aid aid, User user){
        return databaseReference.child("User").child(user.getKey().substring(user.getKey().lastIndexOf("/")+1)).child("Aids").push().setValue(aid);
    }

    public User checkUser(String username, String password){
        databaseReference.child("User").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    databaseReference.child("User").child(snapshot.getKey()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User user = snapshot.getValue(User.class);
                            if (user.getUsername().equals(username) && user.getPassword().equals(password)){
                                objectUser = new User(String.valueOf(databaseReference.child("User").child(snapshot.getKey())),
                                        user.getName(), user.getUsername(), user.getPassword(), user.getSurname(),
                                        user.getPhone(), user.getAddress(), user.getAge(), user.getEmail());
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
        return objectUser;
    }
}
