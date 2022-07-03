package com.example.ihelpou.classes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ihelpou.R;
import com.example.ihelpou.activities.GestAvailableDaysActivity;
import com.example.ihelpou.activities.HelpersActivity;
import com.example.ihelpou.activities.InfoUserActivity;
import com.example.ihelpou.activities.InitialActivity;
import com.example.ihelpou.activities.MainActivity;
import com.example.ihelpou.activities.UserGestActivity;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.AvailableDays;
import com.example.ihelpou.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputLayout;
import com.google.api.Distribution;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class GestClassDB {

    private FirebaseAuth fsAuth;
    private FirebaseFirestore fsDB;
    public RecyclerAdapterAids adapter;
    private int cont = 0;
    private boolean control = false;

    public GestClassDB() {

        fsDB = FirebaseFirestore.getInstance();
        fsAuth = FirebaseAuth.getInstance();
    }

    public void registerUser(User user, String password, LinearProgressIndicator lpi, Context c, Uri selectedImage) {
        lpi.setProgressCompat(20, true);
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("name", user.getName());
        userMap.put("surname", user.getSurname());
        userMap.put("phone", user.getPhone());
        userMap.put("address", user.getAddress());
        userMap.put("dateOfBirth", user.getDateOfBirth());
        lpi.setProgressCompat(40, true);

        fsAuth.createUserWithEmailAndPassword(user.getEmail(), password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                lpi.setProgressCompat(60, true);
                fsDB.collection("User").document(user.getEmail()).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        lpi.setProgressCompat(80, true);
                        savePreferences(user.getEmail(), password, c);
                        uploadImage(selectedImage, user.getEmail());
                        lpi.setProgressCompat(100, true);
                        Intent intent = new Intent(c, InitialActivity.class);
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
                if (e.getClass().equals(FirebaseAuthUserCollisionException.class)) {
                    try {
                        comeBackTo("UserGestActivity", "login", c, "Email already exists");
                    } catch (ClassNotFoundException cnfe) {
                        e.printStackTrace();
                    }
                } else if (e.getClass().equals(FirebaseAuthWeakPasswordException.class)) {
                    try {
                        comeBackTo("UserGestActivity", "login", c, "Password is too weak");
                    } catch (ClassNotFoundException cnfe) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        comeBackTo("UserGestActivity", "login", c, "Error registering user");
                    } catch (ClassNotFoundException cnfe) {
                        e.printStackTrace();
                    }
                }

            }
        });
    }

    public void editUser(User user, String oldPassword, String newPassword, String oldEmail, LinearProgressIndicator lpi, Context c, Uri selectedImage) {
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(oldEmail, oldPassword);
        lpi.setProgressCompat(20, true);
        fbUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            fbUser.updateEmail(user.getEmail()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser fbUser2 = FirebaseAuth.getInstance().getCurrentUser();
                                        AuthCredential credential2 = EmailAuthProvider
                                                .getCredential(user.getEmail(), oldPassword);
                                        lpi.setProgressCompat(40, true);

                                        fbUser2.reauthenticate(credential2)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            lpi.setProgressCompat(60, true);

                                                            fbUser2.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        Log.e("TAG", "Password updated");
                                                                        Map<String, Object> userMap = new HashMap<>();
                                                                        userMap.put("name", user.getName());
                                                                        userMap.put("surname", user.getSurname());
                                                                        userMap.put("phone", user.getPhone());
                                                                        userMap.put("address", user.getAddress());
                                                                        userMap.put("dateOfBirth", user.getDateOfBirth());
                                                                        fsDB.collection("User").document(oldEmail).delete();
                                                                        lpi.setProgressCompat(80, true);

                                                                        fsDB.collection("User").document(user.getEmail()).set(userMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                lpi.setProgressCompat(100, true);
                                                                                SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(c);
                                                                                SharedPreferences.Editor editor = pm.edit();
                                                                                editor.clear();
                                                                                editor.apply();
                                                                                savePreferences(user.getEmail(), newPassword, c);
                                                                                uploadImage(selectedImage, user.getEmail());
                                                                                Intent i = new Intent(c, MainActivity.class);
                                                                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                                c.startActivity(i);
                                                                                Toast.makeText(c, "User edited successfully", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        }).addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(c, "Error editing user", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                                    } else {
                                                                        Toast.makeText(c, "Failed to update password", Toast.LENGTH_SHORT).show();
                                                                        try {
                                                                            comeBackTo("UserGestActivity", "edit", c, null);
                                                                        } catch (ClassNotFoundException e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText(c, "Check your email and old password", Toast.LENGTH_SHORT).show();
                                                            try {
                                                                comeBackTo("UserGestActivity", "edit", c, null);
                                                            } catch (ClassNotFoundException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(c, "Failed to update email", Toast.LENGTH_SHORT).show();
                                        try {
                                            comeBackTo("UserGestActivity", "edit", c, null);
                                        } catch (ClassNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(c, "Check your email and old password", Toast.LENGTH_SHORT).show();
                            try {
                                comeBackTo("UserGestActivity", "edit", c, null);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
    }

    public void deleteAccount(User user, Context c, LinearProgressIndicator lpi, RelativeLayout
            relativeLayout, LinearLayout linearLayout) {
        fsDB.collection("User").document(user.getEmail()).collection("AvailableDay")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        lpi.setProgressCompat(20, true);
                        if (query.getResult().size() != 0) {
                            if (query.isSuccessful()) {
                                lpi.setProgressCompat(40, true);
                                for (QueryDocumentSnapshot objectAvailableDay : query.getResult()) {
                                    lpi.setProgressCompat(60, true);
                                    ObjectMapper mapper = new ObjectMapper();
                                    AvailableDays availableDays = new AvailableDays(objectAvailableDay.getId(), mapper.convertValue(objectAvailableDay.get("day"), ArrayList.class), String.valueOf(objectAvailableDay.get("startTime")), String.valueOf(objectAvailableDay.get("finishTime")));
                                    boolean busy = false;
                                    for (HashMap mapDay : availableDays.getAvailableDays()) {
                                        if (mapDay.get("availability").equals("busy")) {
                                            busy = true;
                                        }
                                    }
                                    lpi.setProgressCompat(80, true);
                                    if (busy) {
                                        relativeLayout.setVisibility(View.VISIBLE);
                                        linearLayout.setVisibility(View.INVISIBLE);
                                        Toast.makeText(c, "You can't because you have busy days", Toast.LENGTH_SHORT).show();
                                    } else {
                                        fsDB.collection("User").document(user.getEmail()).delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        lpi.setProgressCompat(100, true);
                                                        if (task.isSuccessful()) {
                                                            FirebaseAuth.getInstance().getCurrentUser().delete();
                                                            deleteImage(Uri.parse(user.getEmail()));
                                                            SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(c);
                                                            SharedPreferences.Editor editor = pm.edit();
                                                            editor.clear();
                                                            editor.apply();
                                                            Toast.makeText(c, "Your profile has been deleted successfully", Toast.LENGTH_SHORT).show();
                                                            Intent intent = new Intent(c, MainActivity.class);
                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            c.startActivity(intent);
                                                        }
                                                    }
                                                });
                                    }
                                }
                            }
                        } else {
                            fsDB.collection("User").document(user.getEmail()).delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            lpi.setProgressCompat(40, true);
                                            if (task.isSuccessful()) {
                                                FirebaseAuth.getInstance().getCurrentUser().delete();
                                                deleteImage(Uri.parse(user.getEmail()));
                                                lpi.setProgressCompat(100, true);
                                                SharedPreferences pm = PreferenceManager.getDefaultSharedPreferences(c);
                                                SharedPreferences.Editor editor = pm.edit();
                                                editor.clear();
                                                editor.apply();
                                                Toast.makeText(c, "Your profile has been deleted successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(c, MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                c.startActivity(intent);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void comeBackTo(String activityName, String where, Context c, String putExtra) throws
            ClassNotFoundException {
        Intent i = new Intent(c, Class.forName(("com.example.ihelpou.activities." + activityName)));
        if (where.equals("edit")) {
            i.putExtra("where", "userfragment");
        } else if (where.equals("login")) {
            i.putExtra("error", putExtra);
        }
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        c.startActivity(i);
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
        String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        if (currentDate.equals(aid.getDay())) {
            Calendar calendar = Calendar.getInstance();
            DateFormat sdf = new SimpleDateFormat("HH:mm");
            String strDate = sdf.format(calendar.getTime());
            LocalTime startTime = LocalTime.parse(aid.getStartTime());
            LocalTime currentTime = LocalTime.parse(strDate);
            if (startTime.compareTo(currentTime) >= 0) {
                fsDB.collection("User").document(user.getEmail()).collection("Aid").document().set(aidMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(c, InitialActivity.class);
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
            } else {
                Toast.makeText(c, "This hour is not possible", Toast.LENGTH_SHORT).show();
            }
        } else {
            fsDB.collection("User").document(user.getEmail()).collection("Aid").document().set(aidMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Intent intent = new Intent(c, InitialActivity.class);
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
    }

    public void editAid(Aid aid, User user, Context c, String key) {
        Map<String, Object> aidMap = new HashMap<>();
        aidMap.put("description", aid.getDescription());
        aidMap.put("startTime", aid.getStartTime());
        aidMap.put("finishTime", aid.getFinishTime());
        aidMap.put("day", aid.getDay());
        String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        if (currentDate.equals(aid.getDay())) {
            Calendar calendar = Calendar.getInstance();
            DateFormat sdf = new SimpleDateFormat("HH:mm");
            String strDate = sdf.format(calendar.getTime());
            LocalTime startTime = LocalTime.parse(aid.getStartTime());
            LocalTime currentTime = LocalTime.parse(strDate);
            if (startTime.compareTo(currentTime) >= 0) {
                fsDB.collection("User").document(user.getEmail()).collection("Aid").document(key).update(aidMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Intent intent = new Intent(c, InitialActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        c.startActivity(intent);
                        Toast.makeText(c, "Aid edited successfully", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(c, "Error editing aid", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(c, "This hour is not possible", Toast.LENGTH_SHORT).show();
            }
        } else {
            fsDB.collection("User").document(user.getEmail()).collection("Aid").document(key).update(aidMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Intent intent = new Intent(c, InitialActivity.class);
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
    }

    public String getEmailActualUser(Context c) {
        return PreferenceManager.getDefaultSharedPreferences(c).getString("email", "");
    }

    public void getAids(ArrayList<Aid> listAids, RecyclerView listAidsRV, Context c, ImageButton deleteAidBtn, LinearLayout messageID) {
        messageID.setVisibility(View.VISIBLE);
        fsDB.collection("User").document(getEmailActualUser(c)).collection("Aid")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            listAids.clear();
                            for (QueryDocumentSnapshot objectAid : query.getResult()) {
                                deleteDoneAids(c);
                                Aid aid = objectAid.toObject(Aid.class);
                                aid.setKey(objectAid.getId());
                                listAids.add(aid);
                                adapter = new RecyclerAdapterAids(listAids, c, 'r', deleteAidBtn);
                                listAidsRV.setAdapter(adapter);
                                messageID.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            Toast.makeText(c, "Unexpected Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void deleteDoneAids(Context c) {
        fsDB.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> queryUsers) {
                        if (queryUsers.isSuccessful()) {
                            for (QueryDocumentSnapshot objectUser : queryUsers.getResult()) {
                                User user = objectUser.toObject(User.class);
                                user.setEmail(objectUser.getId());
                                fsDB.collection("User").document(user.getEmail()).collection("Aid")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> query) {
                                                if (query.isSuccessful()) {
                                                    for (QueryDocumentSnapshot objectAid : query.getResult()) {
                                                        Aid aid = objectAid.toObject(Aid.class);
                                                        aid.setKey(objectAid.getId());
                                                        String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                                                        if (currentDate.equals(aid.getDay()) && aid.getDone().equals("pending")) {
                                                            Calendar calendar = Calendar.getInstance();
                                                            DateFormat sdf = new SimpleDateFormat("HH:mm");
                                                            String strDate = sdf.format(calendar.getTime());
                                                            LocalTime finishTime = LocalTime.parse(aid.getFinishTime());
                                                            LocalTime currentTime = LocalTime.parse(strDate);
                                                            if (finishTime.compareTo(currentTime) <= 0) {
                                                                fsDB.collection("User").document(aid.getIdHelper()).collection("AvailableDay")
                                                                        .get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    for (QueryDocumentSnapshot objectAvailableDays : task.getResult()) {
                                                                                        ObjectMapper mapper = new ObjectMapper();
                                                                                        AvailableDays availableDays = new AvailableDays(objectAvailableDays.getId(), mapper.convertValue(objectAvailableDays.get("day"), ArrayList.class), String.valueOf(objectAvailableDays.get("startTime")), String.valueOf(objectAvailableDays.get("finishTime")));
                                                                                        Map<String, Object> availableDayMap = new HashMap<>();
                                                                                        for (int i = 0; i < availableDays.getAvailableDays().size(); i++) {
                                                                                            int finalI = i;
                                                                                            availableDays.getAvailableDays().get(i).forEach((s, o) -> {
                                                                                                if (o.equals(aid.getKey())) {
                                                                                                    availableDays.getAvailableDays().get(finalI).put("availability", "available");
                                                                                                    availableDays.getAvailableDays().get(finalI).put("idAid", "");
                                                                                                }
                                                                                            });
                                                                                            availableDayMap.put("day", availableDays.getAvailableDays());
                                                                                            availableDayMap.put("startTime", availableDays.getStartTime());
                                                                                            availableDayMap.put("finishTime", availableDays.getFinishTime());
                                                                                        }
                                                                                        fsDB.collection("User").document(aid.getIdHelper()).collection("AvailableDay").document(objectAvailableDays.getId()).delete();
                                                                                        fsDB.collection("User").document(aid.getIdHelper()).collection("AvailableDay").document(objectAvailableDays.getId()).set(availableDayMap);
                                                                                    }
                                                                                }
                                                                            }
                                                                        });
                                                                fsDB.collection("User").document(getEmailActualUser(c)).collection("Aid").document(aid.getKey()).delete();
                                                            }
                                                        }
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

    public void registerAvailableDay(AvailableDays availableDays, User user, Context c) {
        Map<String, Object> availableDayMap = new HashMap<>();
        availableDayMap.put("day", availableDays.getAvailableDays());
        availableDayMap.put("startTime", availableDays.getStartTime());
        availableDayMap.put("finishTime", availableDays.getFinishTime());
        fsDB.collection("User").document(user.getEmail()).collection("AvailableDay").document().set(availableDayMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Intent intent = new Intent(c, InitialActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("where", R.id.navigation_offer);
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
                Intent intent = new Intent(c, InitialActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("where", R.id.navigation_offer);
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
                                                        Intent intent = new Intent(c, InitialActivity.class);
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

    public void deleteAid(User user, Context c, HashMap<Integer, String> hashMapItems) {
        fsDB.collection("User").document(user.getEmail()).collection("Aid")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            Boolean haveDeleted = false;
                            int cont = 0;
                            for (QueryDocumentSnapshot objectAid : query.getResult()) {
                                Aid aid = objectAid.toObject(Aid.class);
                                aid.setKey(objectAid.getId());

                                if (hashMapItems.size() > 1) {
                                    if (hashMapItems.get(cont).equals("yes")) {
                                        fsDB.collection("User").document(user.getEmail()).collection("Aid").document(aid.getKey()).delete();
                                        haveDeleted = true;
                                    }
                                } else {
                                    if (cont == hashMapItems.entrySet().iterator().next().getKey()) {
                                        if (hashMapItems.get(cont).equals("yes")) {
                                            fsDB.collection("User").document(user.getEmail()).collection("Aid").document(aid.getKey()).delete();
                                            haveDeleted = true;
                                        }
                                    }
                                }
                                cont++;
                                if (cont == query.getResult().size() && haveDeleted) {
                                    Toast.makeText(c, "Your aid/s has been deleted successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(c, InitialActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    c.startActivity(intent);
                                }
                            }
                        }
                    }
                });
    }

    public void checkAvailability(ImageButton button, Context c) {
        fsDB.collection("User").document(getEmailActualUser(c)).collection("AvailableDay")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            if (query.getResult().size() == 0) {
                                Intent intent = new Intent(c, GestAvailableDaysActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                c.startActivity(intent);
                            } else {
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

    public void getHelpSeekerAccordingAid(Aid aid, Context c, String putButton) {
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
                                                            i.putExtra("user", user);
                                                            i.putExtra("aid", aid);
                                                            i.putExtra("where", "offer");
                                                            i.putExtra("putButton", putButton);
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


    public void getAidsAccordingAvailability(ArrayList<Aid> listAidsAvailables, RecyclerView
            listAidsAvailablesRV, Context c, ImageButton availabilityBtn, LinearLayout messageID) {
        messageID.setVisibility(View.VISIBLE);
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
                                                        fsDB.collection("User").document(getEmailActualUser(c)).collection("AvailableDay")
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
                                                                                                if (!getEmailActualUser(c).equals(objectUser.getId()) && aid.getDone().equals("no")) {
                                                                                                    Boolean check = false;
                                                                                                    for (int i = 0; i < listAidsAvailables.size(); i++) {
                                                                                                        if (listAidsAvailables.get(i).getKey().equals(aid.getKey())) {
                                                                                                            check = true;
                                                                                                        }
                                                                                                    }
                                                                                                    if (!check) {
                                                                                                        listAidsAvailables.add(aid);
                                                                                                        messageID.setVisibility(View.INVISIBLE);
                                                                                                    }
                                                                                                }
                                                                                            }
                                                                                        }
                                                                                    }
                                                                                    adapter = new RecyclerAdapterAids(listAidsAvailables, c, 'o', availabilityBtn);
                                                                                    listAidsAvailablesRV.setAdapter(adapter);
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

    public void checkHourAid(Aid aid, Context c) {
        fsDB.collection("User").document(getEmailActualUser(c)).collection("Aid").document(aid.getKey())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> queryAid) {
                        Aid aid = queryAid.getResult().toObject(Aid.class);
                        aid.setKey(queryAid.getResult().getId());
                        String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                        if (aid.getDay().compareTo(currentDate) > 0) {
                            checkHelpersAccordingAid(aid, c);
                        } else if (aid.getDay().compareTo(currentDate) == 0) {
                            Calendar calendar = Calendar.getInstance();
                            DateFormat sdf = new SimpleDateFormat("HH:mm");
                            String strDate = sdf.format(calendar.getTime());
                            LocalTime finishTime = LocalTime.parse(aid.getFinishTime());
                            LocalTime currentTime = LocalTime.parse(strDate);
                            if (finishTime.compareTo(currentTime) > 0) {
                                checkHelpersAccordingAid(aid, c);
                            } else {
                                Toast.makeText(c, "It is an old aid, delete or edit it", Toast.LENGTH_SHORT).show();
                            }
                        } else if (aid.getDay().compareTo(currentDate) < 0) {
                            Toast.makeText(c, "It is an old aid, delete or edit it", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    public void checkHelpersAccordingAid(Aid aid, Context c) {
        fsDB.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> queryUsers) {
                        if (queryUsers.isSuccessful()) {
                            final int[] i = {0};
                            final int[] i2 = {0};
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
                                                                        if (!getEmailActualUser(c).equals(objectUser.getId())) {
                                                                            Intent intent = new Intent(c, HelpersActivity.class);
                                                                            intent.putExtra("aid", aid);
                                                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                            c.startActivity(intent);
                                                                            return;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        i[0]++;
                                                        if (i[0] == cont && !control) {
                                                            Toast.makeText(c, "There isn't any available helper", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                i2[0]++;
                                if (cont < 2 && !control && i2[0] == cont) {
                                    Toast.makeText(c, "There isn't any available helper", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                });
    }


    public void getUsersWithAvailability() {
        fsDB.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> queryUsers) {
                        if (queryUsers.isSuccessful()) {
                            for (QueryDocumentSnapshot objectUser : queryUsers.getResult()) {
                                fsDB.collection("User").document(objectUser.getId()).collection("AvailableDay")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> query) {
                                                if (query.isSuccessful()) {
                                                    if (query.getResult().getDocuments().size() > 0) {
                                                        cont++;
                                                    }
                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }

    public void checkExistHelpersAccordingAid(Aid aid, Context c) {
        fsDB.collection("User")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> queryUsers) {
                        if (queryUsers.isSuccessful()) {
                            Iterator<QueryDocumentSnapshot> iterator = queryUsers.getResult().iterator();
                            for (QueryDocumentSnapshot objectUser : queryUsers.getResult()) {
                                iterator.next().getId();
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
                                                                        if (!getEmailActualUser(c).equals(objectUser.getId())) {
                                                                            control = true;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        });
                                if (!iterator.hasNext()) {
                                    checkHourAid(aid, c);
                                }
                            }
                        }
                    }
                });
    }

    public void getHelpersAccordingAid(ArrayList<User> listHelpersAvailables, RecyclerView
            listHelpersAvailablesRV, Aid aid, Context c) {
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
                                                                    if (!getEmailActualUser(c).equals(objectUser.getId())) {
                                                                        listHelpersAvailables.add(user);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        RecyclerAdapterHelper listHelpersAdapter = new RecyclerAdapterHelper(listHelpersAvailables, c, aid);
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


    public void showHelperAccordingAid(Aid aid, Context c) {
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

    public void setInfoHelper(User user, TextView startTime, TextView finishTime, ImageButton
            mondayBtn, ImageButton tuesdayBtn, ImageButton wednesdayBtn, ImageButton
                                      thursdayBtn, ImageButton fridayBtn, ImageButton saturdayBtn, ImageButton sundayBtn, TextView
                                      titleAvailabilityTV) {
        fsDB.collection("User").document(user.getEmail()).collection("AvailableDay")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> query) {
                        if (query.isSuccessful()) {
                            if (query.getResult().size() != 0) {
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
                            } else {
                                startTime.setVisibility(View.INVISIBLE);
                                finishTime.setVisibility(View.INVISIBLE);
                                mondayBtn.setVisibility(View.INVISIBLE);
                                tuesdayBtn.setVisibility(View.INVISIBLE);
                                wednesdayBtn.setVisibility(View.INVISIBLE);
                                thursdayBtn.setVisibility(View.INVISIBLE);
                                fridayBtn.setVisibility(View.INVISIBLE);
                                saturdayBtn.setVisibility(View.INVISIBLE);
                                sundayBtn.setVisibility(View.INVISIBLE);
                                titleAvailabilityTV.setVisibility(View.INVISIBLE);
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
                                                Intent intent = new Intent(c, InitialActivity.class);
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

    public void getPendingAids(ArrayList<Aid> listAids, RecyclerView listAidsRV, Context c, LinearLayout messageID) {
        messageID.setVisibility(View.VISIBLE);
        fsDB.collection("User").document(getEmailActualUser(c)).collection("AvailableDay")
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
                                                                                        messageID.setVisibility(View.INVISIBLE);
                                                                                        listAids.add(aid);
                                                                                        RecyclerAdapterAids listAidsAdapter = new RecyclerAdapterAids(listAids, c, 'p', null);
                                                                                        listAidsRV.setAdapter(listAidsAdapter);
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

    public void textChanges(EditText editText, TextInputLayout textInputLayout, Boolean
            check, String messageError) {
        if (!editText.getText().toString().equals("")) {
            if (!check) {
                textInputLayout.setErrorEnabled(true);
                textInputLayout.setError(messageError);
            } else {
                textInputLayout.setErrorEnabled(false);
                textInputLayout.setError("");
            }
        } else {
            textInputLayout.setErrorEnabled(false);
            textInputLayout.setError("");
        }
    }

    public void uploadImage(Uri uri, String email) {
        if (uri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Avatars")
                    .child(email);
            storageReference.putFile(uri);
        }
    }

    public void getImage(Uri nameFile, ImageView avatarIV) throws IOException {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Avatars/" + nameFile);
        File localFile = File.createTempFile("tempFile", "");
        storageReference.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                Bitmap bm = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                avatarIV.setImageBitmap(bm);
                if (bm == null) {
                    avatarIV.setImageResource(R.drawable.avatar);
                }
            }
        });
    }

    public void deleteImage(Uri nameFile) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference("Avatars/" + nameFile);
        storageReference.delete();
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
