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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

public class GestAvailableDaysActivity extends AppCompatActivity {

    private TextView titleAvailabilityTV;
    private EditText startTimeET, finishTimeET;
    private int hour, minute;
    private ArrayList<HashMap<String, String>> availableDays = new ArrayList<>();
    private ImageButton okBtn, mondayBtn, tuesdayBtn, wednesdayBtn, thursdayBtn, fridayBtn, saturdayBtn, sundayBtn;
    private GestClassDB gestClassDB = new GestClassDB();
    private User user;
    private String openEdit;
    private AvailableDays availableDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gest_available_days);

        titleAvailabilityTV = findViewById(R.id.titleAvailabilityTV);
        startTimeET = findViewById(R.id.startTimeET);
        finishTimeET = findViewById(R.id.finishTimeET);
        mondayBtn = findViewById(R.id.mondayBtn);
        tuesdayBtn = findViewById(R.id.tuesdayBtn);
        wednesdayBtn = findViewById(R.id.wednesdayBtn);
        thursdayBtn = findViewById(R.id.thursdayBtn);
        fridayBtn = findViewById(R.id.fridayBtn);
        saturdayBtn = findViewById(R.id.saturdayBtn);
        sundayBtn = findViewById(R.id.sundayBtn);
        okBtn = findViewById(R.id.okBtn);

        Intent i = getIntent();
        user = (User) i.getSerializableExtra("user");
        openEdit = i.getStringExtra("openEdit");

        if (openEdit != null) {
            titleAvailabilityTV.setText("Edit your availability");
            availableDay = (AvailableDays) i.getSerializableExtra("availableDays");
            startTimeET.setText(availableDay.getStartTime());
            finishTimeET.setText(availableDay.getFinishTime());
            for (int j = 0; j < availableDay.getAvailableDays().size(); j++) {
                availableDays.add(availableDay.getAvailableDays().get(j));
                availableDays.get(j).forEach((s, o) -> {
                    switch (o) {
                        case "Monday":
                            mondayBtn.setImageResource(R.drawable.mondayrojo);
                            break;
                        case "Tuesday":
                            tuesdayBtn.setImageResource(R.drawable.tdayrojo);
                            break;
                        case "Wednesday":
                            wednesdayBtn.setImageResource(R.drawable.wednesdayrojo);
                            break;
                        case "Thursday":
                            thursdayBtn.setImageResource(R.drawable.tdayrojo);
                            break;
                        case "Friday":
                            fridayBtn.setImageResource(R.drawable.fridayrojo);
                            break;
                        case "Saturday":
                            saturdayBtn.setImageResource(R.drawable.sdayrojo);
                            break;
                        case "Sunday":
                            sundayBtn.setImageResource(R.drawable.sdayrojo);
                            break;
                    }
                });
            }
            okBtn.setImageResource(R.drawable.edit);
        }

        mondayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtn("Monday", mondayBtn);
            }
        });

        tuesdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtn("Tuesday", tuesdayBtn);
            }
        });

        wednesdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtn("Wednesday", wednesdayBtn);
            }
        });

        thursdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtn("Thursday", thursdayBtn);
            }
        });

        fridayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtn("Friday", fridayBtn);
            }
        });

        saturdayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtn("Saturday", saturdayBtn);
            }
        });

        sundayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickBtn("Sunday", sundayBtn);
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (openEdit != null) {
                    AvailableDays availableDaysObj = new AvailableDays(availableDays, startTimeET.getText().toString(), finishTimeET.getText().toString());
                    gestClassDB.editAvailableDay(availableDaysObj, user, getApplicationContext(), availableDay.getKey());
                } else {
                    AvailableDays availableDaysObj = new AvailableDays(availableDays, startTimeET.getText().toString(), finishTimeET.getText().toString());
                    gestClassDB.registerAvailableDay(availableDaysObj, user, getApplicationContext());
                }
            }
        });
    }

    public void comeBack(View view) {
        onBackPressed();
    }

    public void onClickBtn(String day, ImageButton button) {
        if (availableDays.size() > 0) {
            AtomicBoolean existsDay = new AtomicBoolean(false);
            boolean isAvailable = false;
            final int[] posExists = new int[1];
            for (int i = 0; i < availableDays.size(); i++) {
                int finalI = i;
                availableDays.get(i).forEach((s, o) -> {
                    if (s.equals("day") && o.equals(day)) {
                        existsDay.set(true);
                        posExists[0] = finalI;
                    }
                });
            }
            if (existsDay.get()) {
                availableDays.get(posExists[0]).forEach((s, o) -> {
                    if (s.equals("availability") && o.equals("available")) {
                        availableDays.remove(posExists[0]);
                        if (button.equals(mondayBtn)) {
                            mondayBtn.setImageResource(R.drawable.monday);
                        } else if (button.equals(tuesdayBtn)) {
                            tuesdayBtn.setImageResource(R.drawable.tday);
                        } else if (button.equals(wednesdayBtn)) {
                            wednesdayBtn.setImageResource(R.drawable.wednesday);
                        } else if (button.equals(thursdayBtn)) {
                            thursdayBtn.setImageResource(R.drawable.tday);
                        } else if (button.equals(fridayBtn)) {
                            fridayBtn.setImageResource(R.drawable.friday);
                        } else if (button.equals(saturdayBtn)) {
                            saturdayBtn.setImageResource(R.drawable.sday);
                        } else if (button.equals(sundayBtn)) {
                            sundayBtn.setImageResource(R.drawable.sday);
                        }
                    }
                    else{
                        Toast.makeText(this, "This day you are busy", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                HashMap<String, String> dayAndAvailability = new HashMap<>();
                dayAndAvailability.put("availability", "available");
                dayAndAvailability.put("day", day);
                availableDays.add(dayAndAvailability);
                if (button.equals(mondayBtn)) {
                    mondayBtn.setImageResource(R.drawable.mondayrojo);
                } else if (button.equals(tuesdayBtn)) {
                    tuesdayBtn.setImageResource(R.drawable.tdayrojo);
                } else if (button.equals(wednesdayBtn)) {
                    wednesdayBtn.setImageResource(R.drawable.wednesdayrojo);
                } else if (button.equals(thursdayBtn)) {
                    thursdayBtn.setImageResource(R.drawable.tdayrojo);
                } else if (button.equals(fridayBtn)) {
                    fridayBtn.setImageResource(R.drawable.fridayrojo);
                } else if (button.equals(saturdayBtn)) {
                    saturdayBtn.setImageResource(R.drawable.sdayrojo);
                } else if (button.equals(sundayBtn)) {
                    sundayBtn.setImageResource(R.drawable.sdayrojo);
                }
            }

        } else {
            HashMap<String, String> dayAndAvailability = new HashMap<>();
            dayAndAvailability.put("availability", "available");
            dayAndAvailability.put("day", day);
            availableDays.add(dayAndAvailability);
            if (button.equals(mondayBtn)) {
                mondayBtn.setImageResource(R.drawable.mondayrojo);
            } else if (button.equals(tuesdayBtn)) {
                tuesdayBtn.setImageResource(R.drawable.tdayrojo);
            } else if (button.equals(wednesdayBtn)) {
                wednesdayBtn.setImageResource(R.drawable.wednesdayrojo);
            } else if (button.equals(thursdayBtn)) {
                thursdayBtn.setImageResource(R.drawable.tdayrojo);
            } else if (button.equals(fridayBtn)) {
                fridayBtn.setImageResource(R.drawable.fridayrojo);
            } else if (button.equals(saturdayBtn)) {
                saturdayBtn.setImageResource(R.drawable.sdayrojo);
            } else if (button.equals(sundayBtn)) {
                sundayBtn.setImageResource(R.drawable.sdayrojo);
            }
        }
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
