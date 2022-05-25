package com.example.ihelpou.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ihelpou.activities.BeginingActivity;
import com.example.ihelpou.R;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.AvailableDays;
import com.example.ihelpou.models.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GestAidActivity extends AppCompatActivity {

    private EditText descriptionET, startTimeET, finishTimeET, firstDateET;
    private int day = 01, month = 01, year = 2022;
    private boolean firstDatePut = false;
    private GestClassDB gestClassDB = new GestClassDB();
    private User user;
    private ArrayList<String> days = new ArrayList<>();
    private int hour, minute;
    private String openEdit;
    private Aid aid;
    private TextView titleAid;
    private ImageButton okBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gest_aid);

        titleAid = findViewById(R.id.titleAid);
        descriptionET = findViewById(R.id.descriptionET);
        startTimeET = findViewById(R.id.startTimeET);
        finishTimeET = findViewById(R.id.finishTimeET);
        firstDateET = findViewById(R.id.firstDateET);
        okBtn = findViewById(R.id.okBtn);


        Intent i = getIntent();
        user = (User) i.getSerializableExtra("user");
        openEdit = i.getStringExtra("openEdit");

        if (openEdit != null) {
            titleAid.setText("Edit your aid");
            aid = (Aid) i.getSerializableExtra("aid");
            descriptionET.setText(aid.getDescription());
            startTimeET.setText(aid.getStartTime());
            finishTimeET.setText(aid.getFinishTime());
            firstDateET.setText(aid.getDay());
            okBtn.setImageResource(R.drawable.edit);
        }

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openEdit != null) {
                    Aid aidObject = new Aid(descriptionET.getText().toString(), startTimeET.getText().toString(), finishTimeET.getText().toString(), firstDateET.getText().toString());
                    gestClassDB.editAid(aidObject, user, getApplicationContext(), aid.getKey());
                } else {
                    Aid aid = new Aid(descriptionET.getText().toString(), startTimeET.getText().toString(), finishTimeET.getText().toString(), firstDateET.getText().toString(), "no", "");
                    gestClassDB.registerAid(aid, user, getApplicationContext());
                }
            }
        });
    }

    public void comeBack(View view) {
        onBackPressed();
    }

    public void putDate(View view) {
        final Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> firstDateET.setText(day + "/" + (month + 1) + "/" + year), day, month, year);
        LocalDate currentDate = LocalDate.parse(LocalDate.now().toString());
        datePickerDialog.updateDate(2022, currentDate.getMonthValue() - 1, currentDate.getDayOfMonth());
        datePickerDialog.show();
        firstDatePut = true;
    }

    public void popTimePicker(View v) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourTPD, int minuteTPD) {
                hour = hourTPD;
                minute = minuteTPD;
                switch (v.getId()) {
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
}