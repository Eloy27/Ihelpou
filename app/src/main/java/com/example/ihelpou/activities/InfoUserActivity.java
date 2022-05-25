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
    private TextView nameTV, surnameTV, phoneTV, addressTV, ageTV, startTimeTV, finishTimeTV;
    private Button callBtn;
    private ImageView avatarIV;
    private GestClassDB gestClassDB = new GestClassDB();
    private ImageButton mondayBtn, tuesdayBtn, wednesdayBtn, thursdayBtn, fridayBtn, saturdayBtn, sundayBtn;
    String putButton, where = "";

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
        callBtn = findViewById(R.id.callBtn);
        avatarIV.setImageResource(R.drawable.avatar);

        Intent i = getIntent();
        helper = (User) i.getSerializableExtra("helper");
        user = (User) i.getSerializableExtra("user");
        aid = (Aid) i.getSerializableExtra("aid");
        putButton = i.getStringExtra("putButton");
        where = i.getStringExtra("where");


        if (putButton!=null){
            callBtn.setVisibility(View.INVISIBLE);
        }

        if (where.equals("request")) {
            setInfoUser(helper);
        }
        else{
            setInfoUser(user);
        }

        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
    }


    public void makePhoneCall(){
        String number = phoneTV.getText().toString().substring(phoneTV.getText().toString().indexOf(":")+1);
        if (number.trim().length() > 0) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_PHONE_STATE}, REQUEST_CALL);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else{
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+number)));
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
        nameTV.setText(nameTV.getText()+" "+user.getName());
        surnameTV.setText(surnameTV.getText()+" "+user.getSurname());
        phoneTV.setText(phoneTV.getText()+" "+user.getPhone());
        addressTV.setText(addressTV.getText()+" "+user.getAddress());
        ageTV.setText(ageTV.getText()+" "+user.getAge());
        gestClassDB.setInfoHelper(user, startTimeTV, finishTimeTV, mondayBtn, tuesdayBtn, wednesdayBtn, thursdayBtn, fridayBtn, saturdayBtn, sundayBtn);
    }

    public void comeBack(View view) {
        onBackPressed();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        if (CallReceiver.comeFromOnReceive){
            messageAfterCall(this);
            CallReceiver.comeFromOnReceive = false;
        }
        else{
        }
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
                        Toast.makeText(context, "No", Toast.LENGTH_SHORT).show();
                        dialog.cancel();
                    }
                });
        AlertDialog message = alert.create();
        message.setTitle("The call has ended");
        message.show();
    }
}