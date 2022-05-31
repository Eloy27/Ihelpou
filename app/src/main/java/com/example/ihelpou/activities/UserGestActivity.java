package com.example.ihelpou.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ihelpou.R;
import com.example.ihelpou.classes.GestClassDB;
import com.example.ihelpou.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDate;
import java.util.Calendar;

public class UserGestActivity extends AppCompatActivity {

    private EditText nameET, usernameET, passwordET, passwordConfirmET, surnameET, addressET, phoneET, emailET, dateOfBirthET;
    private ImageButton backBtn, avatarBtn;
    private Button okBtn;
    private GestClassDB gestClassDB = new GestClassDB();
    private int day = 01, month = 01, year = 2022;
    private String where, strEmail, error;
    private User user;
    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();
    private TextView crteUserAccountTV, textView;
    private TextInputLayout nameTIL, usernameTIL, passwordTIL, passwordConfirmTIL, surnameTIL, addressTIL, phoneTIL, emailTIL, dateOfBirthTIL;
    private LinearProgressIndicator lpi;
    private LinearLayout editingID;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_gest);

        backBtn = findViewById(R.id.backBtn);
        nameET = findViewById(R.id.nameET);
        nameTIL = findViewById(R.id.nameTIL);

        usernameET = findViewById(R.id.usernameET);
        usernameTIL = findViewById(R.id.usernameTIL);

        passwordET = findViewById(R.id.passwordET);
        passwordTIL = findViewById(R.id.passwordTIL);

        passwordConfirmET = findViewById(R.id.passwordNewET);
        passwordConfirmTIL = findViewById(R.id.passwordNewTIL);

        surnameET = findViewById(R.id.surnameET);
        surnameTIL = findViewById(R.id.surnameTIL);

        addressET = findViewById(R.id.addressET);
        addressTIL = findViewById(R.id.addressTIL);

        phoneET = findViewById(R.id.phoneET);
        phoneTIL = findViewById(R.id.phoneTIL);

        emailET = findViewById(R.id.emailET);
        emailTIL = findViewById(R.id.emailTIL);

        dateOfBirthET = findViewById(R.id.dateOfBirthET);
        dateOfBirthTIL = findViewById(R.id.dateOfBirthTIL);

        crteUserAccountTV = findViewById(R.id.crteUserAccountTV);
        editingID = findViewById(R.id.editingID);
        lpi = findViewById(R.id.linearIndicator);
        relativeLayout = findViewById(R.id.relativeLayout);
        textView = findViewById(R.id.textView);

        avatarBtn = findViewById(R.id.avatarBtn);
        avatarBtn.setImageResource(R.drawable.avatar);
        okBtn = findViewById(R.id.okBtn);

        Intent i = getIntent();
        where = i.getStringExtra("where");
        error =i.getStringExtra("error");

        if (error != null){
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        }

        if (where != null) {
            okBtn.setText("Edit");
            crteUserAccountTV.setText("Edit your user");
            passwordTIL.setHint("Old Password");
            passwordConfirmTIL.setHint("New Password");
            getUser(gestClassDB.getEmailActualUser(this));
        }

        nameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                gestClassDB.textChanges(nameET, nameTIL, nameET.getText().toString().matches("[A-zÀ-ÿ ]{4,20}"), "It cannot contain numbers and must contain between 4 and 20 characters");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        usernameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                gestClassDB.textChanges(usernameET, usernameTIL, usernameET.getText().toString().matches("^[A-z][0-9A-zÀ-ÿ_-]{7,30}"), "Minimum 8 alphanumeric characters and first one must be a letter, not too long");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        emailET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                gestClassDB.textChanges(emailET, emailTIL, emailET.getText().toString().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"), "Wrong email format, use for example example@gmail.com");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        passwordET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                gestClassDB.textChanges(passwordET, passwordTIL, passwordET.getText().toString().matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,30}$"), "Minimum 8 alphanumeric/special characters and at least one uppercase letter and one number, not too long");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        passwordConfirmET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (where != null) {
                    if (passwordET.getText().toString().equals(passwordConfirmET.getText().toString()))
                        gestClassDB.textChanges(passwordConfirmET, passwordConfirmTIL, false, "It must be different as password");
                    else
                        gestClassDB.textChanges(passwordConfirmET, passwordConfirmTIL, passwordConfirmET.getText().toString().matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,30}$"), "It must be different and with the same requirements as the password");
                } else {
                    if (passwordET.getText().toString().equals(passwordConfirmET.getText().toString()))
                        gestClassDB.textChanges(passwordConfirmET, passwordConfirmTIL, true, "It must be the same as password");
                    else
                        gestClassDB.textChanges(passwordConfirmET, passwordConfirmTIL, false, "It must be the same as password");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        surnameET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                gestClassDB.textChanges(surnameET, surnameTIL, surnameET.getText().toString().matches("[A-zÀ-ÿ ]{4,30}"), "It cannot contain numbers and must contain between 4 and 30 characters");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        phoneET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                gestClassDB.textChanges(phoneET, phoneTIL, phoneET.getText().toString().matches("^\\+(?:[0-9]●?){6,14}[0-9]$"), "It cannot contain letters or symbols and must contain prefix, not too long");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        addressET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                gestClassDB.textChanges(addressET, addressTIL, addressET.getText().toString().matches("[0-9A-zÀ-ÿ ]{5,40}"), "Between 5 and 40 characters");
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkName() && checkUsername() && checkEmail() && checkPassword() && checkConfirmPassword() && checkSurname() && checkPhone() && checkAddress()) {
                    User user = new User(emailET.getText().toString(), nameET.getText().toString(), usernameET.getText().toString(),
                            surnameET.getText().toString(), phoneET.getText().toString(), addressET.getText().toString(),
                            dateOfBirthET.getText().toString());
                    if (where != null) {
                        editingID.setVisibility(View.VISIBLE);
                        relativeLayout.setVisibility(View.INVISIBLE);
                        gestClassDB.editUser(user, passwordET.getText().toString(), passwordConfirmET.getText().toString(), strEmail, lpi, getApplicationContext());
                    } else {
                        textView.setText("Creating your profile");
                        editingID.setVisibility(View.VISIBLE);
                        relativeLayout.setVisibility(View.INVISIBLE);
                        gestClassDB.registerUser(user, passwordConfirmET.getText().toString(), lpi, getApplicationContext());
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Check your data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean checkName() {
        return nameET.getText().toString().matches("[A-zÀ-ÿ ]{4,20}");
    }

    public boolean checkUsername() {
        return usernameET.getText().toString().matches("^[A-z][0-9A-zÀ-ÿ_-]{7,30}");
    }

    public boolean checkEmail() {
        return emailET.getText().toString().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    }

    public boolean checkPassword() {
        return passwordET.getText().toString().matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,30}$");
    }

    public boolean checkConfirmPassword() {
        if (where != null) {
            if (passwordET.getText().toString().equals(passwordConfirmET.getText().toString()))
                return false;
            else
                return passwordConfirmET.getText().toString().matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,30}$");
        } else {
            if (passwordET.getText().toString().equals(passwordConfirmET.getText().toString()))
                return true;
            else
                return false;
        }
    }

    public boolean checkSurname() {
        return surnameET.getText().toString().matches("[A-zÀ-ÿ ]{4,30}");
    }

    public boolean checkPhone() {
        return phoneET.getText().toString().matches("^\\+(?:[0-9]●?){6,14}[0-9]$");
    }

    public boolean checkAddress() {
        return addressET.getText().toString().matches("[0-9A-zÀ-ÿ ]{5,40}");
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
                                    strEmail = user.getEmail();
                                    nameET.setText(user.getName());
                                    usernameET.setText(user.getUsername());
                                    surnameET.setText(user.getSurname());
                                    addressET.setText(user.getAddress());
                                    phoneET.setText(user.getPhone());
                                    emailET.setText(user.getEmail());
                                    dateOfBirthET.setText(user.getDateOfBirth());
                                }
                            }
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, year, month, day) -> dateOfBirthET.setText(day + "/" + (month + 1) + "/" + year), day, month, year);
        LocalDate currentDate = LocalDate.parse(LocalDate.now().toString());
        datePickerDialog.updateDate(2000, currentDate.getMonthValue() - 1, currentDate.getDayOfMonth());
        datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis() - Long.parseLong("220903200000"));
        datePickerDialog.getDatePicker().setMinDate(c.getTimeInMillis() - Long.parseLong("3155760000000"));
        datePickerDialog.show();
    }
}