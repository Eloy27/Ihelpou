package com.example.ihelpou.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ihelpou.R;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private EditText emailET, passwordET;
    private GestClassDB gestClassDB = new GestClassDB();
    private FirebaseAuth fsAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        loadPreferences(getApplicationContext());
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
    }

    public void registerBtn(View view){
        Intent intent = new Intent(getApplicationContext(), UserRegisterActivity.class);
        startActivity(intent);
    }

    public void loginBtn(View view){
        User user = new User(emailET.getText().toString());
        login(user, passwordET.getText().toString(), getApplicationContext());
    }

    public void loadPreferences(Context c) {
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(c);
        String email = pm.getString("email", "");
        String pass = pm.getString("pass", "");
        User user = new User(email);
        login(user, pass, c);
    }

    public void login(User userLogin, String password, Context c) {
        if (!userLogin.getEmail().equals("") && !password.equals("")) {
            fsAuth.signInWithEmailAndPassword(userLogin.getEmail(), password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    fsDB.collection("User")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> query) {
                                    if (query.isSuccessful()) {
                                        for (QueryDocumentSnapshot objectUser : query.getResult()) {
                                            if (objectUser.getId().equals(userLogin.getEmail())) {
                                                gestClassDB.savePreferences(objectUser.getId(), password, c);
                                                User user = objectUser.toObject(User.class);
                                                user.setEmail(objectUser.getId());
                                                Intent intent = new Intent(c, BeginingActivity.class);
                                                intent.putExtra("user", user);
                                                Toast.makeText(c, "Welcome " + user.getName(), Toast.LENGTH_SHORT).show();
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                c.startActivity(intent);
                                            }
                                        }
                                    } else {
                                        Toast.makeText(c, "Unexpected Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(c, "This user doesn't exist", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            setTheme(R.style.BackGround);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            setContentView(R.layout.activity_main);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }
}