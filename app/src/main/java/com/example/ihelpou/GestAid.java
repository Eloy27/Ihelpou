package com.example.ihelpou;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;

import java.util.Calendar;

public class GestAid extends AppCompatActivity {

    private EditText descriptionET, startTimeET, finishTimeET, firstDateET, secondDateET;
    private RadioButton especificDayRB, moreDaysRB;
    private ImageButton calendarBtn;
    private int day = 01, month = 01, year = 2022;
    private boolean firstDatePut = false;
    private GestClassDB gestClassDB = new GestClassDB();
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gest_aid);

        Intent i = getIntent();
        user = (User)i.getSerializableExtra("user");

        descriptionET = findViewById(R.id.descriptionET);
        startTimeET = findViewById(R.id.startTimeET);
        finishTimeET = findViewById(R.id.finishTimeET);
        firstDateET = findViewById(R.id.firstDateET);
        secondDateET = findViewById(R.id.secondDateET);
        especificDayRB = findViewById(R.id.especificDayRB);
        moreDaysRB = findViewById(R.id.moreDaysRB);
        descriptionET = findViewById(R.id.descriptionET);
        calendarBtn = findViewById(R.id.calendarBtn);
        calendarBtn.setImageResource(R.drawable.calendar);
        moreDaysRB.setChecked(true);

        especificDayRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (especificDayRB.isChecked()){
                    moreDaysRB.setChecked(false);
                    secondDateET.setVisibility(View.INVISIBLE);
                    firstDateET.setText("Select a day");
                    secondDateET.setText("");
                }
            }
        });

        moreDaysRB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moreDaysRB.isChecked()){
                    especificDayRB.setChecked(false);
                    secondDateET.setVisibility(View.VISIBLE);
                    firstDateET.setText("From");
                    secondDateET.setText("To");
                }
            }
        });
    }

    public void comeBack(View view){
        onBackPressed();
    }

    public void putDate(View view) {
        final Calendar c = Calendar.getInstance();
        day = c.get(Calendar.DAY_OF_MONTH);
        month = c.get(Calendar.MONTH);
        year = c.get(Calendar.YEAR);

        if (especificDayRB.isChecked()){
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> firstDateET.setText(day + "/" + (month + 1) + "/" + year), day, month, year);
            datePickerDialog.updateDate(2022, 01, 01);
            datePickerDialog.show();
            secondDateET.setText("");
        }
        else{
            if (!firstDatePut) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> firstDateET.setText(day + "/" + (month + 1) + "/" + year), day, month, year);
                datePickerDialog.updateDate(2022, 01, 01);
                datePickerDialog.show();
                firstDatePut = true;
            }
            else {
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> secondDateET.setText(day + "/" + (month + 1) + "/" + year), day, month, year);
                datePickerDialog.updateDate(2022, 01, 01);
                datePickerDialog.show();
                firstDatePut = false;
            }
        }
    }

    public void createAid(View view){
        Aid aid = new Aid(descriptionET.getText().toString());
        gestClassDB.addAid(aid, user).addOnSuccessListener(suc ->
        {
            Intent intent = new Intent(this, BeginingActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Aid added successfully", Toast.LENGTH_SHORT).show();

        }).addOnFailureListener(err ->
        {
            Toast.makeText(this, err.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}