package com.example.ihelpou.classes;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.ihelpou.R;
import com.example.ihelpou.activities.GestAidActivity;
import com.example.ihelpou.activities.GestAvailableDaysActivity;
import com.example.ihelpou.models.Aid;
import com.example.ihelpou.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class RecyclerAdapterAids extends RecyclerView.Adapter<RecyclerAdapterAids.AidsViewHolder> {

    private FirebaseFirestore fsDB = FirebaseFirestore.getInstance();
    private ArrayList<Aid> listAids;
    private Context c;
    private GestClassDB gestClassDB = new GestClassDB();
    private char who;
    private ImageButton imageButton;
    private HashMap<Integer, String> hashMapItems = new HashMap<>();
    private boolean control = false;
    private User user;

    public RecyclerAdapterAids(ArrayList<Aid> listAids, Context c, char who, ImageButton imageButton) {
        this.listAids = listAids;
        this.c = c;
        this.who = who;
        this.imageButton = imageButton;
        for (int i = 0; i < listAids.size(); i++) {
            if (!listAids.get(i).getDone().equals("pending")) {
                hashMapItems.put(i, "no");
            } else {
                hashMapItems.put(i, "pending");
            }
        }
        getUser(gestClassDB.getEmailActualUser(c));
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

    @NonNull
    @Override
    public AidsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AidsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.aids_list_row, null, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AidsViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (listAids.get(position).getDone().equals("pending")) {
            holder.editOrPendingAid.setImageResource(R.drawable.waitaid);
        }

        if (who == 'r' && !listAids.get(position).getDone().equals("pending")) {
            holder.editOrPendingAid.setImageResource(R.drawable.edit);
        }

        if (listAids.get(position).getDescription().length() >= 21)
            holder.descriptionTV.setText(listAids.get(position).getDescription().substring(0, 20) + "...");
        else
            holder.descriptionTV.setText(listAids.get(position).getDescription());

        holder.startTimeTV.setText("From: " + listAids.get(position).getStartTime());
        holder.finishTimeTV.setText("To: " + listAids.get(position).getFinishTime());
        holder.dayTV.setText("Day: " + listAids.get(position).getDay());

        try {
            Calendar calendar = Calendar.getInstance();
            Date aidDay = new SimpleDateFormat("dd/MM/yyyy").parse(listAids.get(position).getDay());
            calendar.setTime(aidDay);
            String dayOfWeek = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.US);
            holder.dayOfWeekTV.setText(dayOfWeek);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        switch (hashMapItems.get(position)) {
            case "yes":
                holder.itemCheckBox.setChecked(true);
                holder.itemCheckBox.setVisibility(View.VISIBLE);
                holder.editOrPendingAid.setVisibility(View.INVISIBLE);
                break;
            case "no":
                holder.itemCheckBox.setChecked(false);
                holder.itemCheckBox.setVisibility(View.VISIBLE);
                holder.editOrPendingAid.setVisibility(View.INVISIBLE);
                break;
            case "pending":
                holder.itemCheckBox.setChecked(false);
                holder.itemCheckBox.setVisibility(View.INVISIBLE);
                holder.editOrPendingAid.setVisibility(View.VISIBLE);
                break;
        }


            if (control) {
                if (!hashMapItems.get(position).equals("pending")) {
                    holder.itemCheckBox.setVisibility(View.VISIBLE);
                    holder.editOrPendingAid.setVisibility(View.INVISIBLE);
                }
                imageButton.setImageResource(R.drawable.delete);
            } else {
                holder.itemCheckBox.setVisibility(View.INVISIBLE);
                holder.editOrPendingAid.setVisibility(View.VISIBLE);
                if (who == 'r') {
                imageButton.setImageResource(R.drawable.add);
            }
        }


        holder.itemCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addStateHM(holder, position);
                updateAll();
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (who == 'r') {
                    if (checkState()) {
                        if (!hashMapItems.get(position).equals("pending")) {
                            holder.itemCheckBox.setChecked(!holder.itemCheckBox.isChecked());
                            addStateHM(holder, position);
                        }
                        updateAll();
                    } else {
                        if (!hashMapItems.get(position).equals("pending")) {
                            gestClassDB.showHelperAccordingAid(listAids.get(position), c);
                        } else {
                            gestClassDB.checkHelpersAccordingAid(listAids.get(position), c);
                        }
                    }
                } else if (who == 'o') {
                    gestClassDB.getHelpSeekerAccordingAid(listAids.get(position), c, null);
                } else {
                    gestClassDB.getHelpSeekerAccordingAid(listAids.get(position), c, "no");
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public boolean onLongClick(View v) {
                if (who == 'r') {
                    if (!hashMapItems.get(position).equals("pending")) {
                        holder.itemCheckBox.setChecked(!holder.itemCheckBox.isChecked());
                        addStateHM(holder, position);
                    }
                    updateAll();
                }
                return true;
            }
        });

        holder.editOrPendingAid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (who == 'r') {
                    if (holder.editOrPendingAid.getDrawable().getConstantState().equals(ResourcesCompat.getDrawable(c.getResources(), R.drawable.edit, null).getConstantState())) {
                        Intent intent = new Intent(c, GestAidActivity.class);
                        intent.putExtra("openEdit", "yes");
                        intent.putExtra("aid", listAids.get(position));
                        intent.putExtra("position", position);
                        c.startActivity(intent);
                    } else {
                        gestClassDB.showHelperAccordingAid(listAids.get(position), c);
                    }
                } else if (who == 'o') {
                    gestClassDB.getHelpSeekerAccordingAid(listAids.get(position), c, null);
                } else {
                    gestClassDB.getHelpSeekerAccordingAid(listAids.get(position), c, "no");
                }
            }
        });

        if (imageButton != null) {
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imageButton.getDrawable().getConstantState().equals(ResourcesCompat.getDrawable(c.getResources(), R.drawable.add, null).getConstantState())) {
                        if (who == 'r') {
                            Intent intent = new Intent(c, GestAidActivity.class);
                            c.startActivity(intent);
                        } else if (who == 'o') {
                            Intent intent = new Intent(c, GestAvailableDaysActivity.class);
                            intent.putExtra("user", user);
                            c.startActivity(intent);
                        }
                    } else if (imageButton.getDrawable().getConstantState().equals(ResourcesCompat.getDrawable(c.getResources(), R.drawable.delete, null).getConstantState())) {
                        int cont = 0;
                        for (int i = 0; i < hashMapItems.size(); i++) {
                            if (hashMapItems.get(i).equals("yes")) {
                                cont++;
                            }
                        }
                        if (cont > 1)
                            messageSure("You are going to delete more than one aid", hashMapItems);
                        else
                            messageSure("You are going to delete your aid", hashMapItems);
                    } else if (imageButton.getDrawable().getConstantState().equals(ResourcesCompat.getDrawable(c.getResources(), R.drawable.edit, null).getConstantState())) {
                        Intent intent = new Intent(c, GestAvailableDaysActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("openEdit", "yes");
                        gestClassDB.sendAvailability(user, intent, c);
                    }
                }
            });
        }
    }

    public boolean checkState() {
        Boolean checkState = false;
        for (int i = 0; i < hashMapItems.size(); i++) {
            if (hashMapItems.get(i).equals("yes")) {
                checkState = true;
            }
        }
        return checkState;
    }

    public void addStateHM(AidsViewHolder holder, int position) {
        if (holder.itemCheckBox.isChecked()) {
            hashMapItems.put(position, "yes");
        } else {
            hashMapItems.put(position, "no");
        }
    }

    public void updateAll() {
        hashMapItems.forEach((k, va) -> {
            if (!va.equals("yes") && !va.equals("pending"))
                hashMapItems.put(k, "no");
        });

        hashMapItems.forEach((k, va) -> {
            if (va.equals("yes")) {
                control = true;
            }
        });

        int i = -1;
        for (int j = 0; j < hashMapItems.size(); j++) {
            if (hashMapItems.get(j).equals("no") || hashMapItems.get(j).equals("pending")) {
                i++;
            }
            if (i == hashMapItems.size() - 1) {
                control = false;
            }
        }
        notifyDataSetChanged();
    }

    public void messageSure(String message, HashMap<Integer, String> hashMapItems) {
        AlertDialog.Builder alert = new AlertDialog.Builder(c);
        alert.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User user = new User(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                        gestClassDB.deleteAid(user, c, hashMapItems);
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

    @Override
    public int getItemCount() {
        return listAids.size();
    }


    class AidsViewHolder extends RecyclerView.ViewHolder {

        TextView descriptionTV, startTimeTV, finishTimeTV, dayTV, dayOfWeekTV;
        ImageView pictureIV, editOrPendingAid;
        CheckBox itemCheckBox;

        public AidsViewHolder(View itemView) {
            super(itemView);

            descriptionTV = itemView.findViewById(R.id.descriptionTV);
            startTimeTV = itemView.findViewById(R.id.startTimeTV);
            finishTimeTV = itemView.findViewById(R.id.finishTimeTV);
            dayTV = itemView.findViewById(R.id.dayTV);
            dayOfWeekTV = itemView.findViewById(R.id.dayOfWeekTV);
            pictureIV = itemView.findViewById(R.id.pictureIV);
            pictureIV.setImageResource(R.drawable.help);
            editOrPendingAid = itemView.findViewById(R.id.editOrPendingAid);
            itemCheckBox = itemView.findViewById(R.id.itemCheckBox);
        }
    }
}
