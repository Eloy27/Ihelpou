package com.example.ihelpou.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.ihelpou.R;
import com.example.ihelpou.classes.CallReceiver;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class InfoUserActivity extends AppCompatActivity {

    private static final int REQUEST_CALL = 1;
    private User user, helper;
    private Aid aid;
    private TextView nameTV, surnameTV, phoneTV, addressTV, ageTV, startTimeTV, finishTimeTV, titleAvailabilityTV;
    private Button callBtn;
    private ImageView avatarIV;
    private GestClassDB gestClassDB = new GestClassDB();
    private ImageButton mondayBtn, tuesdayBtn, wednesdayBtn, thursdayBtn, fridayBtn, saturdayBtn, sundayBtn;
    String putButton, where = "";
    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_helper);

        avatarIV = findViewById(R.id.avatarIV);
        nameTV = findViewById(R.id.nameTV);
        surnameTV = findViewById(R.id.surnameTV);
        phoneTV = findViewById(R.id.phoneTV);
        addressTV = findViewById(R.id.addressTV);
        ageTV = findViewById(R.id.ageTV);
        startTimeTV = findViewById(R.id.startTimeTV);
        finishTimeTV = findViewById(R.id.finishTimeTV);
        mondayBtn = findViewById(R.id.mondayBtn);
        tuesdayBtn = findViewById(R.id.tuesdayBtn);
        wednesdayBtn = findViewById(R.id.wednesdayBtn);
        thursdayBtn = findViewById(R.id.thursdayBtn);
        fridayBtn = findViewById(R.id.fridayBtn);
        saturdayBtn = findViewById(R.id.saturdayBtn);
        sundayBtn = findViewById(R.id.sundayBtn);
        titleAvailabilityTV = findViewById(R.id.titleAvailabilityTV);
        callBtn = findViewById(R.id.callBtn);
        avatarIV.setImageResource(R.drawable.avatar);

        Intent i = getIntent();
        helper = (User) i.getSerializableExtra("helper");
        user = (User) i.getSerializableExtra("user");
        aid = (Aid) i.getSerializableExtra("aid");
        putButton = i.getStringExtra("putButton");
        where = i.getStringExtra("where");
        getUser(gestClassDB.getEmailActualUser(this));


        if (putButton != null) {
            callBtn.setVisibility(View.INVISIBLE);
        }

        if (where.equals("request")) {
            if (helper != null)
                setInfoUser(helper);
        } else {
            if (user != null)
                setInfoUser(user);
        }

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
    }


    public void makePhoneCall() {
        String number = phoneTV.getText().toString().substring(phoneTV.getText().toString().indexOf(":") + 1);
        if (number.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_CALL);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number)));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
            }
        }
    }

    public void setInfoUser(User user) {
        nameTV.setText(nameTV.getText() + " " + user.getName());
        surnameTV.setText(surnameTV.getText() + " " + user.getSurname());
        phoneTV.setText(phoneTV.getText() + " " + user.getPhone());
        addressTV.setText(addressTV.getText() + " " + user.getAddress());
        ageTV.setText(ageTV.getText() + " " + user.getDateOfBirth());
        gestClassDB.setInfoHelper(user, startTimeTV, finishTimeTV, mondayBtn, tuesdayBtn, wednesdayBtn, thursdayBtn, fridayBtn, saturdayBtn, sundayBtn, titleAvailabilityTV);
    }

    public void comeBack(View view) {
        onBackPressed();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        if (CallReceiver.comeFromOnReceive) {
            messageAfterCall(this);
            CallReceiver.comeFromOnReceive = false;
        } else {
        }
    }

    public void getUser(String email) {
        fsDB.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            for (QueryDocumentSnapshot objectUser : query.getResult()) {
                                if (objectUser.getId().equals(email) && where.equals("offer")) {
                                    helper = objectUser.toObject(User.class);
                                    helper.setEmail(email);
                                } else if (objectUser.getId().equals(email) && where.equals("request")) {
                                    user = objectUser.toObject(User.class);
                                    user.setEmail(email);
                                }
                            }
                        }
                    }
                });
    }

    public void messageAfterCall(Context context) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Is help going to be done?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        gestClassDB.aidPending(aid, helper, user, context, where);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });
        AlertDialog message = alert.create();
        message.setTitle("The call has ended");
        message.show();
    }
}