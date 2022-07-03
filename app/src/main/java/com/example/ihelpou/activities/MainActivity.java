package com.example.ihelpou.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.ihelpou.R;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private EditText emailET, passwordET;
    private GestClassDB gestClassDB = new GestClassDB();
    private FirebaseAuth fsAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();
    private LinearProgressIndicator lpi;
    private LinearLayout logingID;
    private RelativeLayout relativeLayout;
    private Toast toast;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        setIsConnected();
        checkConnection();
        emailET = findViewById(R.id.emailET);
        passwordET = findViewById(R.id.passwordET);
        lpi = findViewById(R.id.linearIndicator);
        logingID = findViewById(R.id.logingID);
        relativeLayout = findViewById(R.id.relativeLayout);
    }

    public void setIsConnected(){
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void checkConnection() {
        try {
            if (isConnected) {
                loadPreferences(getApplicationContext());
            } else {
                setMainLogin();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setMainLogin() {
        setTheme(R.style.BackGround);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_main);
    }

    public void registerBtn(View view) {
        Intent intent = new Intent(getApplicationContext(), UserGestActivity.class);
        startActivity(intent);
    }

    public void loginBtn(View view) throws IOException, InterruptedException {
        if (!emailET.getText().toString().equals("") && !passwordET.getText().toString().equals("")) {
            User user = new User(emailET.getText().toString());
            relativeLayout.setVisibility(View.INVISIBLE);
            logingID.setVisibility(View.VISIBLE);
            login(user, passwordET.getText().toString(), getApplicationContext(), false);
        } else {
            showToast();
        }
    }

    void showToast() {
        if (toast == null || toast.getView().getWindowVisibility() != View.VISIBLE) {
            toast = Toast.makeText(this, "Introduce your data", Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void loadPreferences(Context c) throws IOException, InterruptedException {
        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(c);
        String email = pm.getString("email", "");
        String pass = pm.getString("pass", "");
        User user = new User(email);
        login(user, pass, c, true);
    }

    public void login(User userLogin, String password, Context c, Boolean control) throws IOException, InterruptedException {
        setIsConnected();
        if (isConnected) {
            if (!userLogin.getEmail().equals("") && !password.equals("")) {
                fsAuth.signInWithEmailAndPassword(userLogin.getEmail(), password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        if (!control)
                            lpi.setProgressCompat(20, true);
                        fsDB.collection("User")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                                        if (!control)
                                            lpi.setProgressCompat(40, true);
                                        if (query.isSuccessful()) {
                                            for (QueryDocumentSnapshot objectUser : query.getResult()) {
                                                if (!control)
                                                    lpi.setProgressCompat(60, true);
                                                if (objectUser.getId().equals(userLogin.getEmail())) {
                                                    if (!control)
                                                        lpi.setProgressCompat(80, true);
                                                    gestClassDB.savePreferences(objectUser.getId(), password, c);
                                                    User user = objectUser.toObject(User.class);
                                                    user.setEmail(objectUser.getId());
                                                    if (!control)
                                                        lpi.setProgressCompat(100, true);
                                                    Intent intent = new Intent(c, InitialActivity.class);
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
                        relativeLayout.setVisibility(View.VISIBLE);
                        logingID.setVisibility(View.INVISIBLE);
                        Toast.makeText(c, "Check your email and password", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                setMainLogin();
            }
        } else {
            try {
                relativeLayout.setVisibility(View.VISIBLE);
                logingID.setVisibility(View.INVISIBLE);
                Toast.makeText(c, "There isn't internet connection", Toast.LENGTH_SHORT).show();
            } catch (NullPointerException npe) {
                Toast.makeText(c, "There isn't internet connection", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
        System.exit(0);
    }
}