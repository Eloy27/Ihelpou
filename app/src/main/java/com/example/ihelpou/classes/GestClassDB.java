package com.example.ihelpou.classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.ihelpou.R;
import com.example.ihelpou.activities.BeginingActivity;
import com.example.ihelpou.activities.GestAvailableDaysActivity;
import com.example.ihelpou.activities.HelpersActivity;
import com.example.ihelpou.activities.InfoUserActivity;
import com.example.ihelpou.databinding.ActivityBeginingBinding;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.AvailableDays;
import com.example.ihelpou.models.User;
import com.example.ihelpou.ui.main.SectionsPagerAdapter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    public void registerUser(User user, String password, Context c) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", user.getName());
        userMap.put("username", user.getUsername());
        userMap.put("surname", user.getSurname());
        userMap.put("phone", user.getPhone());
        userMap.put("address", user.getAddress());
        userMap.put("age", user.getAge());

        fsAuth.createUserWithEmailAndPassword(user.getEmail(), password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                fsDB.collection("User").document(user.getEmail()).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        savePreferences(user.getEmail(), password, c);
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

    public void savePreferences(String email, String password, Context c) {

        SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(c);
        SharedPreferences.Editor editor = pm.edit();
        editor.putString("email", email);
        editor.putString("pass", password);
        editor.apply();
    }

    public void registerAid(Aid aid, User user, Context c) {
        Map<String, Object> aidMap = new HashMap<>();
        aidMap.put("description", aid.getDescription());
        aidMap.put("startTime", aid.getStartTime());
        aidMap.put("finishTime", aid.getFinishTime());
        aidMap.put("day", aid.getDay());
        aidMap.put("done", aid.getDone());
        aidMap.put("idHelper", aid.getIdHelper());
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

    public void editAid(Aid aid, User user, Context c, String key) {
        Map<String, Object> aidMap = new HashMap<>();
        aidMap.put("description", aid.getDescription());
        aidMap.put("startTime", aid.getStartTime());
        aidMap.put("finishTime", aid.getFinishTime());
        aidMap.put("day", aid.getDay());
        fsDB.collection("User").document(user.getEmail()).collection("Aid").document(key).update(aidMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent intent = new Intent(c, BeginingActivity.class);
                intent.putExtra("user", user);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                c.startActivity(intent);
                Toast.makeText(c, "Aid edited successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(c, "Unexpected Error", Toast.LENGTH_SHORT).show();
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
                            listAids.clear();
                            for (QueryDocumentSnapshot objectAid : query.getResult()) {
                                Aid aid = objectAid.toObject(Aid.class);
                                aid.setKey(objectAid.getId());
                                listAids.add(aid);
                                ListAdapter adapter = new ListAdapter(listAids, c, user, 'r');
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

    public void deleteAvailability(User user, Context c, String key) {
        fsDB.collection("User").document(user.getEmail()).collection("AvailableDay")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            for (QueryDocumentSnapshot objectAvailableDay : query.getResult()) {
                                ObjectMapper mapper = new ObjectMapper();
                                AvailableDays availableDays = new AvailableDays(objectAvailableDay.getId(), mapper.convertValue(objectAvailableDay.get("day"), ArrayList.class), String.valueOf(objectAvailableDay.get("startTime")), String.valueOf(objectAvailableDay.get("finishTime")));
                                boolean busy = false;
                                for (HashMap mapDay : availableDays.getAvailableDays()) {
                                    if (mapDay.get("availability").equals("busy")) {
                                        busy = true;
                                    }
                                }
                                if (busy) {
                                    Toast.makeText(c, "You can't because you have busy days", Toast.LENGTH_SHORT).show();
                                } else {
                                    fsDB.collection("User").document(user.getEmail()).collection("AvailableDay").document(key).delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Toast.makeText(c, "Your availability has been deleted successfully", Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(c, BeginingActivity.class);
                                                        intent.putExtra("user", user);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        c.startActivity(intent);
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    }
                });
    }

    public void checkAvailability(User user, ImageButton button, Context c, String where) {
        fsDB.collection("User").document(user.getEmail()).collection("AvailableDay")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            if (where.equals("BeginingActivity") && query.getResult().size() == 0) {
                                Intent intent = new Intent(c, GestAvailableDaysActivity.class);
                                intent.putExtra("user", user);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                c.startActivity(intent);
                            }
                            if (where.equals("HelpSeekerFragment") && query.getResult().size() != 0) {
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

    public void getHelpSeekerAccordingAid(Aid aid, User actualUser, Context c) {
        fsDB.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            for (QueryDocumentSnapshot objectUser : query.getResult()) {
                                User user = objectUser.toObject(User.class);
                                user.setEmail(objectUser.getId());
                                fsDB.collection("User").document(objectUser.getId()).collection("Aid")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> query2) {
                                                if (query2.isSuccessful()) {
                                                    for (QueryDocumentSnapshot objectAid : query2.getResult()) {
                                                        if (objectAid.getId().equals(aid.getKey())) {
                                                            Intent i = new Intent(c, InfoUserActivity.class);
                                                            i.putExtra("helper", actualUser);
                                                            i.putExtra("user", user);
                                                            i.putExtra("aid", aid);
                                                            i.putExtra("where", "offer");
                                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            c.startActivity(i);
                                                        }
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }


    public void getAidsAccordingAvailability(User actualUser, ArrayList<Aid> listAidsAvailables, ListView listAidsAvailablesLV, Context c) {
        fsDB.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            listAidsAvailables.clear();
                            for (QueryDocumentSnapshot objectUser : query.getResult()) {
                                fsDB.collection("User").document(objectUser.getId()).collection("Aid")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> query) {
                                                if (query.isSuccessful()) {
                                                    for (QueryDocumentSnapshot objectAid : query.getResult()) {
                                                        Aid aid = objectAid.toObject(Aid.class);
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
                                                                                                if (!actualUser.getEmail().equals(objectUser.getId()) && aid.getDone().equals("no")) {
                                                                                                    if (!listAidsAvailables.contains(aid)) {
                                                                                                        listAidsAvailables.add(aid);
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }

                                                                                    ListAdapter adapter = new ListAdapter(listAidsAvailables, c, actualUser, 'o');
                                                                                    listAidsAvailablesLV.setAdapter(adapter);
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


    public void checkHelpersAccordingAid(Aid aid, User actualUser, Context c) {
        fsDB.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> queryUsers) {
                        if (queryUsers.isSuccessful()) {
                            for (QueryDocumentSnapshot objectUser : queryUsers.getResult()) {
                                User user = objectUser.toObject(User.class);
                                user.setEmail(objectUser.getId());
                                fsDB.collection("User").document(user.getEmail()).collection("AvailableDay")
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
                                                                            Intent intent = new Intent(c, HelpersActivity.class);
                                                                            intent.putExtra("aid", aid);
                                                                            intent.putExtra("user", actualUser);
                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                            c.startActivity(intent);
                                                                            return;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
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
                            listHelpersAvailables.clear();
                            for (QueryDocumentSnapshot objectUser : query.getResult()) {
                                User user = objectUser.toObject(User.class);
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


    public void showHelperAccordingAid(Aid aid, User actualUser, Context c) {
        fsDB.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            for (QueryDocumentSnapshot objectUser : query.getResult()) {
                                User user = objectUser.toObject(User.class);
                                user.setEmail(objectUser.getId());
                                if (aid.getIdHelper().equals(user.getEmail())) {
                                    Intent i = new Intent(c, InfoUserActivity.class);
                                    i.putExtra("helper", user);
                                    i.putExtra("user", actualUser);
                                    i.putExtra("aid", aid);
                                    i.putExtra("putButton", "no");
                                    i.putExtra("where", "request");
                                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    c.startActivity(i);
                                }
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

                                for (int i = 0; i < availableDays.getAvailableDays().size(); i++) {
                                    availableDays.getAvailableDays().get(i).forEach((s, o) -> {
                                        if (o.equals("Monday")) {
                                            mondayBtn.setImageResource(R.drawable.mondayrojo);
                                        }
                                        if (o.equals("Tuesday")) {
                                            tuesdayBtn.setImageResource(R.drawable.tdayrojo);
                                        }
                                        if (o.equals("Wednesday")) {
                                            wednesdayBtn.setImageResource(R.drawable.wednesdayrojo);
                                        }
                                        if (o.equals("Thursday")) {
                                            thursdayBtn.setImageResource(R.drawable.tdayrojo);
                                        }
                                        if (o.equals("Friday")) {
                                            fridayBtn.setImageResource(R.drawable.fridayrojo);
                                        }
                                        if (o.equals("Saturday")) {
                                            saturdayBtn.setImageResource(R.drawable.sdayrojo);
                                        }
                                        if (o.equals("Sunday")) {
                                            sundayBtn.setImageResource(R.drawable.sdayrojo);
                                        }
                                    });
                                }

                            }
                        }
                    }
                });
    }

    public void aidPending(Aid aid, User helper, User user, Context c, String where) {
        Map<String, Object> aidMap = new HashMap<>();
        aidMap.put("done", "pending");
        aidMap.put("idHelper", helper.getEmail());
        fsDB.collection("User").document(user.getEmail()).collection("Aid").document(aid.getKey()).update(aidMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                fsDB.collection("User").document(helper.getEmail()).collection("AvailableDay")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> query) {
                                if (query.isSuccessful()) {
                                    for (QueryDocumentSnapshot objectAvailableDay : query.getResult()) {
                                        ObjectMapper mapper = new ObjectMapper();
                                        AvailableDays availableDays = new AvailableDays(objectAvailableDay.getId(), mapper.convertValue(objectAvailableDay.get("day"), ArrayList.class), String.valueOf(objectAvailableDay.get("startTime")), String.valueOf(objectAvailableDay.get("finishTime")));
                                        Calendar calendar = Calendar.getInstance();
                                        Map<String, Object> availableDayMap = new HashMap<>();
                                        for (int i = 0; i < availableDays.getAvailableDays().size(); i++) {
                                            int finalI = i;
                                            availableDays.getAvailableDays().get(i).forEach((s, o) -> {
                                                String aidDayOfWeek = "";
                                                try {
                                                    Date aidDay = new SimpleDateFormat("dd/MM/yyyy").parse(aid.getDay());
                                                    calendar.setTime(aidDay);
                                                    aidDayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
                                                } catch (ParseException e) {
                                                    e.printStackTrace();
                                                }
                                                if (o.equals(aidDayOfWeek)) {
                                                    availableDays.getAvailableDays().get(finalI).put("availability", "busy");
                                                    availableDays.getAvailableDays().get(finalI).put("idAid", aid.getKey());
                                                }
                                            });
                                            availableDayMap.put("day", availableDays.getAvailableDays());
                                            availableDayMap.put("startTime", availableDays.getStartTime());
                                            availableDayMap.put("finishTime", availableDays.getFinishTime());
                                        }
                                        fsDB.collection("User").document(helper.getEmail()).collection("AvailableDay").document(objectAvailableDay.getId()).delete();
                                        fsDB.collection("User").document(helper.getEmail()).collection("AvailableDay").document(objectAvailableDay.getId()).set(availableDayMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(c, "You just have to wait", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(c, BeginingActivity.class);
                                                if (where.equals("request")) {
                                                    intent.putExtra("user", user);
                                                } else {
                                                    intent.putExtra("user", helper);
                                                }
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                c.startActivity(intent);
                                            }
                                        });
                                    }
                                }
                            }
                        });
            }
        });
    }

    public void getPendingAids(User user, ArrayList<Aid> listAids, ListView listAidsLV, Context c) {
        fsDB.collection("User").document(user.getEmail()).collection("AvailableDay")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            listAids.clear();
                            for (QueryDocumentSnapshot objectAvailableDay : query.getResult()) {
                                ObjectMapper mapper = new ObjectMapper();
                                AvailableDays availableDays = new AvailableDays(objectAvailableDay.getId(), mapper.convertValue(objectAvailableDay.get("day"), ArrayList.class), String.valueOf(objectAvailableDay.get("startTime")), String.valueOf(objectAvailableDay.get("finishTime")));
                                for (HashMap mapDay : availableDays.getAvailableDays()) {
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
                                                                                    if (mapDay.get("idAid").toString().equals(objectAid.getId())) {
                                                                                        Aid aid = objectAid.toObject(Aid.class);
                                                                                        aid.setKey(objectAid.getId());
                                                                                        if (!listAids.contains(aid)) {
                                                                                            listAids.add(aid);
                                                                                        }
                                                                                        ListAdapter listAidsAdapter = new ListAdapter(listAids, c, user, 'p');
                                                                                        listAidsLV.setAdapter(listAidsAdapter);
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            Toast.makeText(c, "Unexpected Error", Toast.LENGTH_SHORT).show();
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
