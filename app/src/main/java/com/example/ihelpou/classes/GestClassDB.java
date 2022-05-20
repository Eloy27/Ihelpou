package com.example.ihelpou.classes;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ihelpou.R;
import com.example.ihelpou.activities.BeginingActivity;
import com.example.ihelpou.activities.MainActivity;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.AvailableDays;
import com.example.ihelpou.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class GestClassDB {

    private FirebaseAuth fsAuth;
    private FirebaseFirestore fsDB;

    public GestClassDB() {

        fsDB = FirebaseFirestore.getInstance();
        fsAuth = FirebaseAuth.getInstance();
    }

    public void registerUser(User user, Context c) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", user.getName());
        userMap.put("username", user.getUsername());
        userMap.put("password", user.getPassword());
        userMap.put("surname", user.getSurname());
        userMap.put("phone", user.getPhone());
        userMap.put("address", user.getAddress());
        userMap.put("age", user.getAge());

        fsAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                fsDB.collection("User").document(user.getEmail()).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(c, BeginingActivity.class);
                        intent.putExtra("user", user);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        c.startActivity(intent);
                        Toast.makeText(c, "User registered successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(c, "Unexpected Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(c, "Error registering user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void login(User userLogin, Context c) {
        fsAuth.signInWithEmailAndPassword(userLogin.getEmail(), userLogin.getPassword()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
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
                                            ObjectMapper mapper = new ObjectMapper();
                                            User user = mapper.convertValue(objectUser.getData(), User.class);
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

    public void registerAid(Aid aid, User user, Context c) {
        Map<String, Object> aidMap = new HashMap<>();
        aidMap.put("description", aid.getDescription());
        aidMap.put("startTime", aid.getStartTime());
        aidMap.put("finishTime", aid.getFinishTime());
        aidMap.put("day", aid.getDay());
        fsDB.collection("User").document(user.getEmail()).collection("Aid").document().set(aidMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent intent = new Intent(c, BeginingActivity.class);
                intent.putExtra("user", user);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(intent);
                Toast.makeText(c, "Aid added successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(c, "Error registering aid", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void getAids(User user, ArrayList<Aid> listAids, ListView listAidsLV, Context c) {
        fsDB.collection("User").document(user.getEmail()).collection("Aid")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            for (QueryDocumentSnapshot objectAid : query.getResult()) {
                                ObjectMapper mapper = new ObjectMapper();
                                Aid aid = mapper.convertValue(objectAid.getData(), Aid.class);
                                aid.setKey(objectAid.getId());
                                listAids.add(aid);
                                ListAdapter adapter = new ListAdapter(listAids, c);
                                listAidsLV.setAdapter(adapter);
                            }
                        } else {
                            Toast.makeText(c, "Unexpected Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void registerAvailableDay(AvailableDays availableDays, User user, Context c) {
        Map<String, Object> availableDayMap = new HashMap<>();
        availableDayMap.put("day", availableDays.getAvailableDays());
        availableDayMap.put("startTime", availableDays.getStartTime());
        availableDayMap.put("finishTime", availableDays.getFinishTime());
        fsDB.collection("User").document(user.getEmail()).collection("AvailableDay").document().set(availableDayMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent intent = new Intent(c, BeginingActivity.class);
                intent.putExtra("user", user);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(intent);
                Toast.makeText(c, "Availability added successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(c, "Unexpected Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editAvailableDay(AvailableDays availableDays, User user, Context c, String key) {
        Map<String, Object> availableDayMap = new HashMap<>();
        availableDayMap.put("day", availableDays.getAvailableDays());
        availableDayMap.put("startTime", availableDays.getStartTime());
        availableDayMap.put("finishTime", availableDays.getFinishTime());
        fsDB.collection("User").document(user.getEmail()).collection("AvailableDay").document(key).update(availableDayMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent intent = new Intent(c, BeginingActivity.class);
                intent.putExtra("user", user);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(intent);
                Toast.makeText(c, "Availability edited successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(c, "Unexpected Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkAvailability(User user, ImageButton button) {
        button.setImageResource(R.drawable.add);
        fsDB.collection("User").document(user.getEmail()).collection("AvailableDay")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            if (query.getResult().size() != 0) {
                                button.setImageResource(R.drawable.edit);
                            }
                        }
                    }
                });
    }


    public void sendAvailability(User user, Intent intent, Context c) {
        fsDB.collection("User").document(user.getEmail()).collection("AvailableDay")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            for (QueryDocumentSnapshot objectAvailableDays : query.getResult()) {
                                ObjectMapper mapper = new ObjectMapper();
                                AvailableDays availableDays = new AvailableDays(objectAvailableDays.getId(), mapper.convertValue(objectAvailableDays.get("day"), ArrayList.class), String.valueOf(objectAvailableDays.get("startTime")), String.valueOf(objectAvailableDays.get("finishTime")));
                                intent.putExtra("availableDays", availableDays);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                c.startActivity(intent);

                            }
                        }
                    }
                });
    }

    public void getAidsAccordingAvailability(User actualUser, ArrayList<Aid> listAidsAvailables, RecyclerView listAidsAvailablesRV, Context c) {
        fsDB.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            for (QueryDocumentSnapshot objectUser : query.getResult()) {
                                fsDB.collection("User").document(objectUser.getId()).collection("Aid")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> query) {
                                                if (query.isSuccessful()) {
                                                    for (QueryDocumentSnapshot objectAid : query.getResult()) {
                                                        ObjectMapper mapper = new ObjectMapper();
                                                        Aid aid = mapper.convertValue(objectAid.getData(), Aid.class);
                                                        aid.setKey(objectAid.getId());
                                                        LocalTime aidST = LocalTime.parse(aid.getStartTime());
                                                        LocalTime aidFT = LocalTime.parse(aid.getFinishTime());
                                                        fsDB.collection("User").document(actualUser.getEmail()).collection("AvailableDay")
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                                                                        if (query.isSuccessful()) {
                                                                            for (QueryDocumentSnapshot objectAvailableDay : query.getResult()) {
                                                                                ObjectMapper mapper = new ObjectMapper();
                                                                                AvailableDays availableDays = new AvailableDays(objectAvailableDay.getId(), mapper.convertValue(objectAvailableDay.get("day"), ArrayList.class), String.valueOf(objectAvailableDay.get("startTime")), String.valueOf(objectAvailableDay.get("finishTime")));
                                                                                LocalTime availableDaysST = LocalTime.parse(availableDays.getStartTime());
                                                                                LocalTime availableDaysFT = LocalTime.parse(availableDays.getFinishTime());

                                                                                if (aidST.compareTo(availableDaysST) >= 0 && aidFT.compareTo(availableDaysFT) <= 0) {
                                                                                    Calendar calendar = Calendar.getInstance();
                                                                                    String aidDayOfWeek = "";

                                                                                    try {
                                                                                        Date aidDay = new SimpleDateFormat("dd/MM/yyyy").parse(aid.getDay());
                                                                                        calendar.setTime(aidDay);
                                                                                        aidDayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
                                                                                    } catch (ParseException e) {
                                                                                        e.printStackTrace();
                                                                                    }

                                                                                    for (HashMap mapDay : availableDays.getAvailableDays()) {
                                                                                        boolean insert = false;
                                                                                        for (Object dayAndAvailability : mapDay.values()) {
                                                                                            if (dayAndAvailability.toString().equals("available")) {
                                                                                                insert = true;
                                                                                            } else if (dayAndAvailability.toString().equals(aidDayOfWeek) && insert == true) {
                                                                                                if (!actualUser.getEmail().equals(objectUser.getId())) {
                                                                                                    listAidsAvailables.add(aid);
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    RecyclerAdapter listAidsAdapter = new RecyclerAdapter(listAidsAvailables, c);
                                                                                    listAidsAvailablesRV.setAdapter(listAidsAdapter);
                                                                                }
                                                                            }
                                                                        } else {
                                                                            Toast.makeText(c, "Unexpected Error", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                } else {
                                                    Toast.makeText(c, "Unexpected Error", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    public void getHelpersAccordingAid(ArrayList<User> listHelpersAvailables, RecyclerView listHelpersAvailablesRV, Aid aid, User actualUser, Context c) {
        fsDB.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            for (QueryDocumentSnapshot objectUser : query.getResult()) {
                                ObjectMapper mapper = new ObjectMapper();
                                User user = mapper.convertValue(objectUser.getData(), User.class);
                                user.setEmail(objectUser.getId());
                                fsDB.collection("User").document(user.getEmail()).collection("AvailableDay")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> query) {
                                                for (QueryDocumentSnapshot objectAvailableDay : query.getResult()) {
                                                    ObjectMapper mapper = new ObjectMapper();
                                                    AvailableDays availableDays = new AvailableDays(objectAvailableDay.getId(), mapper.convertValue(objectAvailableDay.get("day"), ArrayList.class), String.valueOf(objectAvailableDay.get("startTime")), String.valueOf(objectAvailableDay.get("finishTime")));
                                                    LocalTime availableDaysST = LocalTime.parse(availableDays.getStartTime());
                                                    LocalTime availableDaysFT = LocalTime.parse(availableDays.getFinishTime());
                                                    LocalTime aidST = LocalTime.parse(aid.getStartTime());
                                                    LocalTime aidFT = LocalTime.parse(aid.getFinishTime());

                                                    if (aidST.compareTo(availableDaysST) >= 0 && aidFT.compareTo(availableDaysFT) <= 0) {
                                                        Calendar calendar = Calendar.getInstance();
                                                        String aidDayOfWeek = "";

                                                        try {
                                                            Date aidDay = new SimpleDateFormat("dd/MM/yyyy").parse(aid.getDay());
                                                            calendar.setTime(aidDay);
                                                            aidDayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }

                                                        for (HashMap mapDay : availableDays.getAvailableDays()) {
                                                            boolean insert = false;
                                                            for (Object dayAndAvailability : mapDay.values()) {
                                                                if (dayAndAvailability.toString().equals("available")) {
                                                                    insert = true;
                                                                } else if (dayAndAvailability.toString().equals(aidDayOfWeek) && insert) {
                                                                    if (!actualUser.getEmail().equals(objectUser.getId())) {
                                                                        listHelpersAvailables.add(user);
                                                                    }
                                                                }
                                                            }
                                                        }

                                                        RecyclerAdapterHelper listHelpersAdapter = new RecyclerAdapterHelper(listHelpersAvailables, c, actualUser, aid);
                                                        listHelpersAvailablesRV.setAdapter(listHelpersAdapter);
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    public void setInfoHelper(User user, TextView startTime, TextView finishTime, ImageButton mondayBtn, ImageButton tuesdayBtn, ImageButton wednesdayBtn, ImageButton thursdayBtn, ImageButton fridayBtn, ImageButton saturdayBtn, ImageButton sundayBtn) {
        fsDB.collection("User").document(user.getEmail()).collection("AvailableDay")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            for (QueryDocumentSnapshot objectAvailableDay : query.getResult()) {
                                ObjectMapper mapper = new ObjectMapper();
                                AvailableDays availableDays = new AvailableDays(objectAvailableDay.getId(), mapper.convertValue(objectAvailableDay.get("day"), ArrayList.class), String.valueOf(objectAvailableDay.get("startTime")), String.valueOf(objectAvailableDay.get("finishTime")));
                                startTime.setText(startTime.getText() + " " + availableDays.getStartTime());
                                finishTime.setText(finishTime.getText() + " " + availableDays.getFinishTime());

                                if (availableDays.getAvailableDays().contains("Monday")) {
                                    mondayBtn.setImageResource(R.drawable.mondayrojo);
                                }
                                if (availableDays.getAvailableDays().contains("Tuesday")) {
                                    tuesdayBtn.setImageResource(R.drawable.tdayrojo);
                                }
                                if (availableDays.getAvailableDays().contains("Wednesday")) {
                                    wednesdayBtn.setImageResource(R.drawable.wednesdayrojo);
                                }
                                if (availableDays.getAvailableDays().contains("Thursday")) {
                                    thursdayBtn.setImageResource(R.drawable.tdayrojo);
                                }
                                if (availableDays.getAvailableDays().contains("Friday")) {
                                    fridayBtn.setImageResource(R.drawable.fridayrojo);
                                }
                                if (availableDays.getAvailableDays().contains("Saturday")) {
                                    saturdayBtn.setImageResource(R.drawable.sdayrojo);
                                }
                                if (availableDays.getAvailableDays().contains("Sunday")) {
                                    sundayBtn.setImageResource(R.drawable.sdayrojo);
                                }
                            }
                        }
                    }
                });
    }

    /*
    public void setHolderTime(RecyclerAdapterHelper.HelpersViewHolder holder, User user, boolean startOFinish) {
        fsDB.collection("User").document(user.getEmail()).collection("AvailableDay")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        for (QueryDocumentSnapshot objectAvailableDay : query.getResult()) {
                            ObjectMapper mapper = new ObjectMapper();
                            AvailableDays availableDays = new AvailableDays(objectAvailableDay.getId(), mapper.convertValue(objectAvailableDay.get("day"), ArrayList.class), String.valueOf(objectAvailableDay.get("startTime")), String.valueOf(objectAvailableDay.get("finishTime")));
                            if (startOFinish) {
                                holder.startTimeTV.setText("From: " + availableDays.getStartTime());
                            } else {
                                holder.finishTimeTV.setText("To: " + availableDays.getFinishTime());
                            }
                        }
                    }
                });
    }

    public void test(User user) {
        fsDB.collection("User").document(user.getEmail()).collection("AvailableDay")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            for (QueryDocumentSnapshot objectAvailableDay : query.getResult()) {
                                ObjectMapper mapper = new ObjectMapper();
                                AvailableDays availableDays = new AvailableDays(objectAvailableDay.getId(), mapper.convertValue(objectAvailableDay.get("day"), ArrayList.class), String.valueOf(objectAvailableDay.get("startTime")), String.valueOf(objectAvailableDay.get("finishTime")));

                                for (HashMap mapDay : availableDays.getAvailableDays()) {
                                    Log.e("mapDay", mapDay.toString() + "");
                                    Log.e("mapDay", mapDay.values() + "");
                                    for (Object dayAndAvailability : mapDay.values()) {
                                        Log.e("dayAndAvailability", dayAndAvailability.toString() + "");
                                    }
                                }
                            }
                        }
                    }
                });
    }
    */
}
