package com.example.ihelpou.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.ihelpou.R;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class GestAidActivity extends AppCompatActivity {

    private EditText descriptionET;
    private EditText startTimeET, finishTimeET, firstDateET;
    private int day = 01, month = 01, year = 2022, position;
    private GestClassDB gestClassDB = new GestClassDB();
    private User user;
    private int hour, minute;
    private String openEdit;
    private Aid aid;
    private TextView titleAid;
    private ImageButton okBtn, deleteBtn;
    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();
    private TextInputLayout descriptionTIL, startTimeTIL, finishTimeTIL, firstDateTIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gest_aid);

        titleAid = findViewById(R.id.titleAid);
        descriptionET = findViewById(R.id.descriptionET);
        descriptionTIL = findViewById(R.id.descriptionTIL);
        startTimeET = findViewById(R.id.startTimeET);
        startTimeTIL = findViewById(R.id.startTimeTIL);
        finishTimeET = findViewById(R.id.finishTimeET);
        finishTimeTIL = findViewById(R.id.finishTimeTIL);
        firstDateET = findViewById(R.id.firstDateET);
        firstDateTIL = findViewById(R.id.firstDateTIL);
        deleteBtn = findViewById(R.id.deleteBtn);
        okBtn = findViewById(R.id.okBtn);


        Intent i = getIntent();
        getUser(gestClassDB.getEmailActualUser(this));
        openEdit = i.getStringExtra("openEdit");

        if (openEdit != null) {
            titleAid.setText("Edit your aid");
            aid = (Aid) i.getSerializableExtra("aid");
            position = i.getIntExtra("position", position);
            descriptionET.setText(aid.getDescription());
            startTimeET.setText(aid.getStartTime());
            finishTimeET.setText(aid.getFinishTime());
            firstDateET.setText(aid.getDay());
            okBtn.setImageResource(R.drawable.edit);
            deleteBtn.setVisibility(View.VISIBLE);
        }

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkDescription() && !startTimeET.getText().toString().equals("") && !finishTimeET.getText().toString().equals("") && !firstDateET.getText().toString().equals("")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String firstDate = "";
                    try {
                        Date date_date = sdf.parse(firstDateET.getText().toString());
                        firstDate = sdf.format(date_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (openEdit != null) {
                        Aid aidObject = new Aid(descriptionET.getText().toString(), startTimeET.getText().toString(), finishTimeET.getText().toString(), firstDate);
                        gestClassDB.editAid(aidObject, user, getApplicationContext(), aid.getKey());
                    } else {
                        Aid aid = new Aid(descriptionET.getText().toString(), startTimeET.getText().toString(), finishTimeET.getText().toString(), firstDate, "no", "");
                        gestClassDB.registerAid(aid, user, getApplicationContext());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Check your data", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageSure("You are going to delete your aid");
            }
        });

        descriptionET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                gestClassDB.textChanges(descriptionET, descriptionTIL, descriptionET.getText().toString().matches("[0-9A-zÀ-ÿ ]{5,40}"), "Between 5 and 40 letters or numbers");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public boolean checkDescription() {
        return descriptionET.getText().toString().matches("[0-9A-zÀ-ÿ ]{5,40}");
    }

    public void messageSure(String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        HashMap<Integer, String> hashMapItems = new HashMap<>();
                        hashMapItems.put(position, "yes");
                        gestClassDB.deleteAid(user, getApplicationContext(), hashMapItems);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertMessage = alert.create();
        alertMessage.setTitle("Are you sure?");
        alertMessage.show();
    }

    public void comeBack(View view) {
        onBackPressed();
    }

    public void getUser(String email) {
        fsDB.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            for (QueryDocumentSnapshot objectUser : query.getResult()) {
                                if (objectUser.getId().equals(email)) {
                                    user = objectUser.toObject(User.class);
                                    user.setEmail(email);
                                }
                            }
                        }
                    }
                });
    }

    public void putDate(View view) {
        final Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> firstDateET.setText(day + "/" + (month + 1) + "/" + year), day, month, year);
        LocalDate currentDate = LocalDate.parse(LocalDate.now().toString());
        datePickerDialog.updateDate(2022, currentDate.getMonthValue() - 1, currentDate.getDayOfMonth());
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis());
        datePickerDialog.show();
    }

    public void popTimePicker(View v) {
        TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourTPD, int minuteTPD) {
                hour = hourTPD;
                minute = minuteTPD;
                switch (v.getId()) {
                    case R.id.startTimeET:
                        if (finishTimeET.getText().toString().equals("")) {
                            startTimeET.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                        } else {
                            String startTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                            LocalTime startTimeLT = LocalTime.parse(startTime);
                            LocalTime finishTimeLT = LocalTime.parse(finishTimeET.getText().toString());
                            if (startTimeLT.compareTo(finishTimeLT) < 0) {
                                startTimeET.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute));
                            } else {
                                Toast.makeText(getApplicationContext(), "Introduce a valid time (greater than start time)", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case R.id.finishTimeET:
                        if (startTimeET.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "Introduce a start time first", Toast.LENGTH_SHORT).show();
                        } else {
                            LocalTime startTime = LocalTime.parse(startTimeET.getText().toString());
                            String finishTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute);
                            LocalTime finishTimeLT = LocalTime.parse(finishTime);
                            if (startTime.compareTo(finishTimeLT) < 0) {
                                finishTimeET.setText(finishTime);
                            } else {
                                Toast.makeText(getApplicationContext(), "Introduce a valid time (greater than start time)", Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, onTimeSetListener, hour, minute, true);
        timePickerDialog.setTitle("Select Time");
        timePickerDialog.show();
    }
}