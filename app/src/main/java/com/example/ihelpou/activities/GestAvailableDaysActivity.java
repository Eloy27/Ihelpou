package com.example.ihelpou.activities;

import com.example.ihelpou.R;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.models.AvailableDays;
import com.example.ihelpou.models.User;

import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class GestAvailableDaysActivity extends AppCompatActivity {

    private EditText startTimeET, finishTimeET;
    private int hour, minute;
    private ArrayList<String> availableDays = new ArrayList<>();
    private ImageButton mondayBtn, tuesdayBtn, wednesdayBtn, thursdayBtn, fridayBtn, saturdayBtn, sundayBtn;
    private GestClassDB gestClassDB = new GestClassDB();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gest_available_days);

        startTimeET = findViewById(R.id.startTimeET);
        finishTimeET = findViewById(R.id.finishTimeET);
        mondayBtn = findViewById(R.id.mondayBtn);
        tuesdayBtn= findViewById(R.id.tuesdayBtn);
        wednesdayBtn = findViewById(R.id.wednesdayBtn);
        thursdayBtn = findViewById(R.id.thursdayBtn);
        fridayBtn = findViewById(R.id.fridayBtn);
        saturdayBtn = findViewById(R.id.saturdayBtn);
        sundayBtn = findViewById(R.id.sundayBtn);

        Intent i = getIntent();
        user = (User)i.getSerializableExtra("user");

        mondayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                availableDays.add("Monday");
            }
        });

    }

    public void comeBack(View view){
        onBackPressed();
    }

    public void popTimePicker(View v){
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourTPD, int minuteTPD) {
                hour = hourTPD;
                minute = minuteTPD;
                switch(v.getId()){
                    case R.id.startTimeET:
                        startTimeET.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                    case R.id.finishTimeET:
                        finishTimeET.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        break;
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }

    public void createAvailability(View view){
        AvailableDays availableDaysObj = new AvailableDays(availableDays, startTimeET.getText().toString(), finishTimeET.getText().toString());
        gestClassDB.addAvailableDays(availableDaysObj, user).addOnSuccessListener(suc ->
        {
            Intent intent = new Intent(this, BeginingActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            Toast.makeText(this, "Added your availability successfully", Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(err ->
        {
            Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
